package com.example.carrepair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carrepair.adapter.AdapterItemList;
import com.example.carrepair.databinding.ActivityMainBinding;
import com.example.carrepair.helper.BaseActivity;
import com.example.carrepair.model.CarShopModel;
import com.example.carrepair.okhttp.ApiClient;
import com.example.carrepair.okhttp.ApiSetting;
import com.example.carrepair.okhttp.CallServiceListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    ArrayList<CarShopModel> carShopModels = new ArrayList<>();
    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mRecyclerView.setHasFixedSize(true);

        apiPlaceList(1);

        binding.btnType1.setOnClickListener(view -> {
            binding.mRecyclerView.setVisibility(View.GONE);
            binding.viewLoadContent.setVisibility(View.VISIBLE);
            apiPlaceList(1);
        });

        binding.btnType2.setOnClickListener(view -> {
            binding.mRecyclerView.setVisibility(View.GONE);
            binding.viewLoadContent.setVisibility(View.VISIBLE);
            apiPlaceList(2);
        });

        binding.btnMenu.setOnClickListener(view -> showDialogLogout());
    }

    private void showDialogLogout() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setMessage("กดยืนยัน เพื่อออกจากระบบ ?");
        builder.setPositiveButton("ยืนยัน", (dialog, id) -> LogoutApp());
        builder.setNegativeButton("ยกเลิก", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void apiPlaceList(int type_id) {
        ApiSetting api = new ApiSetting(this);
        ApiClient.GET post = new ApiClient.GET();
        post.setURL(api.getBASE_URL("shop-list.php?type=" + type_id));
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
        final ArrayList<CarShopModel> posts = new ArrayList<>(enums);

        AdapterItemList mAdapter = new AdapterItemList(this, posts);
        binding.mRecyclerView.setAdapter(mAdapter);
        mAdapter.SetOnItemClickListener((view, position) -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("shopId", posts.get(position).getId());
            intent.putExtra("shopName", posts.get(position).getName());
            intent.putExtra("shopAddress", posts.get(position).getAddress());
            intent.putExtra("shopPhone", posts.get(position).getPhone());
            intent.putExtra("shopLatitude", posts.get(position).getLatitude());
            intent.putExtra("shopLongitude", posts.get(position).getLongitude());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        });

        binding.mRecyclerView.setVisibility(View.VISIBLE);
        binding.viewLoadContent.setVisibility(View.GONE);
    }
}