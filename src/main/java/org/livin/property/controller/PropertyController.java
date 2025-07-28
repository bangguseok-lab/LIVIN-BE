package org.livin.property.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.property.dto.AddressDTO;
import org.livin.property.dto.PropertyNearLocationDTO;
import org.livin.property.dto.PropertyWithImageDTO;
import org.livin.property.service.PropertyService;
import org.livin.user.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class PropertyController {

    private final PropertyService propertyService;
    private final UserMapper userMapper;

    // 2) 관심 매물 조회
    @GetMapping("/users/favorite")
    public ResponseEntity<?> getFavoriteProperties(
            @RequestParam String providerId,
            @RequestParam(defaultValue = "3") int limit
    ) {
        log.info("providerId = {}로 관심 매물 요청, limit = {}만큼 매물 정보 전달", providerId, limit);

        List<PropertyWithImageDTO> result = propertyService.getFavoritePropertiesForMain(providerId, limit);
        log.info("회원 {}의 관심 매물 {}건 조회 완료", providerId, result.size());

        return ResponseEntity.ok(result);
    }


    @GetMapping("/properties")
    public ResponseEntity<List<PropertyNearLocationDTO>> getNearbyProperties(AddressDTO address) {
        List<PropertyNearLocationDTO> result = propertyService.getSimplePropertiesNearLocation(address);

        return ResponseEntity.ok(result);
    }

}