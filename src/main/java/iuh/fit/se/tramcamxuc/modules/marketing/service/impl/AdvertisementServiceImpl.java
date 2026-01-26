package iuh.fit.se.tramcamxuc.modules.marketing.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.service.CloudinaryService;
import iuh.fit.se.tramcamxuc.common.service.MinioService;
import iuh.fit.se.tramcamxuc.modules.marketing.dto.request.CreateAdRequest;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.Advertisement;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.enums.AdType;
import iuh.fit.se.tramcamxuc.modules.marketing.repository.AdvertisementRepository;
import iuh.fit.se.tramcamxuc.modules.marketing.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdvertisementServiceImpl implements AdvertisementService {
    private final CloudinaryService cloudinaryService;
    private final MinioService minioService;
    private final AdvertisementRepository adRepository;

    @Override
    @Transactional
    public Advertisement createAd(CreateAdRequest request) {
        String mediaUrl;

        if (request.getAdType() == AdType.AUDIO) {
            File tempFile = convertMultiPartToFile(request.getFile());
            try {
                mediaUrl = minioService.uploadMusicFileAsync(tempFile, request.getFile().getContentType(), request.getFile().getOriginalFilename()).join();
            } catch (Exception e) {
                throw new AppException("Lỗi upload Audio lên MinIO: " + e.getMessage());
            } finally {
                if (tempFile.exists()) tempFile.delete();
            }
        } else {
            String folder = "tramcamxuc/ads/images";
            mediaUrl = cloudinaryService.uploadImageAsync(request.getFile(), folder).join();
        }

        Advertisement ad = Advertisement.builder()
                .title(request.getTitle())
                .mediaUrl(mediaUrl)
                .targetUrl(request.getTargetUrl())
                .adType(request.getAdType())
                .durationSeconds(request.getDurationSeconds())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority())
                .isActive(true)
                .build();

        return adRepository.save(ad);
    }

    @Override
    public Advertisement getRandomAudioAd() {
        return adRepository.findRandomActiveAudioAd().orElse(null);
    }

    private File convertMultiPartToFile(MultipartFile file) {
        try {
            File convFile = File.createTempFile("ad_upload_", file.getOriginalFilename());
            try (var is = file.getInputStream()) {
                Files.copy(is, convFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return convFile;
        } catch (IOException e) {
            throw new AppException("Lỗi xử lý file: " + e.getMessage());
        }
    }
}