package com.me.hurryuphup.domain.user.dto;

import com.me.hurryuphup.domain.user.constant.Constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponse {

    private Long userId;
    private String loginId;
    private String email;
    private String username;
    private String phoneNumber;
    private String picture;
    private boolean activated;
    private boolean emailAuthActivated;
    private Constants.ELoginType loginType;

}