package iuh.fit.se.tramcamxuc.modules.music.artist.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.music.artist.dto.request.CreateArtistRequest;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.Artist;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.ArtistFollow;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.ArtistFollowId;
import iuh.fit.se.tramcamxuc.modules.music.artist.entity.enums.ArtistStatus;
import iuh.fit.se.tramcamxuc.modules.music.artist.repository.ArtistFollowRepository;
import iuh.fit.se.tramcamxuc.modules.music.artist.repository.ArtistRepository;
import iuh.fit.se.tramcamxuc.modules.music.artist.service.ArtistService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistFollowRepository artistFollowRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Artist createSystemArtist(CreateArtistRequest request) {
        if (artistRepository.existsByName(request.getName())) {
            throw new AppException("This artist name already exists.");
        }

        Artist artist = Artist.builder()
                .name(request.getName())
                .bio(request.getBio())
                .imageUrl(request.getImageUrl())
                .slug(toSlug(request.getName()))
                .status(ArtistStatus.ACTIVE)
                .isVerified(true)
                .user(null)
                .build();

        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public Artist registerAsArtist(CreateArtistRequest request) {
        User currentUser = userService.getCurrentUser();

        if (artistRepository.findByUserId(currentUser.getId()).isPresent()) {
            throw new AppException("This account is already registered as an artist.");
        }

        Artist artist = Artist.builder()
                .name(request.getName())
                .bio(request.getBio())
                .imageUrl(request.getImageUrl())
                .slug(toSlug(request.getName()))
                .status(ArtistStatus.ACTIVE)
                .isVerified(false)
                .user(currentUser)
                .build();

        return artistRepository.save(artist);
    }

    @Override
    @Transactional
    public void followArtist(UUID artistId) {
        User currentUser = userService.getCurrentUser();
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        if (artistFollowRepository.existsByUserIdAndArtistId(currentUser.getId(), artistId)) {
            throw new AppException("Follow successful!");
        }

        ArtistFollow follow = ArtistFollow.builder()
                .id(new ArtistFollowId(currentUser.getId(), artistId))
                .user(currentUser)
                .artist(artist)
                .build();

        artistFollowRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowArtist(UUID artistId) {
        User currentUser = userService.getCurrentUser();
        ArtistFollowId id = new ArtistFollowId(currentUser.getId(), artistId);

        if (!artistFollowRepository.existsById(id)) {
            throw new AppException("You are not following this artist.");
        }
        artistFollowRepository.deleteById(id);
    }

    private String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
