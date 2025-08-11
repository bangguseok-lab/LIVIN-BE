package org.livin.global.codef.dto.realestateregister.response;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class RealEstateRegisterResponseDTO {
	private RealEstateResultDTO result;
	private RealEstateDataDTO data;
	private String eprepayNo;
	private String eprepayPass;

	public static Long parseMaximumBondAmount(RealEstateRegisterResponseDTO realEstateRegisterResponseDTO) {
		Optional<ResRegistrationHisListDTO> optionalEulguList = realEstateRegisterResponseDTO.getData()
			.getResRegisterEntriesList().get(0)
			.getResRegistrationHisList().stream()
			.filter(his -> "을구".equals(his.getResType()))
			.findFirst();

		if (optionalEulguList.isEmpty()) {
			return null; // "을구"가 없는 경우
		}

		List<ResContentsListDTO> eulguContentsList = optionalEulguList.get().getResContentsList();
		long maximumBondAmount = 0L;

		// "등기목적"과 "권리자 및 기타사항" 필드의 인덱스를 찾습니다.
		ResContentsListDTO header = eulguContentsList.stream()
			.filter(c -> "1".equals(c.getResType2()))
			.findFirst().orElse(null);

		if (header == null) {
			return null;
		}

		String purposeIndex = header.getResDetailList().stream()
			.filter(d -> "등기목적".equals(d.getResContents()))
			.map(ResDetailListDTO::getResNumber)
			.findFirst().orElse(null);

		String bondAmountIndex = header.getResDetailList().stream()
			.filter(d -> "권리자 및 기타사항".equals(d.getResContents()))
			.map(ResDetailListDTO::getResNumber)
			.findFirst().orElse(null);

		if (purposeIndex == null || bondAmountIndex == null) {
			return null;
		}

		// 실제 등기 항목을 순회하며 채권최고액을 추출합니다.
		for (ResContentsListDTO entry : eulguContentsList) {
			if ("2".equals(entry.getResType2())) { // 데이터 항목인 경우
				Optional<String> registrationPurpose = entry.getResDetailList().stream()
					.filter(d -> purposeIndex.equals(d.getResNumber()))
					.map(ResDetailListDTO::getResContents)
					.findFirst();

				if (registrationPurpose.isPresent() && "근저당권설정".equals(registrationPurpose.get())) {
					Optional<String> bondInfo = entry.getResDetailList().stream()
						.filter(d -> bondAmountIndex.equals(d.getResNumber()))
						.map(ResDetailListDTO::getResContents)
						.findFirst();

					if (bondInfo.isPresent()) {
						try {
							String bondAmountStr = bondInfo.get().split("채권최고액 금")[1].split("원")[0].replace(",", "")
								.trim();
							long currentBondAmount = Long.parseLong(bondAmountStr);
							if (currentBondAmount > maximumBondAmount) {
								maximumBondAmount = currentBondAmount;
							}
						} catch (Exception e) {
							log.error("채권최고액 파싱 중 오류 발생: {}", e.getMessage(), e);
						}
					}
				}
			}
		}
		log.info("채권최고액:{}", maximumBondAmount);
		return maximumBondAmount > 0 ? maximumBondAmount : null;

	}
}
