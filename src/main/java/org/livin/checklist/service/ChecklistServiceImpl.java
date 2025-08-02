package org.livin.checklist.service;

import java.util.List;
import java.util.stream.Collectors;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistItemJoinDTO;
import org.livin.checklist.dto.ChecklistListResponseDTO;
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
	public ChecklistListResponseDTO getAllList(Long userId, Long lastId, int size) {

		// 뒤에 체크리스트가 더 남았는지 확인하기 위해서 1개 더 많이 요청하고 실제 반환할 때는 앞에서 부터 5개만 반환
		List<ChecklistVO> voList = checklistMapper.getAllList(userId, lastId, size + 1);
		boolean hasNext = voList.size() > size;		// 더 조회될 체크리스트가 남았는 지 체크할 변수, isLast

		// 아직 마지막 페이지가 아닐 때
		if(hasNext) {
			voList = voList.subList(0, size); // 초과한 1개 제거
		}

		List<ChecklistDTO> dtoList = voList.stream()
			.map(ChecklistDTO::of)
			.collect(Collectors.toList());

		return new ChecklistListResponseDTO(dtoList, !hasNext);
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

			return resultDto;

		} catch (Exception e) {
			log.error("============> 체크리스트 생성 중 에러 발생", e);
			throw new RuntimeException("체크리스트 생성 실패", e); // 예외를 던져야 controller에서 500 처리 가능
		}
	}


	// 체크리스트 이름, 설명 수정
	@Override
	public ChecklistDetailDTO updateChecklist(Long userId, Long checklistId,
		ChecklistCreateRequestDTO updateChecklistDTO) {

		// DTO -> VO 변환
		ChecklistVO updateChecklistVO = updateChecklistDTO.toVo(userId);

		try{
			// 체크리스트 이름, 설명 수정
			checklistMapper.updateChecklist(updateChecklistVO.getTitle(), updateChecklistVO.getDescription(), checklistId);

			// Checklist + ChecklistItem 테이블 조인해서 상세정보 함께 반환
			List<ChecklistItemJoinDTO> joinList = checklistMapper.getChecklistDetail(checklistId);
			ChecklistDetailDTO resultDto = ChecklistDetailDTO.from(joinList);

			return resultDto;

		} catch (Exception e) {
			log.error("============> 체크리스트 이름, 설명 수정 중 에러 발생", e);
			throw new RuntimeException("체크리스트 이름, 설명 수정 실패", e);
		}
	}

	// 체크리스트 삭제
	@Override
	public void deleteChecklist(Long checklistId) {
		try {
			// 체크리스트 삭제
			checklistMapper.deleteChecklist(checklistId);
		} catch (Exception e) {
			log.error("============> 체크리스트 삭제 중 에러 발생", e);
			throw new RuntimeException("체크리스트 삭제 실패", e);
		}
	}
}
