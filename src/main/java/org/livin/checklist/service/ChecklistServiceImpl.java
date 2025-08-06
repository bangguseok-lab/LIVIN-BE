package org.livin.checklist.service;

import java.util.List;
import java.util.stream.Collectors;

import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.dto.ChecklistDetailDTO;
import org.livin.checklist.dto.ChecklistItemJoinDTO;
import org.livin.checklist.dto.ChecklistItemSimpleDTO;
import org.livin.checklist.dto.ChecklistItemStatusDTO;
import org.livin.checklist.dto.ChecklistListResponseDTO;
import org.livin.checklist.dto.CustomItemDTO;
import org.livin.checklist.dto.RequestChecklistItemDTO;
import org.livin.checklist.dto.RequestCustomItemsDTO;
import org.livin.checklist.entity.ChecklistItemVO;
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

		log.info("체크리스트 생성~");
		try {
			// Checklist 생성
			checklistMapper.create(checklistVO);
			Long checklistId = checklistVO.getChecklistId();    // 생성된 checklistId 추출
			log.info("✅ 생성된 checklistId: {}", checklistId);

			// ChecklistItem 기본 아이템 생성 + 주변 인프라 + 주변 환경 + 옵션 항목 함께 생성
			checklistMapper.createChecklistDefaultItem(checklistId);
			checklistMapper.createInfraItem(checklistId);
			checklistMapper.createCircumstanceItem(checklistId);
			checklistMapper.createOptionItem(checklistId);

			// Checklist + ChecklistItem 테이블 조인해서 상세정보 함께 반환
			List<ChecklistItemJoinDTO> joinList = checklistMapper.getChecklistDetail(checklistId);
			ChecklistDetailDTO resultDto = ChecklistDetailDTO.from(joinList);

			return resultDto;

		} catch (Exception e) {
			log.error("============> 체크리스트 생성 중 에러 발생", e);
			throw new RuntimeException("체크리스트 생성 실패", e); // 예외를 던져야 controller에서 500 처리 가능
		}
	}

	// 기본 항목 외 체크리스트 아이템 항목 생성
	@Override
	public List<ChecklistItemSimpleDTO> createOtherTypeItemList(Long checklistId, String type) {
		switch (type) {
			case "INFRA":
				checklistMapper.createInfraItem(checklistId);
				break;
			case "OPTION":
				checklistMapper.createOptionItem(checklistId);
				break;
			case "CIRCUMSTANCE":
				checklistMapper.createCircumstanceItem(checklistId);
				break;
			default:
				throw new IllegalArgumentException("지원하지 않는 type: " + type);
		}

		return checklistMapper.getItemListByType(checklistId, type);
	}

	// 나만의 아이템 항목 생성
	@Override
	public List<ChecklistItemSimpleDTO> createCustomItem(Long checklistId,
		RequestCustomItemsDTO requestCustomItemsDTO) {

		for(CustomItemDTO customItem : requestCustomItemsDTO.getCustomItems()) {
			// DTO -> VO 변환
			ChecklistItemVO customItemVO = customItem.toVo(checklistId);

			// 나만의 아이템 항목 생성
			checklistMapper.createCustomItem(checklistId, customItemVO.getKeyword());
		}

		// 생성된 나만의 아이템 리스트 반환
		return checklistMapper.getItemListByType(checklistId, "CUSTOM");
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


	// 체크리스트 아이템 활성 상태 수정
	@Override
	public ChecklistDetailDTO updateItem(Long checklistId, RequestChecklistItemDTO requestChecklistItemDTO) {
		try {
			// 요청 들어온 체크리스트 아이템들
			List<ChecklistItemStatusDTO> requestItems = requestChecklistItemDTO.getItems();

			// 체크리스트 아이템 수정
			for(ChecklistItemStatusDTO item : requestItems) {
				checklistMapper.updateItem(item.getChecklistItemId(), item.getIsActive());
			}

			// 수정된 타입의 아이템 리스트 반환
			// Checklist + ChecklistItem 테이블 조인해서 상세정보 함께 반환
			List<ChecklistItemJoinDTO> joinList = checklistMapper.getChecklistDetail(checklistId);
			ChecklistDetailDTO resultDto = ChecklistDetailDTO.from(joinList);

			return resultDto;


		} catch (Exception e) {
			log.error("============> 체크리스트 아이템 수정 중 에러 발생", e);
			throw new RuntimeException("체크리스트 아이템 수정 실패", e);
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

	// 나만의 아이템 삭제
	@Override
	public void deleteCustomItem(Long checklistId, Long checklistItemId) {
		try {
			// 나만의 아이템 삭제
			checklistMapper.deleteCustomItem(checklistId, checklistItemId);

		} catch (Exception e) {
			log.error("============> 나만의 아이템 삭제 중 에러 발생", e);
			throw new RuntimeException("나만의 아이템 삭제 실패", e);
		}
	}
}
