package org.livin.global.codef.dto.buildingregister.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class OwnerDTO {
	private String resOwner;
	private String resUserAddr;
	private String resOwnershipStake;
	private String resChangeDate;
	private String resChangeReason;
	private String resIdentityNo;
}
