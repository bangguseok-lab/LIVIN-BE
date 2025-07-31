package org.livin.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
	private boolean success;
	private String message;
	private T data;
}
