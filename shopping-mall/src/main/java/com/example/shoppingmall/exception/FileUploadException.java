package com.example.shoppingmall.exception;

/**
 * 이미지 업로드/썸네일 처리 실패.
 */
public class FileUploadException extends RuntimeException {

	public FileUploadException(String message, Throwable cause) {
		super(message, cause);
	}
}
