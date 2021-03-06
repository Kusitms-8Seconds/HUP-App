package com.me.hurryuphup.global.retrofit;

import androidx.annotation.Nullable;

import com.me.hurryuphup.domain.chat.dto.ChatMessageResponse;
import com.me.hurryuphup.domain.chat.dto.ChatRoomResponse;
import com.me.hurryuphup.domain.chat.dto.IsEnterChatRoomRequest;
import com.me.hurryuphup.domain.chat.dto.IsEnterChatRoomResponse;
import com.me.hurryuphup.domain.item.constant.ItemConstants;
import com.me.hurryuphup.domain.item.dto.BestItemResponse;
import com.me.hurryuphup.domain.mypage.notice.dto.NoticeListResponse;
import com.me.hurryuphup.domain.mypage.notice.dto.NoticeResponse;
import com.me.hurryuphup.domain.mypage.notice.dto.UpdateNoticeResponse;
import com.me.hurryuphup.domain.notification.dto.NotificationListResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.PriceSuggestionRequest;
import com.me.hurryuphup.domain.pricesuggestion.dto.PriceSuggestionResponse;
import com.me.hurryuphup.domain.user.dto.EmailResetPasswordResponse;
import com.me.hurryuphup.domain.user.dto.FindLoginIdRequest;
import com.me.hurryuphup.domain.user.dto.FindLoginIdResponse;
import com.me.hurryuphup.domain.user.dto.LogoutRequest;
import com.me.hurryuphup.domain.user.dto.ResetPasswordRequest;
import com.me.hurryuphup.domain.user.dto.ResetPasswordResponse;
import com.me.hurryuphup.domain.user.dto.TokenInfoResponse;
import com.me.hurryuphup.domain.user.dto.UpdateProfileResponse;
import com.me.hurryuphup.domain.user.dto.UpdateUserRequest;
import com.me.hurryuphup.domain.user.dto.UpdateUserResponse;
import com.me.hurryuphup.global.dto.DefaultResponse;
import com.me.hurryuphup.domain.item.dto.GetAllItemsByStatusRequest;
import com.me.hurryuphup.domain.item.dto.ItemDetailsResponse;
import com.me.hurryuphup.domain.item.dto.RegisterItemResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.BidderResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.MaximumPriceResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.ParticipantsResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.PriceSuggestionListResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapCheckedRequest;
import com.me.hurryuphup.domain.scrap.dto.ScrapCheckedResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapCountResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapDetailsResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapRegisterRequest;
import com.me.hurryuphup.domain.scrap.dto.ScrapRegisterResponse;
import com.me.hurryuphup.domain.email.dto.CheckAuthCodeRequest;
import com.me.hurryuphup.domain.email.dto.EmailAuthCodeRequest;
import com.me.hurryuphup.domain.user.dto.LoginRequest;
import com.me.hurryuphup.domain.user.dto.OAuth2GoogleLoginRequest;
import com.me.hurryuphup.domain.user.dto.LoginResponse;
import com.me.hurryuphup.domain.user.dto.OAuth2KakaoLoginRequest;
import com.me.hurryuphup.domain.user.dto.OAuth2NaverLoginRequest;
import com.me.hurryuphup.domain.user.dto.SignUpRequest;
import com.me.hurryuphup.domain.user.dto.SignUpResponse;
import com.me.hurryuphup.domain.user.dto.UserInfoResponse;
import com.me.hurryuphup.global.dto.PaginationDto;
import com.me.hurryuphup.global.firebase.FCMRequest;
import com.me.hurryuphup.global.firebase.FCMResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestAPI {

    @POST("api/v1/users")   //????????? ??????
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);
    @PUT("api/v1/users")    //????????? ?????? ??????
    Call<UpdateUserResponse> updateUser(@Body UpdateUserRequest updateUserRequest);
    @Multipart
    @PUT("api/v1/users/images")    //????????? ????????? ?????? ??????
    Call<UpdateProfileResponse> updateUserProfileImg(@Part MultipartBody.Part file,
                                                     @Part("userId") RequestBody userId);
    //email
    @POST("api/v1/email/send/activate-user")   //???????????? ??? ????????? ??????
    Call<DefaultResponse> sendActivateAuthCode(@Body EmailAuthCodeRequest emailAuthCodeRequest);
    @POST("api/v1/email/send/reset-password")   //?????? ????????? ??? ????????? ??????
    Call<DefaultResponse> sendResetPWAuthCode(@Body EmailAuthCodeRequest emailAuthCodeRequest);
    @POST("api/v1/email/verify/activate-user")   //?????? ??????????????? ?????? ???????????? ??????
    Call<DefaultResponse> checkAuthCode(@Body CheckAuthCodeRequest checkAuthCodeRequest);
    @POST("api/v1/email/verify/reset-password")   //???????????? ????????? ???????????? ??????
    Call<EmailResetPasswordResponse> resetPWAuthCode(@Body CheckAuthCodeRequest checkAuthCodeRequest);

    //????????? ??????/?????? ?????????
    @POST("api/v1/users/find-id") //????????? ??????
    Call<FindLoginIdResponse> findId(@Body FindLoginIdRequest findLoginIdRequest);
    @POST("api/v1/users/reset-password") //???????????? ?????????
    Call<ResetPasswordResponse> resetPW(@Body ResetPasswordRequest resetPasswordRequest);

    @POST("api/v1/users/login") //?????????
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("api/v1/users/login") //????????????
    Call<DefaultResponse> logout(@Body LogoutRequest logoutRequest);
    @POST("api/v1/users/reissue") //?????? ?????????
    Call<TokenInfoResponse> reissue(@Body LogoutRequest logoutRequest);
    @GET("api/v1/users/{id}")      //????????? ?????? ??????
    Call<UserInfoResponse> userDetails(@Path("id") Long userId);
    @DELETE("api/v1/users/{id}")      //????????? ??????
    Call<DefaultResponse> deleteUser(@Path("id") Long userId);

    @POST("api/v1/users/google-login")      //?????? ?????????
    Call<LoginResponse> googleIdTokenValidation(@Body OAuth2GoogleLoginRequest oAuth2GoogleLoginRequest);
    @POST("api/v1/users/kakao-login")       //????????? ?????????
    Call<LoginResponse> kakaoAccessTokenValidation(@Body OAuth2KakaoLoginRequest oAuth2KakaoLoginRequest);
    @POST("api/v1/users/naver-login")       //????????? ?????????
    Call<LoginResponse> naverAccessTokenValidation(@Body OAuth2NaverLoginRequest oAuth2NaverLoginRequest);
    @GET("api/v1/users/check/{loginId}")       //????????? ????????????
    Call<DefaultResponse> checkDuplicateId(@Path("loginId") String loginId);

    @Multipart
    @POST("api/v1/items")   //????????? ??????
    Call<RegisterItemResponse> uploadItem(@Part List<MultipartBody.Part> files,
                                          @Part("userId") RequestBody userId,
                                          @Part("itemName") RequestBody itemName,
                                          @Part("category") RequestBody category,
                                          @Part("initPrice") RequestBody initPrice,
                                          @Part("buyDate") RequestBody buyDate,
                                          @Part("itemStatePoint") RequestBody itemStatePoint,
                                          @Part("auctionClosingDate") RequestBody auctionClosingDate,
                                          @Part("description") RequestBody description);
    @GET("api/v1/items/{id}")   //????????? ??????
    Call<ItemDetailsResponse> getItem(@Path("id") Long id);
    @GET("api/v1/items/statuses/{itemSoldStatus}")     //????????? ??????????????? ??????
    Call<PaginationDto<List<ItemDetailsResponse>>> getAllItemsInfo(@Path("itemSoldStatus") ItemConstants.EItemSoldStatus itemSoldStatus);
    @GET("api/v1/items/statuses/hearts/{itemSoldStatus}")  //????????? ???????????????, ????????? ?????? ??? ??????
    Call<List<BestItemResponse>> getBestItems(@Path("itemSoldStatus") ItemConstants.EItemSoldStatus itemSoldStatus);
    @GET("api/v1/items/categories/{category}")      //????????? ??????????????? ??????
    Call<PaginationDto<List<ItemDetailsResponse>>> getAllItemsByCategory(@Path("category") String category);
    @POST("api/v1/items/users")      //????????? ????????? ????????? ???????????? ??????
    Call<PaginationDto<List<ItemDetailsResponse>>> getAllItemsByUserIdAndStatus(@Body GetAllItemsByStatusRequest getAllItemsByStatusRequest);
    @DELETE("/api/v1/items/{id}")   //????????? ??????
    Call<DefaultResponse> deleteItem(@Path("id") Long id);

    @POST("api/v1/scraps")   //scrap create
    Call<ScrapRegisterResponse> createScrap(@Body ScrapRegisterRequest scrapRegisterRequest);
    @DELETE("api/v1/scraps/{scrapId}")   //scrap delete
    Call<DefaultResponse> deleteHeart(@Path("scrapId") Long scrapId);
    @GET("api/v1/scraps/hearts/{itemId}")     //????????? ????????? ??? ??????
    Call<ScrapCountResponse> getHeart(@Path("itemId") Long itemId);
    @POST("api/v1/scraps/hearts")   //????????? ?????? ????????? ????????? ?????? ??? ??????
    Call<ScrapCheckedResponse> isCheckedHeart(@Body ScrapCheckedRequest scrapCheckedRequest);
    @GET("api/v1/scraps/users/{userId}")      //????????? ????????? ?????? ??????
    Call<PaginationDto<List<ScrapDetailsResponse>>> getAllScrapsByUserId(@Path("userId") Long userId);

    @GET("api/v1/priceSuggestions/{itemId}")   //?????? ????????? ?????? ???????????? ??????
    Call<PaginationDto<List<PriceSuggestionListResponse>>> getAllPriceSuggestionByItemId(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestions/bidders/{itemId}")      //?????? ????????? ???????????? ?????? ??????
    Call<BidderResponse> getBidder(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestions/maximumPrice/{itemId}")    //?????? ????????? ?????????????????? ??????
    Call<MaximumPriceResponse> getMaximumPrice(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestions/participants/{itemId}")     //?????? ????????? ???????????? ??????
    Call<ParticipantsResponse> getParticipants(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestions/users/{userId}")   //?????? ????????? ?????? ???????????? ??????
    Call<PaginationDto<List<PriceSuggestionListResponse>>> getAllPriceSuggestionByUserId(@Path("userId") Long userId);
    @POST("api/v1/priceSuggestions/priceSuggestionTest")   //????????????
    Call<PriceSuggestionResponse> priceSuggest(@Body PriceSuggestionRequest priceSuggestionRequest);

    //FCM
    @POST("api/v1/items/sold")   //????????????
    Call<FCMResponse> pushMessage(@Body FCMRequest fcmRequest);
    //notification
    @GET("api/v1/notifications/{userId}")     //????????? ?????? ?????? ??????
    Call<PaginationDto<List<NotificationListResponse>>> getNotificationList(@Path("userId") Long id,
                                                                            @Query("page") int page, @Query("size") int size);

    //????????????
    @GET("api/v1/notices")     //???????????? ?????? ?????? ??????
    Call<PaginationDto<List<NoticeListResponse>>> getAllNotice();
    @Multipart
    @PUT("api/v1/notices")     //???????????? ??????
    Call<UpdateNoticeResponse> updateNotice(@Part List<MultipartBody.Part> files,
                                            @Part("title") RequestBody title,
                                            @Part("body") RequestBody body,
                                            @Part("userId") RequestBody userId,
                                            @Part("noticeId") RequestBody noticeId);
    @Multipart
    @POST("api/v1/notices")     //???????????? ??????
    Call<NoticeResponse> uploadNotice(@Nullable @Part List<MultipartBody.Part> files,
                                      @Part("title") RequestBody title,
                                      @Part("body") RequestBody body,
                                      @Part("userId") RequestBody userId);
    @GET("api/v1/notices/{noticeId}")     //???????????? ?????? ??????
    Call<NoticeResponse> getNotice(@Path("noticeId") Long noticeId);
    @DELETE("api/v1/notices/{noticeId}")     //???????????? ??????
    Call<DefaultResponse> deleteNotice(@Path("noticeId") Long noticeId);

    //chat
    @GET("api/v1/chatRooms/{id}")     //????????? ?????? ????????? ??????
    Call<List<ChatRoomResponse>> getChatRooms(@Path("id") Long id);
    @GET("api/v1/chatMessages/{id}")     //???????????? ?????? ????????? ??????
    Call<PaginationDto<List<ChatMessageResponse>>> getChatMessages(@Path("id") Long id,
                                                                   @Query("page") int page, @Query("size") int size);
    @POST("api/v1/chatRooms/entry")   //?????? ????????? ????????? ???????????? ??????
    Call<IsEnterChatRoomResponse> isChatRoomEntered(@Body IsEnterChatRoomRequest isEnterChatRoomRequest);
}