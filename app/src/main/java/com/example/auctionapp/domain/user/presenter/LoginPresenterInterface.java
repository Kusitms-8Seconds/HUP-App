package com.example.auctionapp.domain.user.presenter;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.auctionapp.domain.user.dto.LoginRequest;
import com.example.auctionapp.domain.user.dto.OAuth2GoogleLoginRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public interface LoginPresenterInterface {

    void appLoginCallback(LoginRequest loginRequest);
    void kakaoLoginCallback(String accessToken);
    void googleLoginCallback(OAuth2GoogleLoginRequest oAuth2GoogleLoginRequest);
    void naverLoginCallback(String accessToken);

    void kakaoLogin();
    void googleLogin();
    void googleSignIn();
    void naverSignIn();

    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
    void handleSignInResult(Task<GoogleSignInAccount> completedTask);
}
