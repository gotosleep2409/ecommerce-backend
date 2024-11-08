package org.example.apitest.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.exception.ErrorDetail;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBuilder<T> {
	private int code;
	private String message;
	/*private int pgtVersionCode;
	private int pgtQuestionCount;
	private int pgtFreeMsgCount;
	private int pgtFreeMsgCountPerDay;
	private int pgtImageCount;
	private int pgtFreeImageCountPerDay;
	private boolean pgtAllowFreeMsgPerDay;
	private long pgtFreeMsgResetAt;
	private String pgtMd;
	private List<String> gptIapPackages;*/
	private T data;
	
	public static <K> ResponseBuilder<K> buildResponse(K data) {
		ResponseBuilder<K> response = new ResponseBuilder<K>();
		response.data = data;
		return response;
	}

	public static <K> ResponseBuilder<K> buildResponse(K data, String exMessage, HttpStatus status) {
		ResponseBuilder<K> response = new ResponseBuilder<K>();
		response.data = data;
		response.code = status.value();
		response.message = exMessage;
		return response;
	}

	public static <K> ResponseBuilder<K> buildResponse(K data, String exMessage) {
		ResponseBuilder<K> response = new ResponseBuilder<K>();
		response.data = data;
		response.message = exMessage;
		return response;
	}
	
	public static <K> ResponseBuilder<K>  buildApplicationException(ApiException apiEx) {
		ResponseBuilder<K> response = new ResponseBuilder<K>();
		response.code = apiEx.getErrorMessage().getCode();
		response.message = apiEx.getErrorMessage().getMessage();
        return response;
    }

    public static <K> ResponseBuilder<K>  buildApplicationException(String exMessage, int exCode) {        
        ResponseBuilder<K> response = new ResponseBuilder<K>();
		response.code = exCode;
		response.message = exMessage;
		return response;
	}

	public static ResponseBuilder<List<ErrorDetail>> buildApplicationExceptionV2(ApiException apiEx) {
		ResponseBuilder<List<ErrorDetail>> response = new ResponseBuilder<>();
		response.code = apiEx.getErrorMessage().getCode();
		response.message = apiEx.getErrorMessage().getMessage();
		if (apiEx.getErrorDetails() != null && !apiEx.getErrorDetails().isEmpty())
			response.setData(apiEx.getErrorDetails());
		return response;
	}

	public void setCode(HttpStatus status) {
		this.code = status.value();
	}
}
