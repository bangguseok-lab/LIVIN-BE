package org.livin.risk.service;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.buildingregister.response.BuildingRegisterCollgationResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.GeneralBuildingRegisterResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.SetBuildingRegisterResponseDTO;
import org.livin.global.codef.dto.marketprice.response.BuildingCodeResponseDTO;
import org.livin.global.codef.dto.marketprice.response.MarketInfoResponseDTO;
import org.livin.global.codef.dto.marketprice.response.MarketPriceInfoDTO;
import org.livin.global.codef.service.CodefService;
import org.livin.global.codef.util.BuildingCodeParser;
import org.livin.global.codef.util.BuildingInfoParser;
import org.livin.global.codef.util.MarketInfoParser;
import org.livin.global.exception.CustomException;
import org.livin.global.exception.ErrorCode;
import org.livin.property.dto.PropertyTemporaryDTO;
import org.livin.property.entity.BuildingVO;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.livin.risk.entity.RiskAnalysisVO;
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
	private final RedisTemplate<String, PropertyTemporaryDTO> propertyTemporaryRedisTemplate;

	@Override
	public void createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		BuildingInfoDTO buildingInfoDTO;
		if (riskAnalysisRequestDTO.isGeneral()) {
			GeneralBuildingRegisterResponseDTO response = requestGeneralBuildingRegister(riskAnalysisRequestDTO);
			buildingInfoDTO = BuildingInfoParser.parse(response);
		} else {
			try {
				BuildingRegisterCollgationResponseDTO response = requestBuildingCollgationRegister(
					riskAnalysisRequestDTO);
				buildingInfoDTO = BuildingInfoParser.parse(response);
			} catch (Exception e) {
				SetBuildingRegisterResponseDTO response = requestSetBuildingRegister(riskAnalysisRequestDTO);
				buildingInfoDTO = BuildingInfoParser.parse(response);
			}

		}
		log.info(buildingInfoDTO.getTotalFloors() + " " + buildingInfoDTO.getTotalParkingSpaces() + " "
			+ buildingInfoDTO.getHasElevator());
		BuildingCodeResponseDTO buildingCodeResponseDTO = codefService.requestBuildingCode(buildingInfoDTO);

		String commComplexNo = BuildingCodeParser.parseCommComplexNo(buildingInfoDTO.getResidentialName(),
			buildingCodeResponseDTO);
		MarketInfoResponseDTO marketInfoResponseDTO = codefService.requestMarketInfo(commComplexNo,
			riskAnalysisRequestDTO);

		MarketPriceInfoDTO marketPriceInfoDTO = MarketInfoParser.parseMarketPriceInfo(marketInfoResponseDTO);
		RiskTemporaryDTO riskTemporaryDTO = riskTemporaryRedisTemplate.opsForValue()
			.get(riskAnalysisRequestDTO.getCommUniqueNo());

		long jeonseRatio = (riskAnalysisRequestDTO.getJeonseDeposit() / (
			(Integer.parseInt(marketPriceInfoDTO.getTopPrice()) + Integer.parseInt(
				marketPriceInfoDTO.getLowestPrice())) / 2)) * 100;

		boolean isSafe = jeonseRatio <= 70 && buildingInfoDTO.isViolating();

		RiskAnalysisVO riskAnalysisVO = RiskAnalysisVO.builder()
			.checkLandlord(riskTemporaryDTO.isOwner())
			.isSafe(isSafe)
			.injusticeBuilding(buildingInfoDTO.isViolating())
			.jeonseRatio((int)jeonseRatio)
			.build();

		BuildingVO buildingVO = BuildingVO.builder()
			.elevator("있음".equals(buildingInfoDTO.getHasElevator()))
			.roadAddress(riskAnalysisRequestDTO.getRoadNo())
			.totalFloors(Integer.parseInt(buildingInfoDTO.getTotalFloors()))
			.heatingType(marketPriceInfoDTO.getHeatingType())
			.numParking(buildingInfoDTO.getTotalParkingSpaces())
			.postcode(riskAnalysisRequestDTO.getZipCode())
			.totalUnit(Integer.parseInt(buildingInfoDTO.getTotalHouseholds()))
			.build();
		//redis에 building정보, 안전정보를 박아 넣어 둬야 함
		//매물 등록 요청시 building 정보 꺼내서 저장 하고 다음 매물 다음 순으로 저장
		//총괄로 안나온다면 표제부로 재요청하도록 그래야 승강기, 주차 자리도 가져올 수 있음
		PropertyTemporaryDTO propertyTemporaryDTO = PropertyTemporaryDTO.builder()
			.riskAnalysisVO(riskAnalysisVO)
			.buildingVO(buildingVO)
			.build();

		propertyTemporaryRedisTemplate.opsForValue()
			.set(riskAnalysisRequestDTO.getCommUniqueNo(), propertyTemporaryDTO);
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

	private BuildingRegisterCollgationResponseDTO requestBuildingCollgationRegister(
		RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO,
			BuildingRegisterCollgationResponseDTO.class);
	}
}
