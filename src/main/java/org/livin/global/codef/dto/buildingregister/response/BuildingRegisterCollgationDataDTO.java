package org.livin.global.codef.dto.buildingregister.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class BuildingRegisterCollgationDataDTO {
	private String resDocNo;
	private String commUniqeNo;
	private String resReceiptNo;
	private String resBuildingName;
	private String resNote1;
	private String resUserAddr;
	private String commAddrLotNumber;
	private String commAddrRoadName;
	private String resNote;
	private String resIssueDate;
	private String resIssueOgzNm;
	private String resViolationStatus;
	private List<DetailDTO> resDetailList;
	private List<BuildingCollgationStatusDTO> resBuildingStatusList;
	private List<LicenseClassDTO> resLicenseClassList;
	private List<ParkingLotStatusDTO> resParkingLotStatusList;
	private List<AuthStatusDTO> resAuthStatusList;
	private List<BuildingCollgationChangeDTO> resChangeList;
	private String resOriGinalData;
}