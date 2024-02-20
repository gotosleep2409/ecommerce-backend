package org.example.apitest.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ErrorDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String defaultMessage;

	private String objectName;

	private String field;

	@Nullable
	private Object rejectedValue;

	private String code;

	public static List<ErrorDetail> convertFromFieldErrors(List<FieldError> fieldErrors) {
		return fieldErrors.parallelStream()
				.map(f -> ErrorDetail.builder()
						.defaultMessage(f.getDefaultMessage())
						.objectName(f.getObjectName())
						.field(f.getField())
						.rejectedValue(f.getRejectedValue())
						.code(f.getCode()).build())
				.collect(Collectors.toList());
	}

}
