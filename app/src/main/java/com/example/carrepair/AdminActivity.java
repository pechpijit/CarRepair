package com.example.carrepair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carrepair.adapter.AdapterItemList;
import com.example.carrepair.databinding.ActivityAdminBinding;
import com.example.carrepair.databinding.BottomSheetsCreateShopBinding;
import com.example.carrepair.helper.BaseActivity;
import com.example.carrepair.model.CarShopModel;
import com.example.carrepair.model.UserModel;
import com.example.carrepair.okhttp.ApiClient;
import com.example.carrepair.okhttp.ApiSetting;
import com.example.carrepair.okhttp.CallServiceListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AdminActivity extends BaseActivity {
    private ActivityAdminBinding binding;
    private int shopType = 1;
    private String TAG = AdminActivity.class.getName();
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mRecyclerView.setHasFixedSize(true);

        apiPlaceList(shopType);

        binding.btnType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mRecyclerView.setVisibility(View.GONE);
                binding.viewLoadContent.setVisibility(View.VISIBLE);
                apiPlaceList(1);
            }
        });

        binding.btnType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.mRecyclerView.setVisibility(View.GONE);
                binding.viewLoadContent.setVisibility(View.VISIBLE);
                apiPlaceList(2);
            }
        });

        binding.btnMenu.setOnClickListener(view -> showDialogLogout());
        binding.btnCreateShop.setOnClickListener(view -> setBottomSheetOpenTax());
    }

    private void showDialogLogout() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setMessage("กดยืนยัน เพื่อออกจากระบบ ?");
        builder.setPositiveButton("ยืนยัน", (dialog, id) -> LogoutApp());
        builder.setNegativeButton("ยกเลิก", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void apiPlaceList(int shopType) {
        this.shopType = shopType;
        ApiSetting api = new ApiSetting(this);
        ApiClient.GET post = new ApiClient.GET();
        post.setURL(api.getBASE_URL("shop-list.php?type=" + shopType));
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                dialogResultError("Login failed", data.contains("timeout") ? "Timeout connect. try again." : "Please try again.");
            }

            @Override
            public void ResultNull(String data) {
                dialogResultError("Load null", "Please try again.");
            }
        });
    }

    private void setView(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<CarShopModel>>() {
        }.getType();
        Collection<CarShopModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<CarShopModel> posts = new ArrayList<CarShopModel>(enums);

        AdapterItemList mAdapter = new AdapterItemList(this, posts);
        binding.mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener(new AdapterItemList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AdminActivity.this, DetailActivity.class);
                intent.putExtra("shopName", posts.get(position).getName());
                intent.putExtra("shopAddress", posts.get(position).getAddress());
                intent.putExtra("shopPhone", posts.get(position).getPhone());
                intent.putExtra("shopLatitude", posts.get(position).getLatitude());
                intent.putExtra("shopLongitude", posts.get(position).getLongitude());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        if (bottomSheetDialog != null) {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.hide();
            }
        }
        hideProgressDialog();
        binding.mRecyclerView.setVisibility(View.VISIBLE);
        binding.viewLoadContent.setVisibility(View.GONE);
    }

    private void setBottomSheetOpenTax() {
        BottomSheetsCreateShopBinding binding = BottomSheetsCreateShopBinding.inflate(getLayoutInflater());
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(binding.getRoot());
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());

        bottomSheetBehavior.setPeekHeight(1500, true);

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.hide();
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == 1) {
                    bottomSheetDialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        binding.btnCreateShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CarShopModel carShopModel = new CarShopModel();
                carShopModel.setName(binding.inputName.getText().toString());
                carShopModel.setPhone(binding.inputPhone.getText().toString());
                carShopModel.setAddress(binding.inputAddress.getText().toString());
                carShopModel.setLatitude(binding.inputLat.getText().toString());
                carShopModel.setLongitude(binding.inputLon.getText().toString());
                carShopModel.setType_id(shopType);
                apiCreateShop(carShopModel);
            }
        });

        bottomSheetDialog.show();
    }

    private void apiCreateShop(CarShopModel carShopModel) {
        showProgressDialog(VERIFY);
        ApiSetting api = new ApiSetting(this);

        RequestBody requestBody = new FormBody.Builder()
                .add("name", carShopModel.getName())
                .add("phone", carShopModel.getPhone())
                .add("address", carShopModel.getAddress())
                .add("latitude", carShopModel.getLatitude())
                .add("longitude", carShopModel.getLongitude())
                .add("type_id", String.valueOf(carShopModel.getType_id()))
                .build();

        ApiClient.POST post = new ApiClient.POST();
        post.setURL(api.getBASE_URL("shop-create.php"));
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                setView(data);
            }

            @Override
            public void ResultError(String data) {
                dialogResultError("ผิดพลาด", data.contains("timeout") ? "Timeout connect. try again." : "ไม่สามารถทำรายการได้ในขณะนี้ กรุณาลองใหม่อีกครั้ง");
            }

            @Override
            public void ResultNull(String data) {
                dialogResultError("ไม่พบข้อมูล", "กรุณาลองใหม่อีกครั้ง");
            }
        });
    }
}