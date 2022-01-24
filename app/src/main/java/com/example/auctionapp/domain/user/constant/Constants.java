package com.example.auctionapp.domain.user.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Constants {

    public static Long userId = null;
    public static String token = null;
    public static final String imageBaseUrl = "http://52.78.175.27:8080/image/";

    @Getter
    @NoArgsConstructor
    public enum ESignUp{
        emailDuplicateMessage("이미 사용하고 있는 이메일입니다."),
        emailNonDuplicateMessage("사용가능한 이메일입니다."),
        emailFormatErrorMessage("이메일을 알맞은 형태로 입력해주세요."),
        emailLetterCountError("이메일을 10~20자로 입력해주세요."),
        emailFormat("\\w+@\\w+\\.\\w+(\\.\\w+)?"),
        pwEnglishNumberFormat("^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$"),
        idWarningMessage("아이디는 최소 5글자 이상 10글자 이하여야 합니다."),
        pwInputMessage("비밀번호창에 비밀번호를 입력해주세요."),
        pwWarningMessage("비밀번호는 영어, 숫자, 특수문자를 조합해 8~12자리로 입력해주세요."),
        pwNotMatchMessage("비밀번호가 서로 일치하지 않습니다."),
        nameInputMessage("이름을 입력해주세요."),
        nameInput2Message("이름은 최소 2글자 이상 10글자 이하여야 합니다."),
        phoneNumberInputMessage("휴대폰번호를 입력해주세요,"),
        phoneNumberFormat("^\\d{3}-\\d{3,4}-\\d{4}$"),
        phoneNumberErrorMessage("올바른 휴대폰 번호를 입력해주세요."),
        genderCheckMessage("성별을 체크해주세요."),
        agreeCheckMessage("약관에 동의해주세요."),
        completeSignUpMessage("회원가입을 완료했습니다."),
        dpYear("년"),
        dpMonth("월"),
        dpDay("일"),
        emailAppType("APP"),
        userEmail("userEmail"),
        userPassword("userPassword");

        private String text;
        private ESignUp(String text){
            this.text=text;
        }
        public String getText(){
            return this.text;
        }

    }

    @Getter
    @NoArgsConstructor
    public enum ELoginType{
        eGoogle("구글"),
        eNaver("네이버"),
        eKakao("카카오"),
        eApp("앱");

//        private final String name;

        private String text;
        private ELoginType(String text){
            this.text=text;
        }
        public String getText(){
            return this.text;
        }
    }

    @Getter
    @NoArgsConstructor
    public enum ELoginCallback{
        TAG("LoginCallback:"),
        eUnauthorized("Unauthorized"),
        eForbidden("Forbidden"),
        eNotFound("Not Found"),

        eSuccessResponse("login retrofit success, idToken: "),
        eFailResponse("onFailResponse"),
        eConnectionFail("연결실패"),

        eGoogleRequestIdToken("221537301769-e1qd8130nulhheiqo68nv8upistikcp4.apps.googleusercontent.com"),

        eKakaoTAG("KAKAO_API"),
        eGoogleTAG("GOOGLE_API"),
        eNaverTAG("NAVER_API"),

        ePersonId("사용자 아이디: "),
        eEmail("email: "),
        eNickname("nickname: "),
        ePersonGivenName("personGivenName: "),
        ePersonFamilynName("personFamilynName: "),
        eProfilImg("profile image: "),
        eThumbnailImg("thumbnail image: "),
        eAccessToken("accessToken: "),
        eRefreshToken("refreshToken : "),
        eExpiresAt("expiresAt: "),
        eTokenType("tokenType : "),

        eKakaoSessionCallback("SessionCallback :: "),
        eSessionOpenFailed("onSessionOpenFailed: "),
        eKakaoSessionError("세션이 닫혀 있음: "),
        eKakaoUserError("사용자 정보 요청 실패: "),
        eGoogleErrorCode("signInResult:failed code=");



        private String text;
        private ELoginCallback(String text){
            this.text=text;
        }
        public String getText(){
            return this.text;
        }
    }
}
