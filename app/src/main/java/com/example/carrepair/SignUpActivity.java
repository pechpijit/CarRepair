package com.example.carrepair;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carrepair.databinding.ActivitySignUpBinding;
import com.example.carrepair.helper.BaseActivity;
import com.example.carrepair.model.UserModel;
import com.example.carrepair.okhttp.ApiClient;
import com.example.carrepair.okhttp.ApiSetting;
import com.example.carrepair.okhttp.CallServiceListener;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;
    String TAG = SignUpActivity.class.getName();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClickSignIn");
                binding.btnSignup.setEnabled(false);

                if (!validate()) {
                    binding.btnSignup.setEnabled(true);
                    return;
                }

                showProgressDialog(AUTH);
                apiRegister();
            }
        });
    }

    private void apiRegister() {
        ApiSetting api = new ApiSetting(this);

        RequestBody requestBody = new FormBody.Builder()
                .add("username", binding.inputUsername.getText().toString())
                .add("email", binding.inputEmail.getText().toString())
                .add("password", binding.inputPassword.getText().toString())
                .build();

        ApiClient.POST post = new ApiClient.POST();
        post.setURL(api.getBASE_URL("register.php"));
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
                binding.btnSignup.setEnabled(true);
            }

            @Override
            public void ResultNull(String data) {
                dialogResultError("ไม่พบข้อมูล", "");
                binding.btnSignup.setEnabled(true);
            }
        });
    }

    private void parseJsonRegisterForm(String json) {
        if (json.equals("SUCCESS")) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            dialogResultError("ผิดพลาด","ไม่สามารถทำรายการได้ในขณะนี้ กรุณาลองใหม่อีกครั้ง");

        }
    }

    public boolean validate() {
        Log.d(TAG, "validate");
        boolean valid = true;

        String username = binding.inputUsername.getText().toString().trim();
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (username.isEmpty() || username.length() < 4) {
            binding.inputEmail.setError("กรุณาชื่อผู้ใช้ มากกว่าหรือเท่ากับ 4 ตัว");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("กรุณากรอกอีเมล์");
            valid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            binding.inputPassword.setError("กรุณากรอกรหัสที่ มากกว่าหรือเท่ากับ 4 ตัว");
            valid = false;
        } else {
            binding.inputPassword.setError(null);
        }
        Log.d(TAG, "validate:" + valid);
        return valid;
    }
}