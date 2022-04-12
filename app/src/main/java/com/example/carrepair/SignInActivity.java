package com.example.carrepair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carrepair.databinding.ActivitySigninBinding;
import com.example.carrepair.helper.BaseActivity;
import com.example.carrepair.model.UserModel;
import com.example.carrepair.okhttp.ApiClient;
import com.example.carrepair.okhttp.ApiSetting;
import com.example.carrepair.okhttp.CallServiceListener;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SignInActivity extends BaseActivity {
    private ActivitySigninBinding binding;
    String TAG = SignInActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickSignIn");
                binding.btnSignin.setEnabled(false);

                if (!validate()) {
                    binding.btnSignin.setEnabled(true);
                    return;
                }

                showProgressDialog(AUTH);
                apiLogin();
            }
        });
    }

    private void apiLogin() {
        ApiSetting api = new ApiSetting(this);

        RequestBody requestBody = new FormBody.Builder()
                .add("email", binding.inputEmail.getText().toString())
                .add("password", binding.inputPassword.getText().toString())
                .build();

        ApiClient.POST post = new ApiClient.POST();
        post.setURL(api.getBASE_URL("login.php"));
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                parseJsonRegisterForm(data);
            }

            @Override
            public void ResultError(String data) {
                dialogResultError("ผิดพลาด", data.contains("timeout") ? "Timeout connect. try again." : "ไม่สามารถทำรายการได้ในขณะนี้ กรุณาลองใหม่อีกครั้ง");
                binding.btnSignin.setEnabled(true);
            }

            @Override
            public void ResultNull(String data) {
                dialogResultError("ไม่พบข้อมูล", "อีเมล์หรือรหัสผ่านไม่ถูกต้อง");
                binding.btnSignin.setEnabled(true);
            }
        });
    }

    private void parseJsonRegisterForm(String json) {
        Gson gson = new Gson();
        UserModel userModel = gson.fromJson(json, UserModel.class);
        Log.d(TAG, userModel.toString());

        ApiSetting setting = new ApiSetting(this);
        setting.putInt(ApiSetting.USER_ID, userModel.getId());
        setting.putInt(ApiSetting.USER_TYPE_ID, userModel.getUserTypeId());
        setting.putString(ApiSetting.USER_NAME, userModel.getUsername());
        setting.putString(ApiSetting.EMAIL, userModel.getEmail());
        setting.putBoolean(ApiSetting.LOGIN_STATUS, true);

        hideProgressDialog();

        if (userModel.getUserTypeId() == 1) {
            startActivity(new Intent(this, AdminActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    public boolean validate() {
        Log.d(TAG, "validate");
        boolean valid = true;

        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("กรุณากรอกอีเมล์");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            binding.inputPassword.setError("กรุณากรอกรหัสที่มากกว่าหรือเท่ากับ 4 ตัว");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }
        Log.d(TAG, "validate:" + valid);
        return valid;
    }
}