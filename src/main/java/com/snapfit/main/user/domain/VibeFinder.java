package com.snapfit.main.user.domain;

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
    private Map<String, Vibe> vibes;


    @PostConstruct
    public void init() {
        vibes = new HashMap<>();
        vibeRepository.findAll().subscribe(fetchVibe -> vibes.put(fetchVibe.getName(), fetchVibe));
    }


    public Vibe findByVibe(String vibe) {
        Vibe mappingVibe = vibes.get(vibe);

        if (mappingVibe == null) {
            throw new ErrorResponse(CommonErrorCode.INVALID_REQUEST);
        }

        return mappingVibe;
    }

    public List<Vibe> findAllVibes() {
        return vibes.values().stream().toList();
    }


}
