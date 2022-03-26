package com.example.auctionapp.domain.item.dto;

import com.example.auctionapp.domain.item.constant.ItemConstants;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BestItemResponse {

    private Long id;
    private Long userId;
    private String itemName;
    private ItemConstants.EItemCategory category;
    private int initPrice;
    private LocalDateTime buyDate;
    private int itemStatePoint;
    private String description;
    private ItemConstants.EItemSoldStatus soldStatus;
    private List<String> fileNames;
    private LocalDateTime auctionClosingDate;
    private int heart;
    private int maximumPrice;
}
