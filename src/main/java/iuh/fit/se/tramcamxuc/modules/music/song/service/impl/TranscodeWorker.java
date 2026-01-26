package iuh.fit.se.tramcamxuc.modules.music.song.service.impl;

import iuh.fit.se.tramcamxuc.common.service.MinioService;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.Song;
import iuh.fit.se.tramcamxuc.modules.music.song.entity.enums.SongStatus;
import iuh.fit.se.tramcamxuc.modules.music.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils; // Class này giúp xóa folder đệ quy

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscodeWorker {

    private final StringRedisTemplate redisTemplate;
    private final SongRepository songRepository;
    private final MinioService minioService;

    // Key Queue trong Redis
    private static final String QUEUE_KEY = "music:transcode:queue";

    @Scheduled(fixedDelay = 5000) // Chạy mỗi 5 giây
    public void processTranscode() {
        // 1. Lấy job từ Redis
        String songIdStr = redisTemplate.opsForList().rightPop(QUEUE_KEY);
        if (songIdStr == null) return; // Queue rỗng thì nghỉ

        log.info(">>> [WORKER] Bắt đầu Transcode bài hát ID: {}", songIdStr);

        File tempInDir = null;
        File tempOutDir = null;

        try {
            UUID songId = UUID.fromString(songIdStr);
            Song song = songRepository.findById(songId).orElse(null);

            if (song == null) {
                log.warn("Bài hát không tồn tại trong DB, bỏ qua: {}", songId);
                return;
            }

            // 2. Tạo thư mục tạm trên ổ cứng Server
            tempInDir = Files.createTempDirectory("transcode_in_" + songId).toFile();
            tempOutDir = Files.createTempDirectory("transcode_out_" + songId).toFile();

            // 3. Tải file MP3 gốc từ MinIO về tempInDir
            log.info("Đang tải file gốc về...");
            File inputFile = minioService.downloadFile(song.getAudioUrl(), tempInDir);

            // 4. Chạy FFmpeg để convert sang HLS
            log.info("Đang chạy FFmpeg...");
            String outputFileName = "playlist.m3u8";
            File outputFile = new File(tempOutDir, outputFileName);

            // Lệnh FFmpeg: Chuyển đổi sang HLS, mỗi segment 10s
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-c:a", "aac", "-b:a", "128k", // Audio codec AAC, bitrate 128k
                    "-hls_time", "10",            // Mỗi đoạn dài 10s
                    "-hls_list_size", "0",        // Giữ lại tất cả các đoạn trong playlist
                    "-f", "hls",
                    outputFile.getAbsolutePath()
            );

            pb.redirectErrorStream(true); // Gộp log lỗi để debug nếu cần
            Process process = pb.start();

            // Đợi tối đa 5 phút cho 1 bài, tránh treo server
            boolean finished = process.waitFor(5, TimeUnit.MINUTES);

            if (!finished || process.exitValue() != 0) {
                throw new RuntimeException("FFmpeg lỗi hoặc timeout!");
            }

            // 5. Upload folder kết quả (HLS) lên MinIO
            log.info("FFmpeg xong. Đang upload HLS lên MinIO...");
            String remotePrefix = "hls/" + songId; // Folder trên MinIO
            String hlsUrl = minioService.uploadFolder(tempOutDir, remotePrefix);

            if (hlsUrl == null) throw new RuntimeException("Lỗi không lấy được URL playlist");

            // 6. Cập nhật vào DB: Đổi link mp3 thành link m3u8 và set Public
            updateSongStatus(songId, hlsUrl);

            log.info(">>> [SUCCESS] Transcode xong bài: {}", songId);

        } catch (Exception e) {
            log.error(">>> [ERROR] Lỗi Transcode bài {}: {}", songIdStr, e.getMessage());
            // TODO: Ở đây có thể push lại vào Redis (Dead Letter Queue) để retry sau
        } finally {
            // 7. QUAN TRỌNG: Dọn dẹp rác trên ổ cứng
            // FileSystemUtils.deleteRecursively xóa cả file con lẫn thư mục cha
            if (tempInDir != null && tempInDir.exists()) {
                FileSystemUtils.deleteRecursively(tempInDir);
            }
            if (tempOutDir != null && tempOutDir.exists()) {
                FileSystemUtils.deleteRecursively(tempOutDir);
            }
            log.info("Đã dọn dẹp file tạm.");
        }
    }

    @Transactional
    public void updateSongStatus(UUID songId, String hlsUrl) {
        Song song = songRepository.findById(songId).orElseThrow();
        song.setAudioUrl(hlsUrl);      // Lưu link .m3u8 vào
        song.setStatus(SongStatus.PUBLIC); // Đổi trạng thái để User thấy bài này
        songRepository.save(song);
    }
}