package org.livin.property.dto.realestateregister;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class OwnerInfoDTO {
	private String commUniqueNo;	//고유 번호
	private String ownerName;	//소유자 명

	public static OwnerInfoDTO fromRealEstateRegisterResponseDTO(RealEstateRegisterResponseDTO realEstateRegisterResponseDTO) {
		// 응답 데이터가 유효한지 확인
		if (realEstateRegisterResponseDTO == null || realEstateRegisterResponseDTO.getData() == null || realEstateRegisterResponseDTO.getData().getResRegisterEntriesListDTO().isEmpty()) {
			return OwnerInfoDTO.builder().commUniqueNo("정보 없음").ownerName("정보 없음").build();
		}

		// 고유 번호 추출
		String commUniqueNo = realEstateRegisterResponseDTO.getData().getResRegisterEntriesListDTO().get(0).getCommUniqueNo();

		// '갑구' 항목 추출
		Optional<ResRegistrationHisListDTO> optionalHisList = realEstateRegisterResponseDTO.getData()
			.getResRegisterEntriesListDTO().get(0)
			.getResRegistrationHisList().stream()
			.filter(his -> "갑구".equals(his.getResType()))
			.findFirst();

		if (optionalHisList.isPresent()) {
			ResRegistrationHisListDTO hisList = optionalHisList.get();
			List<ResContentsListDTO> contentsList = hisList.getResContentsListDTO();

			// 헤더 항목을 찾아 각 필드의 인덱스(resNumber)를 저장
			ResContentsListDTO header = contentsList.stream()
				.filter(c -> "1".equals(c.getResType2()))
				.findFirst().orElse(null);

			if (header != null) {
				// '등기목적'과 '권리자 및 기타사항' 필드의 인덱스 추출
				String purposeIndex = header.getResDetailListDTO().stream()
					.filter(d -> "등기목적".equals(d.getResContents()))
					.map(ResDetailListDTO::getResNumber)
					.findFirst().orElse(null);

				String ownerInfoIndex = header.getResDetailListDTO().stream()
					.filter(d -> "권리자 및 기타사항".equals(d.getResContents()))
					.map(ResDetailListDTO::getResNumber)
					.findFirst().orElse(null);

				// 유효한 인덱스가 있는지 확인
				if (purposeIndex != null && ownerInfoIndex != null) {
					// 실제 데이터 항목들을 역순으로 탐색
					for (int i = contentsList.size() - 1; i >= 0; i--) {
						ResContentsListDTO currentEntry = contentsList.get(i);
						if ("2".equals(currentEntry.getResType2())) {

							String registrationPurpose = currentEntry.getResDetailListDTO().stream()
								.filter(d -> purposeIndex.equals(d.getResNumber()))
								.map(ResDetailListDTO::getResContents)
								.findFirst().orElse("");

							if ("소유권이전".equals(registrationPurpose) || "소유권보존".equals(registrationPurpose)) {
								String ownerInfo = currentEntry.getResDetailListDTO().stream()
									.filter(d -> ownerInfoIndex.equals(d.getResNumber()))
									.map(ResDetailListDTO::getResContents)
									.findFirst().orElse("");

								String[] parts = ownerInfo.split(" ");
								if (parts.length > 1) {
									return OwnerInfoDTO.builder()
										.commUniqueNo(commUniqueNo)
										.ownerName(parts[1])
										.build();
								}
							}
						}
					}
				}
			}
		}
		return OwnerInfoDTO.builder()
			.commUniqueNo(commUniqueNo)
			.ownerName("정보 없음")
			.build();
	}
}
