package org.livin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FavoriteProperty {
    // 유저와 매물 사이 허브(?) 역할
    private Long userId;                 // FK - User

    // 회원 id에 해당하는 등록된 관심 매물들을 리스트로 묶어져 있음.
    private List<Long> favoritePropertyIds;
}
