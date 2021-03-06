package com.me.hurryuphup.domain.item.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.me.hurryuphup.R;
import com.me.hurryuphup.domain.item.model.SellHistoryEndData;
import com.me.hurryuphup.domain.user.constant.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;

import lombok.Getter;

@Getter
public class SellHistoryEndAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<SellHistoryEndData> listData = new ArrayList<>();
    Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_sell_history_end_recyclerview, parent, false);
        return new SellHistoryEndViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SellHistoryEndViewHolder)holder).onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(SellHistoryEndData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onChatButtonClick(View v, int position);
    }
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {

        this.mListener = listener ;
    }

    public class SellHistoryEndViewHolder extends  RecyclerView.ViewHolder {

        ImageView sell_history_end_img;
        TextView sell_history_end_edt_name;
        TextView sell_history_end_edt_myPrice;
        TextView sell_history_end_edt_seller;
        Button goChatButton;
        DecimalFormat myFormatter = new DecimalFormat("###,###");

        public SellHistoryEndViewHolder(@NonNull View itemView) {
            super(itemView);

            sell_history_end_img = itemView.findViewById(R.id.sell_history_end_img);
            sell_history_end_edt_name = itemView.findViewById(R.id.sell_history_end_edt_name);
            sell_history_end_edt_myPrice = itemView.findViewById(R.id.sell_history_end_edt_myPrice);
            sell_history_end_edt_seller = itemView.findViewById(R.id.sell_history_end_edt_seller);
            goChatButton = itemView.findViewById(R.id.btn_chat);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            goChatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onChatButtonClick(v, pos);
                        }
                    }
                }
            });

        }

        public void onBind(SellHistoryEndData data){
            if(data.getImageURL() == null) {
                Glide.with(context).load(context.getString(R.string.hup_icon_url)).override(sell_history_end_img.getWidth()
                        , sell_history_end_img.getHeight()).into(sell_history_end_img);
            } else {
                Glide.with(context).load(Constants.imageBaseUrl + data.getImageURL()).override(sell_history_end_img.getWidth()
                        , sell_history_end_img.getHeight()).into(sell_history_end_img);
            }
            sell_history_end_img.setClipToOutline(true);  //item 테두리
            sell_history_end_edt_name.setText(data.getItemName());
            sell_history_end_edt_myPrice.setText(myFormatter.format(data.getItemPrice()));
            sell_history_end_edt_seller.setText(data.getBidderName());
        }


    }
}