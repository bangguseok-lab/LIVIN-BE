package org.livin.property.dto.realestateregister.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealEstateDataDTO {
	private List<ResRegisterEntriesListDTO> resRegisterEntriesListDTO;
	private String resOriGinalData;
	private String commStartPageNo;
	private String resIssueYN;
	private String resEndPageNo;
	private String resWarningMessage;
	private List<Object> resSearchList;
	private List<Object> resImageList;
	private String commIssueCode;
	private List<Object> resAddrList;
	private String resTotalPageCount;
}
