package iuh.fit.se.tramcamxuc.common.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadAvatar(MultipartFile file, String userId) {
        File tempFile = null;
        try {
            Path tempPath = Files.createTempFile("avatar_" + userId, "_" + UUID.randomUUID().toString());
            tempFile = tempPath.toFile();

            file.transferTo(tempFile);

            String publicId = "avatar_" + userId;
            Map params = ObjectUtils.asMap(
                    "folder", "tramcamxuc/avatars",
                    "public_id", publicId,
                    "overwrite", true,
                    "invalidate", true,
                    "resource_type", "image",
                    "transformation", new Transformation()
                            .width(500).height(500).crop("fill").gravity("face")
            );

            Map uploadResult = cloudinary.uploader().upload(tempFile, params);

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    System.err.println("Không thể xóa file tạm: " + tempFile.getAbsolutePath());
                }
            }
        }
    }

    public String uploadImage(MultipartFile file, String folderName) {
        try {
            File tempFile = Files.createTempFile("upload_", UUID.randomUUID().toString()).toFile();
            file.transferTo(tempFile);

            Map params = ObjectUtils.asMap(
                    "folder", folderName,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(tempFile, params);

            tempFile.delete();

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload ảnh: " + e.getMessage());
        }
    }

    public void deleteAvatar(String userId) {
        try {
            String publicId = "phazelsound/avatars/avatar_" + userId;
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Lỗi xóa ảnh cũ: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        try {

            if (imageUrl == null) return;

            String[] parts = imageUrl.split("/");
            String publicIdWithExtension = "";

            int uploadIndex = imageUrl.indexOf("upload/");
            if (uploadIndex != -1) {
                String suffix = imageUrl.substring(uploadIndex + 7);
                if (suffix.startsWith("v")) {
                    int slashIndex = suffix.indexOf("/");
                    if (slashIndex != -1) {
                        suffix = suffix.substring(slashIndex + 1);
                    }
                }
                publicIdWithExtension = suffix;
            }

            String publicId = publicIdWithExtension;
            if (publicId.contains(".")) {
                publicId = publicId.substring(0, publicId.lastIndexOf("."));
            }

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Đã xóa ảnh rác Cloudinary: {}", publicId);

        } catch (Exception e) {
            log.error("Lỗi xóa ảnh Cloudinary: {}", e.getMessage());
        }
    }

    public String uploadImageFromUrl(String imageUrl, String folderName) {
        try {
            Map params = ObjectUtils.asMap(
                    "folder", folderName,
                    "resource_type", "image"
            );
            Map uploadResult = cloudinary.uploader().upload(imageUrl, params);

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            log.error("Lỗi sync ảnh từ URL: {}", e.getMessage());
            return null;
        }
    }
}