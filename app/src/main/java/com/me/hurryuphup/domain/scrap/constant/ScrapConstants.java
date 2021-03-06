package com.me.hurryuphup.domain.scrap.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ScrapConstants {

    public enum EScrapCallback {
        eScrapTAG("Scrap: "),
        egetAllScrapsCallback("getAllScrapsCallback::"),
        egetMaximumPriceCallback("getMaximumPriceCallback::"),

        dpMinute("분"),
        logItemId("itemId: "),
        rtSuccessResponse("retrofit success, idToken: "),
        rtFailResponse("onFailResponse"),
        rtConnectionFail("연결실패");

        private String text;
        private EScrapCallback(String text) {
            this.text = text;
        }
        public String getText() { return this.text; }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum EScrapServiceImpl {
        eAlreadyScrapExceptionMessage("이미 스크랩한 상품입니다."),
        eNotExistingScrapOfUserExceptionMessage("해당 유저의 스크랩 내역이 존재하지 않습니다."),
        eNotFoundItemExceptionMessage("존재하지 않는 스크랩입니다.");
        private String value;
    }
}
