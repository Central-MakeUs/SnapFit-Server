package com.snapfit.main.common.domain.vibe;

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
public class VibeFinder {

    private final VibeRepository vibeRepository;
    private final Map<String, Vibe> vibeCache = new HashMap<>();
    private final Map<Long, Vibe> idCache = new HashMap<>();


    @PostConstruct
    public void init() {
        vibeRepository.findAll().subscribe(fetchVibe -> vibeCache.put(fetchVibe.getName().toLowerCase(), fetchVibe));
        vibeRepository.findAll().subscribe(fetchVibe -> idCache.put(fetchVibe.getId(), fetchVibe));

    }


    public Vibe findByVibe(String vibe) {
        Vibe mappingVibe = vibeCache.get(vibe.toLowerCase());

        if (mappingVibe == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingVibe;
    }

    public Vibe findById(long id) {
        Vibe mappingVibe = idCache.get(id);

        if (mappingVibe == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingVibe;
    }

    public List<Vibe> findAllVibes() {
        return vibeCache.values().stream().toList();
    }


}
