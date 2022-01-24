package com.example.auctionapp.domain.mypage.presenter;

public interface Presenter {
    void init();
    void getUserInfo();
    void socialLogOut();

    void exceptionToast(String tag, int statusCode);
}
