package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.service.CloudinaryService;
import iuh.fit.se.tramcamxuc.common.service.MinioService;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import iuh.fit.se.tramcamxuc.modules.music.artist.repository.ArtistRepository;
import iuh.fit.se.tramcamxuc.modules.music.genre.entity.Genre;
import iuh.fit.se.tramcamxuc.modules.music.genre.repository.GenreRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.CreateSongRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.request.UpdateSongRequest;
import iuh.fit.se.tramcamxuc.modules.music.song.dto.response.SongResponse;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import iuh.fit.se.tramcamxuc.modules.music.song.service.SongService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SongServiceImpl implements SongService {
    private final MinioService minioService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String SONG_VIEW_COUNT_KEY = "song:view:buffer";

    @Override
    @Transactional
    public SongResponse uploadSong(CreateSongRequest request) {
        User currentUser = userService.getCurrentUser();

        Artist artist = artistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException("You must be registered as an artist to upload songs."));

        if(request.getAudioFile() == null || request.getAudioFile().isEmpty()) {
            throw new AppException("Audio file is required.");
        }

        int duration = getDurationFromMultipartFile(request.getAudioFile());
        String slug = toSlug(request.getTitle()) + "-" + UUID.randomUUID().toString().substring(0, 6);

        File tempFile;
        try {
            tempFile = File.createTempFile("upload_", request.getAudioFile().getOriginalFilename());
            request.getAudioFile().transferTo(tempFile);
        } catch (IOException e) {
            throw new AppException("Lỗi khi xử lý file nhạc: " + e.getMessage());
        }

        CompletableFuture<String> audioUploadFuture = minioService.uploadMusicFileAsync(tempFile, request.getAudioFile().getContentType(), request.getAudioFile().getOriginalFilename());

        CompletableFuture<String> coverUploadFuture = CompletableFuture.completedFuture(null);
        if (request.getCoverFile() != null && !request.getCoverFile().isEmpty()) {
            coverUploadFuture = cloudinaryService.uploadImageAsync(request.getCoverFile(), "tramcamxuc/covers");
        }

        CompletableFuture.allOf(audioUploadFuture, coverUploadFuture).join();
        String audioUrl = audioUploadFuture.join();
        String coverUrl = coverUploadFuture.join();

        Set<Genre> genres = new HashSet<>();
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            genres.addAll(genreRepository.findAllById(request.getGenreIds()));
        }

        Song song = Song.builder()
                .title(request.getTitle())
                .slug(slug)
                .lyric(request.getLyric())
                .audioUrl(audioUrl)
                .coverUrl(coverUrl)
                .duration(duration)
                .status(SongStatus.DRAFT)
                .listeningCount(0L)
                .artist(artist)
                .genres(genres)
                .build();

        Song savedSong = songRepository.save(song);
        return SongResponse.fromEntity(savedSong);
    }

    @Override
    @Transactional
    public SongResponse updateSong(UUID songId, UpdateSongRequest request) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException("The song does not exist"));

        User currentUser = userService.getCurrentUser();
        Artist currentArtist = artistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException("You don't have artist permissions"));

        if (!song.getArtist().getId().equals(currentArtist.getId())) {
            throw new AppException("You don't have permission to update this song");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            song.setTitle(request.getTitle());
            song.setSlug(toSlug(request.getTitle()) + "-" + UUID.randomUUID().toString().substring(0, 6));
        }

        if (request.getLyric() != null) {
            song.setLyric(request.getLyric());
        }

        if (request.getGenreIds() != null) {
            var genres = genreRepository.findAllById(request.getGenreIds());
            song.setGenres(new HashSet<>(genres));
        }

        if (request.getCoverFile() != null && !request.getCoverFile().isEmpty()) {
            String oldCoverUrl = song.getCoverUrl();

            String newCoverUrl = cloudinaryService.uploadImageAsync(request.getCoverFile(), "tramcamxuc/covers").join();

            song.setCoverUrl(newCoverUrl);

            if (oldCoverUrl != null && !oldCoverUrl.isBlank()) {
                cloudinaryService.deleteImage(oldCoverUrl);
            }
        }
        Song updatedSong = songRepository.save(song);
        return SongResponse.fromEntity(updatedSong);
    }

    @Override
    @Transactional
    public SongResponse changeSongStatus(UUID songId, SongStatus newStatus) {
        Song song = getSongAndCheckOwner(songId);

        SongStatus currentStatus = song.getStatus();

        if (currentStatus == newStatus) return SongResponse.fromEntity(song);

        if (currentStatus == SongStatus.PUBLIC && newStatus == SongStatus.DRAFT) {
            throw new AppException("The song is already public and cannot be changed directly to draft. If you want to change it to draft, please change it to private first.");
        }

        if (newStatus == SongStatus.PUBLIC) {
            if (song.isVerified()) {
                song.setStatus(SongStatus.PUBLIC);
            } else {
                song.setStatus(SongStatus.PENDING_APPROVAL);
            }
        }
        else {
            song.setStatus(newStatus);
        }

        Song savedSong = songRepository.save(song);
        return SongResponse.fromEntity(savedSong);
    }

    @Override
    public void incrementListeningCount(UUID songId) {
        redisTemplate.opsForHash().increment(SONG_VIEW_COUNT_KEY, songId.toString(), 1);
    }

    // For ADMIN
    @Override
    public Page<SongResponse> getPendingSongs(Pageable pageable) {
        Page<Song> songs = songRepository.findByStatus(SongStatus.PENDING_APPROVAL, pageable);

        return songs.map(SongResponse::fromEntity);
    }
    @Override
    @Transactional
    public void approveSong(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException("The song does not exist"));

        if (song.getStatus() != SongStatus.PENDING_APPROVAL) {
            throw new AppException("This song is not in pending status ( Current Status: " + song.getStatus() + ")");
        }

        song.setStatus(SongStatus.PUBLIC);
        song.setVerified(true);
        songRepository.save(song);

        log.info("Admin approved song: {}", song.getTitle());
    }

    @Override
    @Transactional
    public void rejectSong(UUID songId, String reason) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException("The song does not exist"));

        song.setStatus(SongStatus.REJECTED);

        // TODO: Gửi email thông báo lý do cho Artist (Làm sau)
        log.info("Rejected song {}. Reason: {}", song.getTitle(), reason);

        songRepository.save(song);
    }

    //Helper methods
    private int getDurationFromMultipartFile(MultipartFile file) {
        File tempFile = null;
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) throw new AppException("Tên file lỗi");

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!Set.of(".mp3", ".wav", ".flac", ".m4a", ".mp4", ".ogg").contains(extension)) {
                throw new AppException("Định dạng file không hỗ trợ: " + extension);
            }

            tempFile = File.createTempFile("upload_" + UUID.randomUUID(), extension);
            try (var is = file.getInputStream()) {
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            if (".mp4".equals(extension) || ".m4a".equals(extension)) {
                try (IsoFile isoFile = new IsoFile(tempFile)) {
                    MovieHeaderBox mvhd = isoFile.getMovieBox().getMovieHeaderBox();
                    return (int) (mvhd.getDuration() / mvhd.getTimescale());
                }
            } else {
                AudioFile audioFile = AudioFileIO.read(tempFile);
                return audioFile.getAudioHeader().getTrackLength();
            }

        } catch (Exception e) {
            log.error("Lỗi đọc duration: {}", e.getMessage());
            throw new AppException("Không thể đọc thông tin file nhạc: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private String toSlug(String input) {
            String nowhitespace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
            String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
            String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
            return slug.toLowerCase(Locale.ENGLISH);
        }

    private Song getSongAndCheckOwner(UUID songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException("The song does not exist"));

        var currentUser = userService.getCurrentUser();
        Artist currentArtist = artistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException("You don't have artist permissions"));

        if (!song.getArtist().getId().equals(currentArtist.getId())) {
            throw new AppException("You don't have permission to modify this song");
        }
        return song;
    }
}
