package com.viehai.identity_service.identity.domain.exception;

public enum ErrorCode {
    UNCATEGORISED_ERROR(9999, "Uncategorised error"),
    USER_EXISTS(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters long"),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters long"),
    INVALID_KEY(1005, "Invalid key"),
    USER_NOT_EXISTED(1006, "User not existed"),
    JOB_CODE_EXISTS(1007, "Job code already exists"),
    JOB_NOT_FOUND(1008, "Job not found");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
