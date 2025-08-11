package org.livin.global.codef.util;

import java.util.List;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.buildingregister.response.DetailDTO;
import org.livin.global.codef.dto.buildingregister.response.GeneralBuildingRegisterResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.ParkingLotStatusDTO;
import org.livin.global.codef.dto.buildingregister.response.SetBuildingRegisterResponseDTO;

public class BuildingInfoParser {

	// 일반 건축물 응답 DTO를 파싱하는 메서드
	public static BuildingInfoDTO parse(GeneralBuildingRegisterResponseDTO dto) {
		if (dto == null || dto.getData() == null) {
			return BuildingInfoDTO.builder().isViolating(false).build();
		}

		// 공통 헬퍼 메서드를 사용하여 데이터 추출
		String hasElevator = getElevatorStatus(dto.getData().getResDetailList());
		boolean isViolating = getViolationStatus(dto.getData().getResDetailList(),
			dto.getData().getResViolationStatus());
		String totalFloors = getTotalFloors(dto.getData().getResDetailList());
		int totalParkingSpaces = getTotalParkingSpaces(dto.getData().getResParkingLotStatusList());

		return BuildingInfoDTO.builder()
			.hasElevator(hasElevator)
			.isViolating(isViolating)
			.totalFloors(totalFloors)
			.totalParkingSpaces(totalParkingSpaces)
			.build();
	}

	// 집합 건축물 응답 DTO를 파싱하는 메서드
	public static BuildingInfoDTO parse(SetBuildingRegisterResponseDTO dto) {
		if (dto == null || dto.getData() == null) {
			return BuildingInfoDTO.builder().isViolating(false).build();
		}

		// 공통 헬퍼 메서드를 사용하여 데이터 추출
		String hasElevator = getElevatorStatus(dto.getData().getResDetailList());
		boolean isViolating = getViolationStatus(dto.getData().getResDetailList(),
			dto.getData().getResViolationStatus());
		String totalFloors = getTotalFloors(dto.getData().getResDetailList());
		int totalParkingSpaces = getTotalParkingSpaces(dto.getData().getResParkingLotStatusList());

		return BuildingInfoDTO.builder()
			.hasElevator(hasElevator)
			.isViolating(isViolating)
			.totalFloors(totalFloors)
			.totalParkingSpaces(totalParkingSpaces)
			.build();
	}

	// 승강기 유무를 추출하는 공통 메서드
	private static String getElevatorStatus(List<DetailDTO> detailList) {
		if (detailList == null)
			return "없음";
		return detailList.stream()
			.filter(d -> "승강기|승용".equals(d.getResType()) || "승용".equals(d.getResType()))
			.findFirst()
			.map(d -> "있음")
			.orElse("없음");
	}

	// 위반건축물 여부를 추출하는 공통 메서드
	private static boolean getViolationStatus(List<DetailDTO> detailList, String directViolationStatus) {
		if (detailList != null) {
			return detailList.stream()
				.filter(d -> "위반건축물여부".equals(d.getResType()))
				.findFirst()
				.map(d -> d.getResContents() != null && !d.getResContents().isEmpty())
				.orElse(false);
		}
		// resDetailList가 없는 경우, 상위 필드인 resViolationStatus를 사용
		return directViolationStatus != null && !directViolationStatus.isEmpty();
	}

	// 총 층수를 추출하는 공통 메서드
	private static String getTotalFloors(List<DetailDTO> detailList) {
		if (detailList == null)
			return "";
		return detailList.stream()
			.filter(d -> "층수".equals(d.getResType()))
			.findFirst()
			.map(DetailDTO::getResContents)
			.orElse("");
	}

	// 총 주차자리 수를 합산하는 공통 메서드
	private static int getTotalParkingSpaces(List<ParkingLotStatusDTO> parkingLotStatusList) {
		if (parkingLotStatusList == null)
			return 0;
		return parkingLotStatusList.stream()
			.mapToInt(p -> {
				try {
					return Integer.parseInt(p.getResNumber());
				} catch (NumberFormatException | NullPointerException e) {
					return 0;
				}
			})
			.sum();
	}
}