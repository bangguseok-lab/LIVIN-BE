package org.livin.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum UserRole {
	LANDLORD("임대인"),
	TENANT("임차인");
	private final String label;
}