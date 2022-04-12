package com.example.carrepair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carrepair.adapter.AdapterCommentList;
import com.example.carrepair.adapter.AdapterItemList;
import com.example.carrepair.databinding.ActivityDetailBinding;
import com.example.carrepair.helper.BaseActivity;
import com.example.carrepair.model.CarShopModel;
import com.example.carrepair.model.CommentModel;
import com.example.carrepair.okhttp.ApiClient;
import com.example.carrepair.okhttp.ApiSetting;
import com.example.carrepair.okhttp.CallServiceListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    String TAG = DetailActivity.class.getName();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mRecyclerView.setHasFixedSize(true);

        ApiSetting setting = new ApiSetting(this);
        if (!setting.getBoolean(ApiSetting.LOGIN_STATUS)) {
            binding.contentComment.setLayoutParams(new LinearLayoutCompat.LayoutParams(0,0));
        }

        Bundle i = getIntent().getExtras();
        int id = i.getInt("shopId");
        String name = i.getString("shopName");
        String address = i.getString("shopAddress");
        String phone = i.getString("shopPhone");
        String latitude = i.getString("shopLatitude");
        String longitude = i.getString("shopLongitude");

        binding.txtName.setText(name);
        binding.txtAddress.setText(address);
        binding.txtPhone.setText(phone);

        binding.btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("shopName", name);
                intent.putExtra("shopLatitude", latitude);
                intent.putExtra("shopLongitude", longitude);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiCreateComment(id, setting.getInt(ApiSetting.USER_ID));
            }
        });

        apiCommentList(id);
    }


    private void apiCommentList(int shop_id) {
        ApiSetting api = new ApiSetting(this);
        ApiClient.GET post = new ApiClient.GET();
        post.setURL(api.getBASE_URL("comment.php?shop_id="+shop_id));
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                setViewComment(data);
            }

            @Override
            public void ResultError(String data) {
                dialogResultError("ผิดพลาด", data.contains("timeout") ? "Timeout connect. try again." : "ไม่สามารถทำรายการได้ในขณะนี้ กรุณาลองใหม่อีกครั้ง");
            }

            @Override
            public void ResultNull(String data) {
                binding.mRecyclerView.setVisibility(View.VISIBLE);
                binding.viewLoadContent.setVisibility(View.GONE);
            }
        });
    }

    private void setViewComment(String json) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<CommentModel>>() {
        }.getType();
        Collection<CommentModel> enums = gson.fromJson(json, collectionType);
        final ArrayList<CommentModel> posts = new ArrayList<>(enums);

        AdapterCommentList mAdapter = new AdapterCommentList(this, posts);
        binding.mRecyclerView.setAdapter(mAdapter);

        binding.inputComment.setText("");

        hideProgressDialog();
        binding.mRecyclerView.setVisibility(View.VISIBLE);
        binding.viewLoadContent.setVisibility(View.GONE);
    }


    private void apiCreateComment(int shop_id, int user_id) {
        showProgressDialog(VERIFY);
        ApiSetting api = new ApiSetting(this);

        RequestBody requestBody = new FormBody.Builder()
                .add("comment", binding.inputComment.getText().toString())
                .add("shop_id", String.valueOf(shop_id))
                .add("user_id", String.valueOf(user_id))
                .build();

        ApiClient.POST post = new ApiClient.POST();
        post.setURL(api.getBASE_URL("shop-comment.php"));
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                setViewComment(data);
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