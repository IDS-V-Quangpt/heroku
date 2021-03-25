package jp.co.hyas.hpf.core.exception;

import org.springframework.util.MultiValueMap;

public class ValidateException extends RuntimeException {
	private MultiValueMap<String, String> detailMap;

	public ValidateException(MultiValueMap<String, String> map) {
		this.detailMap = map;
	}

	public MultiValueMap<String, String> getDetailMap() {
		return this.detailMap;
	}
}
