package org.livin.global.codef.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.livin.global.codef.dto.buildingregister.BuildingInfoDTO;
import org.livin.global.codef.dto.buildingregister.response.BuildingCollgationStatusDTO;
import org.livin.global.codef.dto.buildingregister.response.BuildingRegisterCollgationResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.DetailDTO;
import org.livin.global.codef.dto.buildingregister.response.GeneralBuildingRegisterResponseDTO;
import org.livin.global.codef.dto.buildingregister.response.ParkingLotStatusDTO;
import org.livin.global.codef.dto.buildingregister.response.SetBuildingRegisterResponseDTO;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BuildingInfoParser {
	private static final Pattern PARENTHESES_PATTERN = Pattern.compile("\\([^)]*\\d+세대[^)]*\\)");

	// 일반 건축물 응답 DTO를 파싱하는 메서드
	public static BuildingInfoDTO parse(GeneralBuildingRegisterResponseDTO dto) {
		if (dto == null || dto.getData() == null) {
			return BuildingInfoDTO.builder().isViolating(false).build();
		}

		// 공통 헬퍼 메서드를 사용하여 데이터 추출
		String hasElevator = getElevatorStatus(dto.getData().getResDetailList());
		boolean isViolating = getViolationStatus(dto.getData().getResDetailList(),
			dto.getData().getResViolationStatus());
		String totalFloors = getTotalFloorsFromDetailList(dto.getData().getResDetailList()); // 분리된 메서드 호출
		int totalParkingSpaces = getTotalParkingSpaces(dto.getData().getResParkingLotStatusList());
		String[] address = parseAddress(dto.getData().getResUserAddr());
		String residentialName = extractResidentialName(dto.getData().getResAddrDong());
		return BuildingInfoDTO.builder()
			.hasElevator(hasElevator)
			.isViolating(isViolating)
			.totalFloors(totalFloors)
			.totalParkingSpaces(totalParkingSpaces)
			.sido(address[0])
			.sigungu(address[1])
			.eupmyeondong(address[2])
			.residentialName(residentialName)
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
		String totalFloors = getTotalFloorsFromDetailList(dto.getData().getResDetailList()); // 분리된 메서드 호출
		int totalParkingSpaces = getTotalParkingSpaces(dto.getData().getResParkingLotStatusList());
		String[] address = parseAddress(dto.getData().getResUserAddr());
		String residentialName = extractResidentialName(dto.getData().getResAddrDong());
		return BuildingInfoDTO.builder()
			.hasElevator(hasElevator)
			.isViolating(isViolating)
			.totalFloors(totalFloors)
			.totalParkingSpaces(totalParkingSpaces)
			.sido(address[0])
			.sigungu(address[1])
			.eupmyeondong(address[2])
			.residentialName(residentialName)
			.build();
	}

	// 집합 건축물대장 응답 DTO를 파싱하는 메서드 (가장 최신 DTO 구조)
	public static BuildingInfoDTO parse(BuildingRegisterCollgationResponseDTO dto) {
		// 데이터가 null이거나 비어있을 경우 기본값 반환
		if (dto == null || dto.getData() == null) {
			return BuildingInfoDTO.builder().isViolating(false).build();
		}

		// 기존 헬퍼 메서드를 사용하여 데이터 추출
		String hasElevator = getElevatorStatus(dto.getData().getResDetailList());
		boolean isViolating = getViolationStatus(dto.getData().getResDetailList(),
			dto.getData().getResViolationStatus());
		// **BuildingRegisterCollgationResponseDTO 전용 층수 로직**
		String totalFloors = getTotalFloorsFromStatusList(dto.getData().getResBuildingStatusList());
		int totalParkingSpaces = getTotalParkingSpaces(dto.getData().getResParkingLotStatusList());
		String[] address = parseAddress(dto.getData().getResUserAddr());
		// 새로운 DTO에는 resAddrDong이 없으므로 resBuildingName을 사용하도록 수정
		String residentialName = extractResidentialNameFromBuildingName(dto.getData().getResBuildingName());
		String totalHouseholds = getTotalHouseholds(dto.getData().getResDetailList()); // <-- 새로운 메서드 호출
		return BuildingInfoDTO.builder()
			.hasElevator(hasElevator)
			.isViolating(isViolating)
			.totalFloors(totalFloors)
			.totalParkingSpaces(totalParkingSpaces)
			.sido(address[0])
			.sigungu(address[1])
			.eupmyeondong(address[2])
			.totalHouseholds(totalHouseholds)
			.residentialName(residentialName)
			.build();
	}

	private static String getTotalHouseholds(List<DetailDTO> detailList) {
		if (detailList == null) {
			return "";
		}
		return detailList.stream()
			// "총호수" 또는 "호가구세대수" 항목을 찾습니다.
			.filter(d -> "총호수".equals(d.getResType()) || "호가구세대수".equals(d.getResType()))
			.findFirst()
			.map(d -> {
				// "1144세대" 부분만 추출하기 위한 정규표현식
				Pattern pattern = Pattern.compile("(\\d+)세대");
				Matcher matcher = pattern.matcher(d.getResContents());
				if (matcher.find()) {
					return matcher.group(1); // 숫자만 반환
				}
				return "";
			})
			.orElse("");
	}

	private static String extractResidentialNameFromBuildingName(String resBuildingName) {
		if (resBuildingName == null || resBuildingName.isEmpty()) {
			return "";
		}
		// 예: "중산자이 1단지" -> "중산자이1단지"
		return resBuildingName.replaceAll("\\s", "").trim();
	}

	private static String extractResidentialName(String resAddrDong) {
		if (resAddrDong == null || resAddrDong.isEmpty()) {
			return "";
		}
		// 예: "중산 하늘채 더퍼스트 109동" -> "중산 하늘채 더퍼스트"
		// 예: "경남신성(13동 1,118세대)" -> "경남신성"
		// '동'으로 끝나는 경우 '동'과 그 앞의 숫자를 제거
		String cleanedName = resAddrDong.replaceAll("\\s\\d+동$", "");

		// 괄호 안에 있는 "세대" 정보 제거 (예: "(13동 1,118세대)" 제거)
		Matcher matcher = PARENTHESES_PATTERN.matcher(cleanedName);
		cleanedName = matcher.replaceAll("");

		// 3. ✨추가: 모든 띄어쓰기를 제거합니다.
		// 예: "펜타힐즈 더샵 1차" -> "펜타힐즈더샵1차"
		cleanedName = cleanedName.replaceAll("\\s", "");

		log.info(cleanedName.trim()); // 로그를 남기려면 주석 해제

		return cleanedName.trim();
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

	// 주소 파싱을 위한 헬퍼 메서드
	private static String[] parseAddress(String resUserAddr) {
		if (resUserAddr == null || resUserAddr.isEmpty()) {
			return new String[] {"", "", ""};
		}
		String[] parts = resUserAddr.split(" ");
		String sido = "";
		String sigungu = "";
		String eupmyeondong = "";

		if (parts.length > 0) {
			sido = parts[0];
		}

		// 세종특별자치시, 제주특별자치도와 같이 시/군/구가 없는 경우 처리
		if (parts.length == 2 && ("세종특별자치시".equals(sido))) {
			sigungu = sido;
			eupmyeondong = parts[1];
		}
		// '시'와 '구'가 모두 있는 일반적인 경우 처리
		else if (parts.length > 2 && parts[1].endsWith("시") && parts[2].endsWith("구")) {
			sigungu = parts[1] + " " + parts[2];
			if (parts.length > 3) {
				eupmyeondong = parts[3];
			}
		}
		// 그 외 일반적인 시/군/구 주소 처리
		else {
			if (parts.length > 1) {
				sigungu = parts[1];
			}
			if (parts.length > 2) {
				eupmyeondong = parts[2];
			}
		}

		return new String[] {sido, sigungu, eupmyeondong};
	}

	// resDetailList에서 총 층수를 추출하는 메서드 (기존 로직)
	private static String getTotalFloorsFromDetailList(List<DetailDTO> detailList) {
		if (detailList == null)
			return "";
		return detailList.stream()
			.filter(d -> "층수".equals(d.getResType()))
			.findFirst()
			.map(DetailDTO::getResContents)
			.orElse("");
	}

	// resBuildingStatusList에서 가장 높은 층수를 추출하는 메서드 (새로운 로직)
	private static String getTotalFloorsFromStatusList(List<BuildingCollgationStatusDTO> buildingStatusList) {
		if (buildingStatusList == null || buildingStatusList.isEmpty()) {
			return "";
		}
		int maxFloor = 0;
		for (BuildingCollgationStatusDTO status : buildingStatusList) {
			String floorInfo = status.getResFloor();
			if (floorInfo != null && !floorInfo.isEmpty()) {
				try {
					String[] parts = floorInfo.split("/");
					if (parts.length > 1) {
						int currentFloor = Integer.parseInt(parts[1]);
						if (currentFloor > maxFloor) {
							maxFloor = currentFloor;
						}
					}
				} catch (NumberFormatException e) {
					log.warn("Failed to parse floor number: " + floorInfo);
				}
			}
		}
		return maxFloor > 0 ? String.valueOf(maxFloor) : "";
	}

	// 총 주차자리 수를 합산하는 공통 메서드
	private static int getTotalParkingSpaces(List<ParkingLotStatusDTO> parkingLotStatusList) {
		if (parkingLotStatusList == null)
			return 0;
		return parkingLotStatusList.stream()
			.mapToInt(p -> {
				try {
					// NumberFormatException을 방지하기 위해 쉼표(,)를 제거
					String numberString = p.getResNumber().replaceAll(",", "");
					return Integer.parseInt(numberString);
				} catch (NumberFormatException | NullPointerException e) {
					return 0;
				}
			})
			.sum();
	}
}