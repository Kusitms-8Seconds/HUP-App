package com.me.hurryuphup.domain.user.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.me.hurryuphup.MainActivity;
import com.me.hurryuphup.R;
import com.me.hurryuphup.databinding.ActivityLoginBinding;
import com.me.hurryuphup.domain.email.dto.CheckAuthCodeRequest;
import com.me.hurryuphup.domain.email.dto.EmailAuthCodeRequest;
import com.me.hurryuphup.domain.user.constant.Constants;
import com.me.hurryuphup.domain.user.dto.EmailResetPasswordResponse;
import com.me.hurryuphup.domain.user.dto.FindLoginIdRequest;
import com.me.hurryuphup.domain.user.dto.FindLoginIdResponse;
import com.me.hurryuphup.domain.user.dto.LoginRequest;
import com.me.hurryuphup.domain.user.dto.LoginResponse;
import com.me.hurryuphup.domain.user.dto.OAuth2GoogleLoginRequest;
import com.me.hurryuphup.domain.user.dto.OAuth2KakaoLoginRequest;
import com.me.hurryuphup.domain.user.dto.OAuth2NaverLoginRequest;
import com.me.hurryuphup.domain.user.dto.ResetPasswordRequest;
import com.me.hurryuphup.domain.user.dto.ResetPasswordResponse;
import com.me.hurryuphup.domain.user.view.LoginView;
import com.me.hurryuphup.global.dto.DefaultResponse;
import com.me.hurryuphup.global.retrofit.MainRetrofitCallback;
import com.me.hurryuphup.global.retrofit.MainRetrofitTool;
import com.me.hurryuphup.global.retrofit.RetrofitTool;
import com.me.hurryuphup.global.util.ErrorMessageParser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lombok.SneakyThrows;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class LoginPresenter implements LoginPresenterInterface {
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 0116;  //google login request code
    private int NOT_SELECTED = 12501;
    OAuthLogin mOAuthLoginModule;
    //find id dialog
    Dialog findIDDialog;
    //reset Pw dialog
    Dialog resetPWDialog;
    // userId
    Long userId;
    
    // Attributes
    private LoginView loginView;
    private ActivityLoginBinding binding;
    private Context context;
    private Activity activity;
    ErrorMessageParser errorMessageParser;

    // Constructor
    public LoginPresenter(LoginView loginView, ActivityLoginBinding binding, Context context, Activity activity){
        this.loginView = loginView;
        this.binding = binding;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void appLoginCallback(LoginRequest loginRequest) throws InterruptedException {
        RetrofitTool.getAPIWithNullConverter().login(loginRequest)
                .enqueue(MainRetrofitTool.getCallback(new LoginCallback()));
        Thread.sleep(1000);
        goMain();
    }

    @Override
    public void kakaoLoginCallback(String accessToken) throws InterruptedException {
        OAuth2KakaoLoginRequest oAuth2KakaoLoginRequest = new OAuth2KakaoLoginRequest(accessToken, Constants.targetToken);
        RetrofitTool.getAPIWithNullConverter().kakaoAccessTokenValidation(oAuth2KakaoLoginRequest)
                .enqueue(MainRetrofitTool.getCallback(new LoginCallback()));
        Thread.sleep(1000);
        goMain();
    }

    @Override
    public void googleLoginCallback(String idToken) throws InterruptedException {
        OAuth2GoogleLoginRequest oAuth2GoogleLoginRequest = new OAuth2GoogleLoginRequest(idToken, Constants.targetToken);
        RetrofitTool.getAPIWithNullConverter().googleIdTokenValidation(oAuth2GoogleLoginRequest)
                .enqueue(MainRetrofitTool.getCallback(new LoginCallback()));
        Thread.sleep(1000);
        goMain();
    }

    @Override
    public void naverLoginCallback(String accessToken) throws InterruptedException {
        OAuth2NaverLoginRequest oAuth2NaverLoginRequest = new OAuth2NaverLoginRequest(accessToken, Constants.targetToken);
        RetrofitTool.getAPIWithNullConverter().naverAccessTokenValidation(oAuth2NaverLoginRequest)
                .enqueue(MainRetrofitTool.getCallback(new LoginCallback()));
        Thread.sleep(1000);
        goMain();
    }

    @Override
    public void kakaoLogin() {
        //kakao login
        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);
        session.open(AuthType.KAKAO_LOGIN_ALL, activity);
    }

    @Override
    public void googleLogin() throws InterruptedException {
        // ?????? ????????? ????????? ???????????? ??????????????? ????????? ????????? ????????????.
        // DEFAULT_SIGN_IN parameter??? ????????? ID??? ???????????? ????????? ????????? ??????????????? ????????????.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.ELoginCallback.eGoogleRequestIdToken.getText())
                .requestEmail() // email addresses??? ?????????
                .build();

        // ????????? ?????? GoogleSignInOptions??? ????????? GoogleSignInClient ????????? ??????
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        // ????????? ????????? ?????? ????????? ????????????.
//        GoogleSignInAccount gsa = GoogleSignIn.getLastSignedInAccount(context);
//        // ????????? ???????????? ??????
//        if (gsa != null) {
//            String idToken = gsa.getIdToken();
//            googleLoginCallback(idToken);
//        } else
            googleSignIn();
    }

    @Override
    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void naverSignIn() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                context
                ,context.getString(R.string.naver_client_id)
                ,context.getString(R.string.naver_client_secret)
                ,context.getString(R.string.naver_client_name)
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 ??????????????? OAUTH_CALLBACK_INTENT????????? ???????????? ????????????.
        );

        @SuppressLint("HandlerLeak")
        OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
            @SneakyThrows
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mOAuthLoginModule.getAccessToken(context);
                    String refreshToken = mOAuthLoginModule.getRefreshToken(context);
                    long expiresAt = mOAuthLoginModule.getExpiresAt(context);
                    String tokenType = mOAuthLoginModule.getTokenType(context);

                    Log.i(Constants.ELoginCallback.eNaverTAG.getText(),Constants.ELoginCallback.eAccessToken.getText()+ accessToken);
                    Log.i(Constants.ELoginCallback.eNaverTAG.getText(),Constants.ELoginCallback.eRefreshToken.getText()+ refreshToken);
                    Log.i(Constants.ELoginCallback.eNaverTAG.getText(),Constants.ELoginCallback.eExpiresAt.getText()+ expiresAt);
                    Log.i(Constants.ELoginCallback.eNaverTAG.getText(),Constants.ELoginCallback.eTokenType.getText()+ tokenType);

                    naverLoginCallback(accessToken);

                } else {
                    String errorCode = mOAuthLoginModule
                            .getLastErrorCode(context).getCode();
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(context);
                    Log.i(Constants.ELoginCallback.eNaverTAG.getText(),errorCode + errorDesc);
                }
            };
        };
        mOAuthLoginModule.startOauthLoginActivity(activity, mOAuthLoginHandler);
    }

    @Override
    public void showFindIDDialog(Dialog dialog) {
        this.findIDDialog = dialog;
        findIDDialog.show(); // ??????????????? ?????????

        EditText email_tv = findIDDialog.findViewById(R.id.edit_email);
        // ?????? ??????
        findIDDialog.findViewById(R.id.bt_find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_tv.getText().toString();
                FindLoginIdRequest findLoginIdRequest = new FindLoginIdRequest(email);
                RetrofitTool.getAPIWithNullConverter().findId(findLoginIdRequest)
                        .enqueue(MainRetrofitTool.getCallback(new FindIDCallback()));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email_tv.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void showResetPWDialog(Dialog dialog) {
        this.resetPWDialog = dialog;
        resetPWDialog.show();

        //attributes
        EditText email_tv = resetPWDialog.findViewById(R.id.edit_email);
        Button sendEmail = resetPWDialog.findViewById(R.id.bt_send_email);
        Button resendEmail = resetPWDialog.findViewById(R.id.btn_reSend);
        ConstraintLayout ly_codeCheck = resetPWDialog.findViewById(R.id.ly_check_authcode);
        ConstraintLayout ly_reset_pw = resetPWDialog.findViewById(R.id.ly_reset_pw);
        EditText edt_codeCheck = resetPWDialog.findViewById(R.id.edt_codeCheck);
        Button btn_codeCheck = resetPWDialog.findViewById(R.id.btn_codeCheck);
        EditText edt_reset_pw = resetPWDialog.findViewById(R.id.edt_reset_pw);
        EditText edt_reset_pw_check = resetPWDialog.findViewById(R.id.edt_reset_pw_check);
        Button btn_reset_pw = resetPWDialog.findViewById(R.id.btn_reset_pw);

        ly_codeCheck.setVisibility(View.GONE);
        ly_reset_pw.setVisibility(View.GONE);

        class checkCodeCallback implements MainRetrofitCallback<EmailResetPasswordResponse> {
            @Override
            public void onSuccessResponse(Response<EmailResetPasswordResponse> response) {
                loginView.showToast("????????? ?????? ??????");
                ly_reset_pw.setVisibility(View.VISIBLE);
                Constants.userId = response.body().getUserId();
                Log.d(TAG, "checking code success, idToken: " + response.body().toString());
            }
            @Override
            public void onFailResponse(Response<EmailResetPasswordResponse> response) throws IOException, JSONException {
                errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
                ly_reset_pw.setVisibility(View.GONE);
                Log.d(TAG, "onFailResponse");
            }
            @Override
            public void onConnectionFail(Throwable t) {
                Log.e("????????????", t.getMessage());
            }
        }

        // ?????? ??????
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_tv.getText().toString();
                EmailAuthCodeRequest emailAuthCodeRequest = new EmailAuthCodeRequest(email);
                RetrofitTool.getAPIWithNullConverter().sendResetPWAuthCode(emailAuthCodeRequest)
                        .enqueue(MainRetrofitTool.getCallback(new sendEmailCallback()));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email_tv.getWindowToken(), 0);
                sendEmail.setEnabled(false);
                sendEmail.setBackgroundColor(Color.GRAY);
                ly_codeCheck.setVisibility(View.VISIBLE);
            }
        });
        // ????????? ??????
        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_tv.getText().toString();
                EmailAuthCodeRequest emailAuthCodeRequest = new EmailAuthCodeRequest(email);
                RetrofitTool.getAPIWithNullConverter().sendResetPWAuthCode(emailAuthCodeRequest)
                        .enqueue(MainRetrofitTool.getCallback(new sendEmailCallback()));
