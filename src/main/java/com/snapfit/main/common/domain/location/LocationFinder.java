package com.snapfit.main.common.domain.location;

import com.snapfit.main.common.exception.ErrorResponse;
import com.snapfit.main.common.exception.enums.CommonErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocationFinder {

    private final LocationRepository locationRepository;
    private final Map<String, Location> adminNameCache = new HashMap<>();
    private final Map<Long, Location> idCache = new HashMap<>();


    @PostConstruct
    void init() {
        locationRepository.findAll().subscribe(fetchLocation -> adminNameCache.put(fetchLocation.getAdminName().toLowerCase(), fetchLocation));
        locationRepository.findAll().subscribe(fetchLocation -> idCache.put(fetchLocation.getId(), fetchLocation));

    }

    public Location findByAdminName(String adminName) {
        Location mappingLocation = adminNameCache.get(adminName.toLowerCase());

        if (mappingLocation == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingLocation;
    }

    public Location findById(Long id) {
        Location mappingLocation = idCache.get(id);

        if (mappingLocation == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingLocation;
    }

    public List<Location> findAllLocation() {
        return adminNameCache.values().stream().toList();
    }
}
