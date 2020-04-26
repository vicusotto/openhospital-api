package org.isf.shared.controller.dto;

import java.io.Serializable;

public class OHResponseDTO implements Serializable {

	private static final long serialVersionUID = -2825718865289305256L;

	private OHResponseCode responseCode;
	private boolean success;

	public OHResponseDTO(OHResponseCode responseCode, boolean success) {
		this.responseCode = responseCode;
		this.success = success;
	}

	public OHResponseCode getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(OHResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
