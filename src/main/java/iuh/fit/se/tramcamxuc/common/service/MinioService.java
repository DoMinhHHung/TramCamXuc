package iuh.fit.se.tramcamxuc.common.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.music}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Async
    public CompletableFuture<String> uploadMusicFileAsync(File file, String contentType, String originalFilename) {
        return CompletableFuture.supplyAsync(() -> {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                String fileName = UUID.randomUUID().toString() + "_" + originalFilename.replaceAll("\\s+", "_");

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.length(), -1)
                                .contentType(contentType)
                                .build()
                );

                String fileUrl = String.format("%s/%s/%s", minioUrl, bucketName, fileName);
                log.info("Upload nhạc thành công: {}", fileUrl);
                return fileUrl;

            } catch (Exception e) {
                log.error("Lỗi upload nhạc: {}", e.getMessage());
                throw new RuntimeException("Upload failed");
            } finally {
                if (file != null && file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) log.warn("Không thể xóa file tạm: {}", file.getAbsolutePath());
                }
            }
        });
    }

    @Async
    public void deleteFileAsync(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("Đã xóa file nhạc trên MinIO: {}", fileName);
        } catch (Exception e) {
            log.error("Lỗi xóa file MinIO (url={}): {}", fileUrl, e.getMessage());
        }
    }
}