//                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(email_tv.getWindowToken(), 0);
            }
        });
        // ???????????? ?????? ??????
        btn_codeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkCode = edt_codeCheck.getText().toString();
                CheckAuthCodeRequest checkAuthCodeRequest = new CheckAuthCodeRequest(checkCode);
                RetrofitTool.getAPIWithNullConverter().resetPWAuthCode(checkAuthCodeRequest)
                        .enqueue(MainRetrofitTool.getCallback(new checkCodeCallback()));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_codeCheck.getWindowToken(), 0);
            }
        });
        // ???????????? ????????? ??????
        btn_reset_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = edt_reset_pw.getText().toString();
                String passwordCheck = edt_reset_pw_check.getText().toString();
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(Constants.userId, password, passwordCheck);
                RetrofitTool.getAPIWithNullConverter().resetPW(resetPasswordRequest)
                        .enqueue(MainRetrofitTool.getCallback(new ResetPWCallback()));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_reset_pw_check.getWindowToken(), 0);
            }
        });
        //TODO : ??????
    }

    @Override
    public void onDestroy() {
        // ?????? ?????? ??????
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ????????????|????????? ??????????????? ?????? ????????? ????????? SDK??? ??????
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // google
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        if (requestCode == NOT_SELECTED) { return; }   // google account ?????? ??? ?????? ???
    }

    @Override
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                String idToken = acct.getIdToken();

                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.eNickname.getText() +personName);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.ePersonGivenName.getText()+personGivenName);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.eEmail.getText()+personEmail);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.ePersonId.getText()+personId);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.ePersonFamilynName.getText()+personFamilyName);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.eProfilImg.getText()+personPhoto);
                Log.d(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.eAccessToken.getText()+idToken);

                googleLoginCallback(idToken);
            }
        } catch (ApiException | InterruptedException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(Constants.ELoginCallback.eGoogleTAG.getText(), Constants.ELoginCallback.eGoogleErrorCode.getText() + e.getMessage());

        }
    }

    @Override
    public void goMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    private class LoginCallback implements MainRetrofitCallback<LoginResponse> {
        @Override
        public void onSuccessResponse(Response<LoginResponse> response) {
            Constants.userId = response.body().getUserId();
            Constants.accessToken = response.body().getAccessToken();
            Constants.refreshToken = response.body().getRefreshToken();
            Log.d(Constants.ELoginCallback.TAG.getText(), Constants.ELoginCallback.eSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<LoginResponse> response) throws IOException, JSONException {
            ErrorMessageParser errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            loginView.showToast(errorMessageParser.getParsedErrorMessage());
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                Log.d(Constants.ELoginCallback.TAG.getText(), jObjError.getString("error"));
            } catch (Exception e) {
                Log.d(Constants.ELoginCallback.TAG.getText(), e.getMessage());
            }
            Log.d(Constants.ELoginCallback.TAG.getText(), Constants.ELoginCallback.eFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e(Constants.ELoginCallback.eConnectionFail.getText(), t.getMessage());
        }
    }
    
    //kakao session callback
    public class SessionCallback implements ISessionCallback {
        // ???????????? ????????? ??????
        @Override
        public void onSessionOpened() {
            requestMe();
        }
        // ???????????? ????????? ??????
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e(Constants.ELoginCallback.eKakaoSessionCallback.getText(), Constants.ELoginCallback.eSessionOpenFailed.getText() + exception.getMessage());
        }
        // ????????? ?????? ??????
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) { Log.e(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eKakaoSessionError.getText()+ errorResult); }
                        @Override
                        public void onFailure(ErrorResult errorResult) { Log.e(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eKakaoUserError.getText() + errorResult); }
                        @SneakyThrows
                        @Override
                        public void onSuccess(MeV2Response result) {
//                            onBackPressed();

                            Log.i(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.ePersonId.getText() + result.getId());

                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {
                                // ?????????
                                String email = kakaoAccount.getEmail();
                                if (email != null) {
                                    Log.i(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eEmail.getText()  + email);
                                } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ??????
                                    // ???, ?????? ????????? ???????????? ????????? ????????? ?????? ???????????? ????????? ????????? ????????? ???????????? ???????????? ?????????.
                                } else {
                                    // ????????? ?????? ??????
                                }
                                // ?????????
                                Profile profile = kakaoAccount.getProfile();

                                if (profile != null) {
                                    Log.d(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eNickname.getText()  + profile.getNickname());
                                    Log.d(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eProfilImg.getText()  + profile.getProfileImageUrl());
                                    Log.d(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eThumbnailImg.getText()  + profile.getThumbnailImageUrl());
                                    String accessToken = AuthApiClient.getInstance().getTokenManagerProvider()
                                            .getManager().getToken().getAccessToken();
                                    Log.d(Constants.ELoginCallback.eKakaoTAG.getText(), Constants.ELoginCallback.eAccessToken.getText() + accessToken);

                                    kakaoLoginCallback(accessToken);

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // ?????? ?????? ??? ????????? ?????? ?????? ??????
                                } else {
                                    // ????????? ?????? ??????
                                }
                            }
                        }
                    });
        }
    }
    private class FindIDCallback implements MainRetrofitCallback<FindLoginIdResponse> {
        @Override
        public void onSuccessResponse(Response<FindLoginIdResponse> response) {
            String loginId = response.body().getLoginId();
            Long userId = response.body().getUserId();

            LinearLayout ly = findIDDialog.findViewById(R.id.ly_show_id);
            TextView tv_showId = findIDDialog.findViewById(R.id.tv_show_id);
            ly.setVisibility(View.VISIBLE);
            tv_showId.setText(loginId);
            Log.d(Constants.ELoginCallback.TAG.getText(), Constants.ELoginCallback.eSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<FindLoginIdResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);

            LinearLayout ly = findIDDialog.findViewById(R.id.ly_show_id);
            ly.setVisibility(View.GONE);
            Log.d("findID", Constants.ELoginCallback.eFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e(Constants.ELoginCallback.eConnectionFail.getText(), t.getMessage());
        }
    }
    class sendEmailCallback implements MainRetrofitCallback<DefaultResponse> {
        @Override
        public void onSuccessResponse(Response<DefaultResponse> response) {
            Log.d(TAG, "sending email success, idToken: " + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<DefaultResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, errorMessageParser.getParsedErrorMessage());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("????????????", t.getMessage());
        }
    }
    private class ResetPWCallback implements MainRetrofitCallback<ResetPasswordResponse> {
        @Override
        public void onSuccessResponse(Response<ResetPasswordResponse> response) {
            loginView.showToast("???????????? ???????????? ??????????????????.");
            resetPWDialog.dismiss();
            Log.d(Constants.ELoginCallback.TAG.getText(), Constants.ELoginCallback.eSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<ResetPasswordResponse> response) throws IOException, JSONException {
            ErrorMessageParser errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            loginView.showToast(errorMessageParser.getParsedErrorMessage());
            Log.d("findID", Constants.ELoginCallback.eFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e(Constants.ELoginCallback.eConnectionFail.getText(), t.getMessage());
        }
    }
}
