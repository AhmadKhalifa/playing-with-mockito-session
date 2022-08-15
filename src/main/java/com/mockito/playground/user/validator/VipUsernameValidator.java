package com.mockito.playground.user.validator;

public class VipUsernameValidator implements Validator<String> {

    private static final String VIP_PART = "VIP";

    @Override
    public boolean isValid(String value) {
        return !value.contains(VIP_PART);
    }
}
