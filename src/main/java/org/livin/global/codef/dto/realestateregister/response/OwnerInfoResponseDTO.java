package org.livin.global.codef.dto.realestateregister.response;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
@Log4j2
public class OwnerInfoResponseDTO {
	private String commUniqueNo;    //고유 번호
	private String ownerName;    //소유자 명
	private boolean isGeneral;

	public static OwnerInfoResponseDTO fromRealEstateRegisterResponseDTO(
		RealEstateRegisterResponseDTO realEstateRegisterResponseDTO) {
		// 응답 데이터가 유효한지 확인
		if (realEstateRegisterResponseDTO == null || realEstateRegisterResponseDTO.getData() == null
			|| realEstateRegisterResponseDTO.getData().getResRegisterEntriesList().isEmpty()) {
			return OwnerInfoResponseDTO.builder().commUniqueNo("정보 없음").ownerName("정보 없음").build();
		}

		// resRegisterEntriesList에서 첫 번째 항목을 가져옵니다.
		ResRegisterEntriesListDTO entry = realEstateRegisterResponseDTO.getData().getResRegisterEntriesList().get(0);
		// 고유 번호 추출
		String commUniqueNo = realEstateRegisterResponseDTO.getData()
			.getResRegisterEntriesList()
			.get(0)
			.getCommUniqueNo();

		// isGeneral 필드 판단
		boolean isGeneral = !entry.getResDocTitle().contains("집합건물");

		// '갑구' 항목 추출
		Optional<ResRegistrationHisListDTO> optionalHisList = realEstateRegisterResponseDTO.getData()
			.getResRegisterEntriesList().get(0)
			.getResRegistrationHisList().stream()
			.filter(his -> "갑구".equals(his.getResType()))
			.findFirst();

		if (optionalHisList.isPresent()) {
			ResRegistrationHisListDTO hisList = optionalHisList.get();
			List<ResContentsListDTO> contentsList = hisList.getResContentsList();

			// 헤더 항목을 찾아 각 필드의 인덱스(resNumber)를 저장
			ResContentsListDTO header = contentsList.stream()
				.filter(c -> "1".equals(c.getResType2()))
				.findFirst().orElse(null);

			if (header != null) {
				// '등기목적'과 '권리자 및 기타사항' 필드의 인덱스 추출
				String purposeIndex = header.getResDetailList().stream()
					.filter(d -> "등기목적".equals(d.getResContents()))
					.map(ResDetailListDTO::getResNumber)
					.findFirst().orElse(null);

				String ownerInfoIndex = header.getResDetailList().stream()
					.filter(d -> "권리자 및 기타사항".equals(d.getResContents()))
					.map(ResDetailListDTO::getResNumber)
					.findFirst().orElse(null);

				// 유효한 인덱스가 있는지 확인
				if (purposeIndex != null && ownerInfoIndex != null) {
					// 실제 데이터 항목들을 역순으로 탐색
					for (int i = contentsList.size() - 1; i >= 0; i--) {
						ResContentsListDTO currentEntry = contentsList.get(i);
						if ("2".equals(currentEntry.getResType2())) {

							String registrationPurpose = currentEntry.getResDetailList().stream()
								.filter(d -> purposeIndex.equals(d.getResNumber()))
								.map(ResDetailListDTO::getResContents)
								.findFirst().orElse("");

							// 등기목적에 '소유권이전' 또는 '소유권보존'이 포함되는지 확인
							if (registrationPurpose.contains("소유권이전") || registrationPurpose.contains("소유권보존")) {
								String ownerInfo = currentEntry.getResDetailList().stream()
									.filter(d -> ownerInfoIndex.equals(d.getResNumber()))
									.map(ResDetailListDTO::getResContents)
									.findFirst().orElse("");

								String[] parts = ownerInfo.split(" ");
								// '소유자' 키워드로 시작하는지 확인
								if (parts.length > 1 && parts[0].contains("소유자")) {
									return OwnerInfoResponseDTO.builder()
										.commUniqueNo(commUniqueNo)
										.ownerName(parts[1])
										.isGeneral(isGeneral)
										.build();
								}
							}
						}
					}
				}
			}
		}
		return OwnerInfoResponseDTO.builder()
			.commUniqueNo(commUniqueNo)
			.ownerName("정보 없음")
			.isGeneral(isGeneral)
			.build();
	}
}
