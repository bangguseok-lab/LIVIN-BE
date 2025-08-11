package org.livin.risk.service;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.buildingregister.response.GeneralBuildingRegisterResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.SetBuildingRegisterResponseDTO;
import org.livin.global.codef.service.CodefService;
import org.livin.global.codef.util.BuildingInfoParser;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class RiskServiceImpl implements RiskService {

	private final RedisTemplate<String, RiskTemporaryDTO> riskTemporaryRedisTemplate;
	private final CodefService codefService;

	@Override
	public void createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		BuildingInfoDTO buildingInfoDTO;
		if (riskAnalysisRequestDTO.isGeneral()) {
			GeneralBuildingRegisterResponseDTO response = requestGeneralBuildingRegister(riskAnalysisRequestDTO);
			buildingInfoDTO = BuildingInfoParser.parse(response);
		} else {
			SetBuildingRegisterResponseDTO response = requestSetBuildingRegister(riskAnalysisRequestDTO);
			buildingInfoDTO = BuildingInfoParser.parse(response);
		}
		log.info(buildingInfoDTO.getTotalFloors() + " " + buildingInfoDTO.getTotalParkingSpaces());
		//현재까지 채권최고액, 불법 건축물 유무, 소유자 판단 여부
		//남은 것 시세 조회 후 전세금과 함께 계산
		//빌딩 테이블VO 만들기
	}

	@Override
	public void deleteRiskTemporaryInfo(String commUniqueNo) {
		try {
			riskTemporaryRedisTemplate.delete(commUniqueNo);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private GeneralBuildingRegisterResponseDTO requestGeneralBuildingRegister
		(
			RiskAnalysisRequestDTO riskAnalysisRequestDTO
		) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO, GeneralBuildingRegisterResponseDTO.class);
	}

	private SetBuildingRegisterResponseDTO requestSetBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO, SetBuildingRegisterResponseDTO.class);
	}
}
