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
import org.livin.property.entity.property_enum.AbleStatus;
import org.livin.property.entity.property_enum.EntranceStructure;
import org.livin.property.entity.property_enum.HeatingFuel;
import org.livin.property.entity.property_enum.HeatingType;
import org.livin.risk.dto.RiskAddressResponseDTO;
import org.livin.risk.dto.RiskAnalysisRequestDTO;
import org.livin.risk.dto.RiskAnalysisResponseDTO;
import org.livin.risk.dto.RiskTemporaryDTO;
import org.livin.risk.entity.RiskAnalysisVO;
import org.livin.risk.mapper.RiskMapper;
import org.livin.user.service.UserService;
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
	private final RiskMapper riskMapper;
	private final UserService userService;

	@Override
	public RiskAddressResponseDTO createRiskTemporaryInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		try {
			BuildingInfoDTO buildingInfoDTO = requestBuildingInfo(riskAnalysisRequestDTO);
			MarketPriceInfoDTO marketPriceInfoDTO = requestMarketPriceInfo(buildingInfoDTO, riskAnalysisRequestDTO);
			RiskTemporaryDTO riskTemporaryDTO = getRiskTemporaryInfo(riskAnalysisRequestDTO.getCommUniqueNo());

			int jeonseRatio = calculateJeonseRatio(riskAnalysisRequestDTO.getJeonseDeposit(), marketPriceInfoDTO);
			boolean isSafe = jeonseRatio <= 70 && !buildingInfoDTO.isViolating();

			Long salePrice = (Long.parseLong(marketPriceInfoDTO.getTopPrice()) * 10000L
				+ Long.parseLong(marketPriceInfoDTO.getLowestPrice()) * 10000L) / 2;

			RiskAnalysisVO riskAnalysisVO = buildRiskAnalysisVO(riskTemporaryDTO, isSafe, buildingInfoDTO.isViolating(),
				jeonseRatio, salePrice);
			BuildingVO buildingVO = buildBuildingVO(buildingInfoDTO, marketPriceInfoDTO, riskAnalysisRequestDTO);

			PropertyTemporaryDTO propertyTemporaryDTO = PropertyTemporaryDTO.builder()
				.riskAnalysisVO(riskAnalysisVO)
				.buildingVO(buildingVO)
				.build();

			propertyTemporaryRedisTemplate.opsForValue()
				.set(riskAnalysisRequestDTO.getCommUniqueNo(), propertyTemporaryDTO);

			return RiskAddressResponseDTO.builder()
				.sido(buildingInfoDTO.getSido())
				.sigungu(buildingInfoDTO.getSigungu())
				.eupmyeondong(buildingInfoDTO.getEupmyeondong())
				.commUniqueNo(riskAnalysisRequestDTO.getCommUniqueNo())
				.build();
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	//건물 정보 가져오기
	private BuildingInfoDTO requestBuildingInfo(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		if (riskAnalysisRequestDTO.isGeneral()) {
			GeneralBuildingRegisterResponseDTO response = requestGeneralBuildingRegister(riskAnalysisRequestDTO);
			if ("CF-00000".equals(response.getResult().getCode())) {
				return BuildingInfoParser.parse(response);
			}
			return BuildingInfoParser.parse();
		} else {
			try {
				BuildingRegisterCollgationResponseDTO response = requestBuildingCollgationRegister(
					riskAnalysisRequestDTO);
				if ("CF-00000".equals(response.getResult().getCode())) {
					return BuildingInfoParser.parse(response);
				}
				return BuildingInfoParser.parse();
			} catch (Exception e) {
				SetBuildingRegisterResponseDTO response = requestSetBuildingRegister(riskAnalysisRequestDTO);
				if ("CF-00000".equals(response.getResult().getCode())) {
					return BuildingInfoParser.parse(response);
				}
				return BuildingInfoParser.parse();
			}
		}
	}

	//시세정보 가져오기
	private MarketPriceInfoDTO requestMarketPriceInfo(BuildingInfoDTO buildingInfoDTO,
		RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		BuildingCodeResponseDTO buildingCodeResponseDTO = codefService.requestBuildingCode(buildingInfoDTO);
		String commComplexNo = BuildingCodeParser.parseCommComplexNo(buildingInfoDTO.getResidentialName(),
			buildingCodeResponseDTO);
		MarketInfoResponseDTO marketInfoResponseDTO = codefService.requestMarketInfo(commComplexNo,
			riskAnalysisRequestDTO);
		return MarketInfoParser.parseMarketPriceInfo(marketInfoResponseDTO);
	}

	//이전에 등기부등본 과정에서 redis에 저장한 임시 데이터 가져오기
	private RiskTemporaryDTO getRiskTemporaryInfo(String commUniqueNo) {
		RiskTemporaryDTO riskTemporaryDTO = riskTemporaryRedisTemplate.opsForValue().get(commUniqueNo);
		if (riskTemporaryDTO == null) {
			log.error("Redis에서 RiskTemporaryDTO를 찾을 수 없습니다. 키: {}", commUniqueNo);
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		return riskTemporaryDTO;
	}

	//전세가율 계산
	private int calculateJeonseRatio(long jeonseDeposit, MarketPriceInfoDTO marketPriceInfoDTO) {
		long averageMarketPrice = (Long.parseLong(marketPriceInfoDTO.getTopPrice()) * 10000L
			+ Long.parseLong(marketPriceInfoDTO.getLowestPrice()) * 10000L) / 2;
		double jeonseRatioDouble = ((double)jeonseDeposit / averageMarketPrice) * 100;
		return (int)Math.round(jeonseRatioDouble);
	}

	//BuildingVO 생성
	private BuildingVO buildBuildingVO(BuildingInfoDTO buildingInfoDTO, MarketPriceInfoDTO marketPriceInfoDTO,
		RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		return BuildingVO.builder()
			.elevator("있음".equals(buildingInfoDTO.getHasElevator()))
			.roadAddress(riskAnalysisRequestDTO.getRoadNo())
			.totalFloors(Integer.parseInt(buildingInfoDTO.getTotalFloors()))
			.heatingType(marketPriceInfoDTO.getHeatingType() != null ?
				marketPriceInfoDTO.getHeatingType() : HeatingType.CENTRAL_HEATING)
			.heatingFuel(HeatingFuel.LPG)
			.entranceStructure(EntranceStructure.HALLWAY)
			.numParking(buildingInfoDTO.getTotalParkingSpaces())
			.postcode(riskAnalysisRequestDTO.getZipCode())
			.totalUnit(Integer.parseInt(buildingInfoDTO.getTotalHouseholds()))
			.parking(AbleStatus.ABLE)
			.completionYear(2022)
			.build();
	}

	//RiskAnalysisVO 생성
	private RiskAnalysisVO buildRiskAnalysisVO(RiskTemporaryDTO riskTemporaryDTO, boolean isSafe, boolean isViolating,
		int jeonseRatio, Long salePrice) {
		return RiskAnalysisVO.builder()
			.checkLandlord(riskTemporaryDTO.isOwner())
			.isSafe(isSafe)
			.injusticeBuilding(isViolating)
			.jeonseRatio(jeonseRatio)
			.maximumBondAmount(riskTemporaryDTO.getMaximum_bond_amount())
			.salePrice(salePrice)
			.build();
	}

	//임시 위험 데이터 삭제
	@Override
	public void deleteRiskTemporaryInfo(String commUniqueNo) {
		try {
			riskTemporaryRedisTemplate.delete(commUniqueNo);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void createRiskAnalysis(RiskAnalysisVO riskAnalysisVO, Long propertyId) {
		riskAnalysisVO.setPropertyId(propertyId);
		riskMapper.createRiskAnalysis(riskAnalysisVO);
	}

	//일반 건축물대장 요청
	private GeneralBuildingRegisterResponseDTO requestGeneralBuildingRegister
	(
		RiskAnalysisRequestDTO riskAnalysisRequestDTO
	) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO, GeneralBuildingRegisterResponseDTO.class);
	}

	//집합 건축물
	private SetBuildingRegisterResponseDTO requestSetBuildingRegister(RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO, SetBuildingRegisterResponseDTO.class);
	}

	//총괄 집합 건축물대장 요청
	private BuildingRegisterCollgationResponseDTO requestBuildingCollgationRegister(
		RiskAnalysisRequestDTO riskAnalysisRequestDTO) {
		return codefService.requestBuildingRegister(riskAnalysisRequestDTO,
			BuildingRegisterCollgationResponseDTO.class);
	}

	@Override
	public RiskAnalysisResponseDTO getRiskAnalysis(Long propertyId, String providerId) {
		RiskAnalysisVO riskAnalysisVO = riskMapper.getRiskAnalysis(propertyId);
		Long userDeposit = userService.getUserDeposit(providerId);
		return RiskAnalysisResponseDTO.fromRiskAnalysisVO(riskAnalysisVO, userDeposit);
	}
}