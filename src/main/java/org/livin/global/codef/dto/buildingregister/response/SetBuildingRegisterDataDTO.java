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
public class SetBuildingRegisterDataDTO {
	private String resUserAddr;
	private String resOriGinalData;
	private String resIssueDate;
	private String resIssueOgzNm;
	private String resNote;
	private String resDocNo;
	private String resReceiptNo;
	private String resNumber;
	private String commUniqeNo;
	private String commAddrRoadName;
	private String commAddrLotNumber;
	private String resAddrDong;
	private String resViolationStatus;
	private List<LicenseClassDTO> resLicenseClassList;
	private List<DetailDTO> resDetailList;
	private List<ChangeDTO> resChangeList;
	private List<BuildingStatusDTO> resBuildingStatusList;
	private List<ParkingLotStatusDTO> resParkingLotStatusList;
	private List<AuthStatusDTO> resAuthStatusList;
}
