package org.livin.property.dto.realestateregister.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class ResRegisterEntriesListDTO {
	private String resPublishRegistryOffice;
	private String commUniqueNo;
	private String resIssueNo;
	private List<ResRegistrationHisListDTO> resRegistrationHisList;
	private String resDocTitle;
	private String resRealty;
	private String resPublishNo;
	private String resPublishDate;
	private List<ResRegistrationSumListDTO> resRegistrationSumList;
	private String commCompetentRegistryOffice;
	private List<Object> resPrecautionsList;
}