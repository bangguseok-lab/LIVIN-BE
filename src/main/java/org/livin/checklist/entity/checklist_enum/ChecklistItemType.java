package org.livin.checklist.entity.checklist_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChecklistItemType {
	ROOM("방 컨디션"),
	BUILDING("건물 컨디션"),
	OPTION("옵션"),
	INFRA("주변 인프라"),
	CIRCUMSTANCE("주변 환경"),
	CUSTOM("나만의 항목");

	private final String label;
}
