package iuh.fit.se.tramcamxuc.modules.marketing.service;

import iuh.fit.se.tramcamxuc.modules.marketing.dto.request.CreateAdRequest;
import iuh.fit.se.tramcamxuc.modules.marketing.entity.Advertisement;

public interface AdvertisementService {
    Advertisement createAd(CreateAdRequest request);

    Advertisement getRandomAudioAd();
}
