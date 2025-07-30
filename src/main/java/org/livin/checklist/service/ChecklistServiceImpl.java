package org.livin.checklist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.entity.ChecklistVO;
import org.livin.checklist.mapper.ChecklistMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

	final ChecklistMapper checklistMapper;

	@Override
	public List<ChecklistDTO> getAllList(Long userId) {
		List<ChecklistVO> voList = checklistMapper.getAllList(userId);
		return voList.stream()
			.map(ChecklistDTO::of)
			.collect(Collectors.toList());
	}

	@Override
	public ChecklistDTO createChecklist(ChecklistCreateRequestDTO checklistDto, Long userId) {
		// DTO -> VO 변환
		ChecklistVO checklistVO = ChecklistVO.builder()
			.userId(userId)
			.title(checklistDto.getTitle())
			.description(checklistDto.getDescription())
			.type(checklistDto.getType())
			.createdAt(LocalDateTime.now())
			.updateAt(LocalDateTime.now())
			.build();

		log.info("============> request VO: {}", checklistVO);

		// DB insert 작업
		try {
			checklistMapper.create(checklistVO);
			log.info("✅ 생성된 checklistId: {}", checklistVO.getChecklistId());
		} catch (Exception e) {
			log.error("============> 체크리스트 생성 중 에러 발생", e);
			throw new RuntimeException("체크리스트 생성 실패", e); // 예외를 던져야 controller에서 500 처리 가능
		}
		// VO → DTO 변환 후 반환
		return ChecklistDTO.of(checklistVO);
	}
}
