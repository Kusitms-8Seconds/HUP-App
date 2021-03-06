package com.me.hurryuphup.domain.scrap.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.me.hurryuphup.databinding.ActivityScrapBinding;
import com.me.hurryuphup.domain.item.view.ItemDetail;
import com.me.hurryuphup.domain.scrap.adapter.ScrapAdapter;
import com.me.hurryuphup.domain.scrap.constant.ScrapConstants;
import com.me.hurryuphup.domain.scrap.dto.ScrapDetailsResponse;
import com.me.hurryuphup.domain.scrap.model.ScrapItem;
import com.me.hurryuphup.domain.scrap.view.ScrapView;
import com.me.hurryuphup.domain.user.constant.Constants;
import com.me.hurryuphup.global.dto.PaginationDto;
import com.me.hurryuphup.global.retrofit.MainRetrofitCallback;
import com.me.hurryuphup.global.retrofit.MainRetrofitTool;
import com.me.hurryuphup.global.retrofit.RetrofitTool;
import com.me.hurryuphup.global.util.ErrorMessageParser;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ScrapPresenter implements Presenter{
    ScrapAdapter adapter;
    ScrapItem data;
    List<ScrapItem> scrapItemsList = new ArrayList<>();
    int maximumPriceCount;
    DecimalFormat myFormatter = new DecimalFormat("###,###");

    // Attributes
    private ScrapView scrapView;
    private ActivityScrapBinding mBinding;
    private Context context;
    ErrorMessageParser errorMessageParser;

    // Constructor
    public ScrapPresenter(ScrapView scrapView, ActivityScrapBinding mBinding, Context getApplicationContext){
        this.scrapView = scrapView;
        this.mBinding = mBinding;
        this.context = getApplicationContext;
    }

    @Override
    public void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mBinding.scrapRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ScrapAdapter();
        mBinding.scrapRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ScrapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(context, ItemDetail.class);
                intent.putExtra("itemId", scrapItemsList.get(position).getItemId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        //?????????
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.scrapRecyclerView.getContext(), new LinearLayoutManager(context).getOrientation());
        mBinding.scrapRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void getData() {
        scrapItemsList = new ArrayList<>();
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).getAllScrapsByUserId(Constants.userId)
                .enqueue(MainRetrofitTool.getCallback(new getAllScrapsCallback()));
    }

    private class getAllScrapsCallback implements MainRetrofitCallback<PaginationDto<List<ScrapDetailsResponse>>> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSuccessResponse(Response<PaginationDto<List<ScrapDetailsResponse>>> response) {
            for(int i=0; i<response.body().getData().size(); i++){
                LocalDateTime startDateTime = LocalDateTime.now();
                LocalDateTime endDateTime = response.body().getData().get(i).getAuctionClosingDate();
                String days = String.valueOf(ChronoUnit.DAYS.between(startDateTime, endDateTime));
                String hours = String.valueOf(ChronoUnit.HOURS.between(startDateTime, endDateTime));
                String minutes = String.valueOf(ChronoUnit.MINUTES.between(startDateTime, endDateTime)%60);
                Long itemId = response.body().getData().get(i).getItemId();
                String itemName = response.body().getData().get(i).getItemName();
                int maximumPrice = response.body().getData().get(i).getMaximumPrice();

                String date = "";
                if(Integer.parseInt(hours) >= 24) {
                    hours = String.valueOf(Integer.parseInt(hours)%24);
                    date = days + "??? " + hours + "?????? " + minutes + "???";
                } else if(Integer.parseInt(hours) < 0 || Integer.parseInt(minutes) < 0) {
                    date = "";
                }
                else
                    date = hours + "?????? " + minutes + "???";

                if(response.body().getData().get(i).getFileNames().size()!=0) {
                    String fileNameMajor = response.body().getData().get(i).getFileNames().get(0);
                    data = new ScrapItem(itemId, fileNameMajor, itemName, myFormatter.format(maximumPrice), date);
                } else{
                    data = new ScrapItem(itemId, null, itemName, myFormatter.format(maximumPrice), date);
                }
                scrapItemsList.add(data);
                adapter.addItem(data);
                adapter.notifyDataSetChanged();
            }
            Log.d(TAG, ScrapConstants.EScrapCallback.rtSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<PaginationDto<List<ScrapDetailsResponse>>> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, ScrapConstants.EScrapCallback.rtFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e(ScrapConstants.EScrapCallback.rtConnectionFail.getText(), t.getMessage());
        }
    }
}
