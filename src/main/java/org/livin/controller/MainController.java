package org.livin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.livin.dto.*;
import org.livin.mapper.UserMapper;
import org.livin.service.MainService;
import org.livin.vo.Property;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class MainController {

    private final MainService mainService;
    private final UserMapper userMapper;

    // 1) 로그인 이후 진입한 메인 페이지
    @GetMapping("/users")
    //    provider_id를 쿼리문으로 전달 받아 처리.
    public ResponseEntity<UserNicknameDTO> getUserNickname(@RequestParam("providerId") String providerId) {
        log.info("getUserNickname: " + providerId);

        UserNicknameDTO userNicknameDTO = mainService.getUserNickname(providerId);

        return ResponseEntity.ok(userNicknameDTO);
    }


    // 2) 관심 매물 조회
    @GetMapping("/users/favorite")
    public ResponseEntity<?> getFavoriteProperties(
            @RequestParam String providerId,
            @RequestParam(defaultValue = "3") int limit
    ) {
        log.info("providerId = {}로 관심 매물 요청, limit = {}만큼 매물 정보 전달", providerId, limit);

        List<PropertyWithImageDTO> result = mainService.getFavoritePropertiesForMain(providerId, limit);
        log.info("회원 {}의 관심 매물 {}건 조회 완료", providerId, result.size());

        return ResponseEntity.ok(result);
    }


    @GetMapping("/properties")
    public ResponseEntity<List<PropertyNearLocationDTO>> getNearbyProperties(
            @RequestParam("sido") String sido,
            @RequestParam("sigungu") String sigungu,
            @RequestParam("eupmyendong") String eupmyendong
    ) {
        List<PropertyNearLocationDTO> result = mainService.getSimplePropertiesNearLocation(sido, sigungu, eupmyendong);

        return ResponseEntity.ok(result);
    }

}