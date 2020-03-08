package org.isf.shared.controller.dto;

public class OHFailureDTO extends OHResponseDTO {

	private static final long serialVersionUID = -6193791376259942266L;

	private String message;

	public OHFailureDTO(OHResponseCode responseCode, String message) {
		super(responseCode, false);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
