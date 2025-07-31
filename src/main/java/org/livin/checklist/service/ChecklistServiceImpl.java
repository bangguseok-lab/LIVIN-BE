package org.livin.checklist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistItemJoinDTO;
import org.livin.checklist.entity.ChecklistVO;
import org.livin.checklist.mapper.ChecklistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

	final ChecklistMapper checklistMapper;

	// 체크리스트 전체 목록 조회
	@Override
	public List<ChecklistDTO> getAllList(Long userId) {
		List<ChecklistVO> voList = checklistMapper.getAllList(userId);
		return voList.stream()
			.map(ChecklistDTO::of)
			.collect(Collectors.toList());
	}

	// 체크리스트 상세 조회
	@Override
	public ChecklistDetailDTO getChecklistDetail(Long checklistId) {
		List<ChecklistItemJoinDTO> joinRows = checklistMapper.getChecklistDetail(checklistId);
		return ChecklistDetailDTO.from(joinRows);
	}

	// 체크리스트 생성
	@Transactional    // 전체 과정이 하나의 트랜잭션으로 처리되도록 보장하는 어노테이션
	@Override
	public ChecklistDetailDTO createChecklist(ChecklistCreateRequestDTO checklistDto, Long userId) {
		// DTO -> VO 변환
		ChecklistVO checklistVO = checklistDto.toVo(userId);

		log.info("============> request VO: {}", checklistVO);

		try {
			// Checklist 생성
			checklistMapper.create(checklistVO);
			Long checklistId = checklistVO.getChecklistId();    // 생성된 checklistId 추출
			log.info("✅ 생성된 checklistId: {}", checklistId);

			// ChecklistItem 기본 아이템 생성
			checklistMapper.createChecklistDefaultItem(checklistId);

			// Checklist + ChecklistItem 테이블 조인해서 상세정보 함께 반환
			List<ChecklistItemJoinDTO> joinList = checklistMapper.getChecklistDetail(checklistId);
			ChecklistDetailDTO resultDto = ChecklistDetailDTO.from(joinList);

			log.info("✅ 생성된 resultDTO: {}", resultDto);

			return resultDto;

		} catch (Exception e) {
			log.error("============> 체크리스트 생성 중 에러 발생", e);
			throw new RuntimeException("체크리스트 생성 실패", e); // 예외를 던져야 controller에서 500 처리 가능
		}
	}
}
