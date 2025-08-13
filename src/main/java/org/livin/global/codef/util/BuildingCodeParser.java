package org.livin.global.codef.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.livin.global.codef.dto.marketprice.response.BuildingCodeResponseDTO;
import org.livin.global.codef.dto.marketprice.response.ComplexDetailDto;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BuildingCodeParser {
	private static final Pattern PARENTHESES_PATTERN = Pattern.compile("\\s*\\([^)]*\\d+(동|세대)[^)]*\\)");

	public static String parseCommComplexNo(String residentialName, BuildingCodeResponseDTO buildingCodeResponseDTO) {
		log.info("residentialName17:{}", residentialName);
		if (residentialName == null || residentialName.trim().isEmpty() ||
			buildingCodeResponseDTO == null || buildingCodeResponseDTO.getData() == null) {
			return null; // 유효하지 않은 입력인 경우 null 반환
		}

		// 입력된 residentialName을 다시 한번 정제하여 비교의 정확성을 높입니다.
		// 예를 들어, "중산 하늘채 더퍼스트"와 "중산하늘채더퍼스트"의 차이 등을 고려할 수 있습니다.
		String cleanedResidentialName = cleanResidentialName(residentialName);

		Optional<String> commComplexNo = buildingCodeResponseDTO.getData().stream()
			.filter(complex -> {
				// 두 번째 응답의 resComplexName도 비교를 위해 정제합니다.
				String complexNameFromResponse = cleanResidentialName(complex.getResComplexName());
				// 정제된 이름이 포함되는지 확인합니다.
				return complexNameFromResponse.contains(cleanedResidentialName) ||
					cleanedResidentialName.contains(complexNameFromResponse);
			})
			.map(ComplexDetailDto::getCommComplexNo)
			.findFirst(); // 첫 번째로 매칭되는 commComplexNo를 반환

		return commComplexNo.orElse(null); // 찾으면 값 반환, 없으면 null 반환
	}

	/**
	 * 주거지 이름을 비교하기 좋게 정제하는 헬퍼 메서드.
	 * 괄호 안의 정보와 공백 등을 제거하여 순수한 단지명만 남깁니다.
	 */
	private static String cleanResidentialName(String name) {
		if (name == null || name.isEmpty()) {
			return "";
		}
		// 괄호 안의 정보 제거 (예: "(13동 1,118세대)" 제거)
		Matcher matcher = PARENTHESES_PATTERN.matcher(name);
		String cleaned = matcher.replaceAll("");

		// 모든 공백 제거 (예: "중산 하늘채 더퍼스트" -> "중산하늘채더퍼스트")
		cleaned = cleaned.replaceAll("\\s", "");

		return cleaned.trim();
	}
}
