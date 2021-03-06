package com.me.hurryuphup.domain.upload.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.me.hurryuphup.MainActivity;
import com.me.hurryuphup.R;
import com.me.hurryuphup.databinding.ActivityUploadPageBinding;
import com.me.hurryuphup.domain.upload.adapter.MultiImageAdapter;
import com.me.hurryuphup.domain.upload.constant.UploadConstants;
import com.me.hurryuphup.domain.upload.presenter.UploadPresenter;
import com.me.hurryuphup.domain.user.constant.Constants;
import com.me.hurryuphup.global.util.CustomTextWatcher;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadPage extends AppCompatActivity implements UploadView{
    private ActivityUploadPageBinding binding;
    UploadPresenter presenter;

    // DatePickerDialog
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener datePickerBuyDate = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            LocalDateTime endDateTime = LocalDateTime.of(year, month+1, dayOfMonth, 00, 00,00,0000);
            String formatDate = endDateTime.format(DateTimeFormatter.ofPattern(UploadConstants.EDate.datePattern.getText()));
            binding.editAuctionBuyDate.setText(formatDate.toString());
        }
    };
    DatePickerDialog.OnDateSetListener datePickerEndDate = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            LocalDateTime endDateTime = LocalDateTime.of(year, month+1, dayOfMonth, 00, 00,00,0000);
            String formatDate = endDateTime.format(DateTimeFormatter.ofPattern(UploadConstants.EDate.datePattern.getText()));
            binding.editAuctionFinalDate.setText(formatDate.toString());
        }
    };
    TimePickerDialog.OnTimeSetListener timePickerEndTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hourStr = String.valueOf(hourOfDay);
            String minStr = String.valueOf(minute);
            if(hourOfDay < 10) hourStr = "0" + hourOfDay;
            if(minute < 10) minStr = "0" + minute;
            binding.editAuctionFinalTime.setText(hourStr + ":" + minStr + ":00");
        }
    };

    private static final String TAG = "UploadPage";
    ArrayList<Uri> uriList;   // ???????????? uri??? ?????? ArrayList ??????
    ArrayList<File> fileList;

    MultiImageAdapter adapter;  // ????????????????????? ???????????? ?????????
    int itemStatePoint; //item rating
    ArrayList<MultipartBody.Part> files = new ArrayList<>(); // ?????? file?????? ????????? ArrayList

    static final String[] LIST_MENU = {"????????? ??????", "????????????", "??????/????????????", "?????????", "??????/????????????"
            ,"????????????", "?????????/??????", "????????????", "????????????", "????????????/??????", "??????/??????", "??????/??????",
            "??????????????????", "??????/??????/??????", "??????"} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter = new UploadPresenter(this, binding, getApplicationContext());

        binding.goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(UploadPage.this, MainActivity.class);
                tt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tt);
            }
        });

        //?????? ??????
        binding.editItemStartPrice.addTextChangedListener(new CustomTextWatcher(binding.editItemStartPrice));
        // ?????? ??????
        Calendar calender = Calendar.getInstance();
        binding.editAuctionBuyDate.setText(UploadConstants.ECategory.itemBuyDate.getText());
        binding.editAuctionBuyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UploadPage.this, datePickerBuyDate,
                        calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DATE));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
            }
        });

        // ?????? ?????? ??????
        binding.editAuctionFinalDate.setText(UploadConstants.ECategory.itemEndDate.getText());
        binding.editAuctionFinalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UploadPage.this, datePickerEndDate,
                        calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DATE));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });
        // ?????? ?????? ??????
        binding.editAuctionFinalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(UploadPage.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                        timePickerEndTime, 00, 00, true);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });

        // ?????? ??????
        binding.itemStateRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                itemStatePoint = Math.round(rating);
            }
        });

        // ????????? ?????????
        binding.chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ???????????? ??????
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //?????? ???????????? ????????? ??? ?????????
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        // category
        LinearLayout selectCategory = (LinearLayout) findViewById(R.id.selectCategoryLayout);
        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(UploadPage.this);
                dlg.setTitle("???????????? ??????"); //??????
                dlg.setIcon(R.drawable.interest); // ????????? ??????

                dlg.setItems(LIST_MENU, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.selectItemCategory.setText(LIST_MENU[which]);
                    }
                });
                dlg.show();
            }
        });

        // ?????? ??????
        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(Constants.userId == null) {
                    showToast(UploadConstants.EUploadToast.afterLogin.getText());
                } else {
                    String itemName = binding.editItemName.getText().toString();
                    String initPriceStr = binding.editItemStartPrice.getText().toString().replace(",","");
                    if (initPriceStr.equals("")) {
                        showToast(UploadConstants.EUploadToast.eEditInitPrice.getText());
                        return;
                    }
                    if(binding.editItemName.getText().toString().equals("")) {
                        showToast(UploadConstants.EUploadToast.eEditItemName.getText());
                        return;
                    }
                    if(binding.selectItemCategory.getText().toString().equals("")) {
                        showToast(UploadConstants.EUploadToast.eSelectCategory.getText());
                        return;
                    }
                    if(binding.editAuctionBuyDate.getText().toString().equals(UploadConstants.ECategory.itemBuyDate.getText())) {
                        showToast(UploadConstants.EUploadToast.eSelectBuyDate.getText());
                        return;
                    }
                    if(binding.editAuctionFinalDate.getText().toString().equals(UploadConstants.ECategory.itemEndDate.getText())) {
                        showToast(UploadConstants.EUploadToast.eSelectFinalDate.getText());
                        return;
                    }
                    if(binding.editAuctionFinalTime.getText().toString().equals(UploadConstants.ECategory.itemEndTime.getText())) {
                        showToast(UploadConstants.EUploadToast.eSelectFinalTime.getText());
                        return;
                    }
                    if(binding.editItemContent.getText().toString().equals("")) {
                        showToast(UploadConstants.EUploadToast.eEditItemContent.getText());
                        return;
                    }
                    int initPrice = Integer.parseInt(initPriceStr);
                    String buyDate = binding.editAuctionBuyDate.getText().toString() + UploadConstants.EDate.dateZero.getText();
                    String auctionClosingDate = binding.editAuctionFinalDate.getText().toString() + " " + binding.editAuctionFinalTime.getText().toString();
                    String description = binding.editItemContent.getText().toString();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(UploadConstants.EDate.dateTimePattern.getText());
                    LocalDateTime buyDateTime = LocalDateTime.parse(buyDate, formatter);
                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(UploadConstants.EDate.dateTimePattern.getText());
                    LocalDateTime auctionClosingDateTime = LocalDateTime.parse(auctionClosingDate, formatter2);

                    RequestBody userIdR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), String.valueOf(Constants.userId));
                    RequestBody itemNameR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), itemName);
                    RequestBody categoryR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), presenter.choiceCategory(binding.selectItemCategory.getText().toString()));
                    RequestBody initPriceR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), String.valueOf(initPrice));
                    RequestBody buyDateR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), String.valueOf(buyDateTime));
                    RequestBody itemStatePointR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), String.valueOf(itemStatePoint));
                    RequestBody auctionClosingDateR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), String.valueOf(auctionClosingDateTime));
                    RequestBody descriptionR = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypePlain.getText()), description);

                    makeMultiPart();
                    System.out.println(UploadConstants.EUploadLog.fileCheck.getText() + files.toString());
                    presenter.upload(files, userIdR,
                            itemNameR, categoryR, initPriceR, buyDateR, itemStatePointR,
                            auctionClosingDateR, descriptionR);
                    //go home
                    Intent tt = new Intent(getApplicationContext(), MainActivity.class);
                    tt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(tt);
                }
            }

        });

    }

    // select image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //uriList.clear();    // ????????? ?????? ?????? ??? ?????????
        uriList = new ArrayList<>();
        fileList = new ArrayList<>();
        if(requestCode == 2222){
            if(data == null){   // ?????? ???????????? ???????????? ?????? ??????
                showToast(UploadConstants.EUploadToast.unselectImage.getText());
            }
            else{   // ???????????? ???????????? ????????? ??????
                if(data.getClipData() == null){     // ???????????? ????????? ????????? ??????
                    Log.e(UploadConstants.EUploadLog.singleChoice.getText(), String.valueOf(data.getData()));
                    binding.selectedImageCount.setText("1/10");
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);
                    String imagePath = getRealpath(imageUri);
                    File destFile = new File(imagePath);
                    fileList.add(destFile);
                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    binding.selectedImageRecyclerView.setAdapter(adapter);
                    binding.selectedImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
                }
                else{      // ???????????? ????????? ????????? ??????
                    ClipData clipData = data.getClipData();
                    binding.selectedImageCount.setText(String.valueOf(clipData.getItemCount()) + "/10");

                    if(clipData.getItemCount() > 10){   // ????????? ???????????? 11??? ????????? ??????
                        showToast(UploadConstants.EUploadToast.imageSelectOver.getText());
                    }
                    else{   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????
                        Log.e(TAG, UploadConstants.EUploadLog.multiChoice.getText());

                        for (int i = 0; i < clipData.getItemCount(); i++){
                            Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.

                            try {
                                uriList.add(imageUri);  //uri??? list??? ?????????.
                                String imagePath = getRealpath(imageUri);
                                File destFile = new File(imagePath);
                                fileList.add(destFile);
                            } catch (Exception e) {
                                Log.e(TAG, UploadConstants.EUploadLog.fileSelectError.getText(), e);
                            }
                        }
                        adapter = new MultiImageAdapter(uriList, getApplicationContext());
                        binding.selectedImageRecyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                        binding.selectedImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // ?????????????????? ?????? ????????? ??????
                    }
                }
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void makeMultiPart() {
        for (int i = 0; i < fileList.size(); ++i) {
            // Uri ????????? ??????????????? ????????? RequestBody ?????? ??????
            RequestBody fileBody = RequestBody.create(MediaType.parse(UploadConstants.EMultiPart.mediaTypeImage.getText()), fileList.get(0));
            // ?????? ?????? ??????
            LocalDateTime localDateTime = LocalDateTime.now();
            String fileName = "photo" + localDateTime + ".jpg";
            // RequestBody??? Multipart.Part ?????? ??????
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(UploadConstants.EMultiPart.files.getText(), fileName, fileBody);
            // ??????
            files.add(filePart);
        }

    }

    @Override
    public String getRealpath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = managedQuery(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }
}


