package com.example.app;

import com.google.gson.annotations.SerializedName;

public class RegistrationResponse {

    @SerializedName("success")
    private final boolean success;

    @SerializedName("message")
    private final String message;

    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}