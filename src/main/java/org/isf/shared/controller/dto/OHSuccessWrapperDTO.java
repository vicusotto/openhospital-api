package org.isf.shared.controller.dto;

public class OHSuccessWrapperDTO<T> extends OHSuccessDTO {

	private static final long serialVersionUID = -6093669082308045328L;

	private T response;

	public OHSuccessWrapperDTO(T response) {
		this.response = response;
	}

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
