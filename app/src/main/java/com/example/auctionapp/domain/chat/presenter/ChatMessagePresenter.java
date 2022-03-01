package com.example.auctionapp.domain.chat.presenter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.auctionapp.databinding.ActivityChatRoomBinding;
import com.example.auctionapp.domain.chat.adapter.ChattingViewAdapter;
import com.example.auctionapp.domain.chat.constant.ChatConstants;
import com.example.auctionapp.domain.chat.model.ChatModel;
import com.example.auctionapp.domain.chat.model.User;
import com.example.auctionapp.domain.chat.view.ChatMessageView;
import com.example.auctionapp.domain.item.dto.ItemDetailsResponse;
import com.example.auctionapp.domain.user.constant.Constants;
import com.example.auctionapp.domain.user.dto.UserInfoResponse;
import com.example.auctionapp.global.retrofit.MainRetrofitCallback;
import com.example.auctionapp.global.retrofit.MainRetrofitTool;
import com.example.auctionapp.global.retrofit.RetrofitTool;
import com.example.auctionapp.global.util.ErrorMessageParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;

import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ChatMessagePresenter implements ChatMessagePresenterInterface {
    //uid
    private String chatRoomUid; //채팅방 하나 id
    private String myuid;       //나의 id
    private String destUid;     //상대방 uid
    private User destUser;
    String profileUrlStr;
    private Long EndItemId;

    //firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Attributes
    private ChatMessageView chatMessageView;
    private ActivityChatRoomBinding mBinding;
    private Context context;

    // Constructor
    public ChatMessagePresenter(ChatMessageView chatMessageView, ActivityChatRoomBinding mBinding, Context getApplicationContext){
        this.chatMessageView = chatMessageView;
        this.mBinding = mBinding;
        this.context = getApplicationContext;
    }

    @Override
    public void init(String destuid, Long EndItemId) {
        database = FirebaseDatabase.getInstance(ChatConstants.EChatFirebase.firebaseUrl.getText());
        databaseReference = database.getReference();

        myuid = String.valueOf(Constants.userId);
        destUid = destuid;
//        EndItemId = intent.getLongExtra("itemId", 0);

        mBinding.chattingItemImage.setClipToOutline(true);

        //상품 정보 가져오기
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).getItem(EndItemId)
                .enqueue(MainRetrofitTool.getCallback(new getItemDetailsCallback()));

        if (mBinding.editText.getText().toString() == null) mBinding.sendbutton.setEnabled(false);
        else mBinding.sendbutton.setEnabled(true);

        checkChatRoom();
    }

    @Override
    public void sendMsg() {
        mBinding.sendbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(myuid, true);
                chatModel.users.put(destUid, true);
                chatModel.itemId.put(ChatConstants.EChatFirebase.itemId.getText(), EndItemId);

                //push() 데이터가 쌓이기 위해 채팅방 생성_미완
                if (chatRoomUid == null) {
                    mBinding.sendbutton.setEnabled(false);
                    databaseReference.child(ChatConstants.EChatFirebase.chatrooms.getText()).push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
//                    insertUserInfo(Long.valueOf(myuid));
//                    insertUserInfo(Long.valueOf(destUid));
//                    databaseReference.child("User").push().child("uid").setValue(myuid);
//                    databaseReference.child("User").push().child("uid").setValue(destUid);
                } else {
                    sendMsgToDataBase();
                }
            }
        });
    }

    @Override
    public void insertUserInfo(Long chatUserId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).userDetails(chatUserId)
                .enqueue(MainRetrofitTool.getCallback(new UserDetailsInfoCallback()));
    }

    @Override
    public void sendMsgToDataBase() {
        if (!mBinding.editText.getText().toString().equals("")) { // 이 안에서 한 번 더 나인지 상대방인지 체크해 주는게 필요할 듯
            // comment.uid 값이 myuid 여야 하는지, destuid여야 하는지 체크해야 함..
            LocalDateTime currentDate = LocalDateTime.now();
            ChatModel.Comment comment = new ChatModel.Comment();
            comment.uid = myuid;
            comment.message = mBinding.editText.getText().toString();
            comment.timestamp = String.valueOf(currentDate);
            databaseReference.child(ChatConstants.EChatFirebase.chatrooms.getText()).child(chatRoomUid).child(ChatConstants.EChatFirebase.comments.getText()).push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mBinding.editText.setText("");
                }
            });
        }
    }

    @Override
    public void checkChatRoom() {
//자신 key == true 일때 chatModel 가져온다.
        /* chatModel
        public Map<String,Boolean> users = new HashMap<>(); //채팅방 유저
        public Map<String, ChatModel.Comment> comments = new HashMap<>(); //채팅 메시지
        */
        databaseReference.child(ChatConstants.EChatFirebase.chatrooms.getText()).orderByChild(ChatConstants.EChatFirebase.users.getText()+ ChatConstants.EChatFirebase.slash.getText() + myuid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) //나, 상대방 id 가져온다.
                {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destUid)) {           //상대방 id 포함돼 있을때 채팅방 key 가져옴
                        chatRoomUid = dataSnapshot.getKey();
                        mBinding.sendbutton.setEnabled(true);

                        //동기화
                        mBinding.chattingRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        mBinding.chattingRecyclerView.setAdapter(new ChattingViewAdapter(chatMessageView, mBinding, context, chatRoomUid, myuid, destUid));

                        //메시지 보내기
                        sendMsgToDataBase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public class getItemDetailsCallback implements MainRetrofitCallback<ItemDetailsResponse> {
        @Override
        public void onSuccessResponse(Response<ItemDetailsResponse> response) {
            mBinding.chattingItemDetailName.setText(response.body().getItemName());
            if(response.body().getFileNames().size()!=0){
                String fileThumbNail = response.body().getFileNames().get(0);
                Glide.with(context).load(Constants.imageBaseUrl+fileThumbNail).into(mBinding.chattingItemImage);
            }
            mBinding.chattingItemDetailCategory.setText(response.body().getCategory().getName());
            mBinding.chattingItemDetailPrice.setText("500000");    //낙찰가 출력(임시)
            Log.d(TAG, ChatConstants.EChatCallback.rtSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<ItemDetailsResponse> response) throws IOException, JSONException {
            ErrorMessageParser errorMessageParser = new ErrorMessageParser(response.errorBody().string());
            chatMessageView.showToast(errorMessageParser.getParsedErrorMessage());
            Log.d(TAG, ChatConstants.EChatCallback.rtFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            mBinding.chattingItemDetailPrice.setText(ChatConstants.EChatCallback.rtConnectionFail.getText());
            Log.e(ChatConstants.EChatCallback.rtConnectionFail.getText(), t.getMessage());
        }
    }
    private class UserDetailsInfoCallback implements MainRetrofitCallback<UserInfoResponse> {
        @Override
        public void onSuccessResponse(Response<UserInfoResponse> response) {
            String userProfile = response.body().getPicture();
            Long chatUserId = response.body().getUserId();
            String userName = response.body().getUsername();
//            User userInfo = new User(userName, userProfile, chatUserId);
//            databaseReference.child(ChatConstants.EChatFirebase.User.getText()).push().setValue(userInfo);

            Log.d(TAG, ChatConstants.EChatCallback.rtSuccessResponse.getText() + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<UserInfoResponse> response) throws IOException, JSONException {
            ErrorMessageParser errorMessageParser = new ErrorMessageParser(response.errorBody().string());
            chatMessageView.showToast(errorMessageParser.getParsedErrorMessage());
            Log.d(TAG, ChatConstants.EChatCallback.rtFailResponse.getText());
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e(ChatConstants.EChatCallback.rtConnectionFail.getText(), t.getMessage());
        }
    }
}
