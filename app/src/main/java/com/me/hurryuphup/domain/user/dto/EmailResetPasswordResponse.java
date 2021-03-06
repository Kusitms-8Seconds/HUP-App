package com.me.hurryuphup.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailResetPasswordResponse {
    private Long userId;
    private String loginId;

    public static EmailResetPasswordResponse from(Long userId, String loginId) {
        EmailResetPasswordResponse emailResetPasswordResponse = EmailResetPasswordResponse.builder()
                .userId(userId)
                .loginId(loginId)
                .build();
        return emailResetPasswordResponse;
    }
}