package com.example.carrepair.okhttp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient extends ApiSetting{
    public ApiClient(Context context) {
        super(context);
    }

    public static class POST extends AsyncTask<Void, Void, String> {
        private RequestBody requestBody;
        private CallServiceListener callServiceListener = null;
        private String URL = null;
        private String TOKEN = null;
        private String TAG = "POST-API ";

        public void setListenerCallService(CallServiceListener callService) {
            this.callServiceListener = callService;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public void setTOKEN(String TOKEN) {
            this.TOKEN = TOKEN;
        }

        public void setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("content-type", "multipart/form-data")
                    .cacheControl(new CacheControl.Builder().noCache().build())
                    .url(URL)
                    .post(requestBody)
                    .build();

            Log.d(TAG, request.toString());

            try {
                Response response = client.newCall(request).execute();
                if (response.code() == 401){
                    return "invalid";
                } else if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Not Success - code : " + response.code()+" , message : "+response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d("ApiClientPOST : ", "onPostExecute:" + string);
            if (string.contains("Error") || string.contains("Not")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultError(string);
                }
            } else if (string.equals("[]") || string.equals("{}")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultNull(string);
                }
            } else {
                if (callServiceListener != null) {
                    callServiceListener.ResultData(string);
                }
            }
        }
    }

    public static class GET extends AsyncTask<Void, Void, String> {
        private CallServiceListener callServiceListener = null;
        private String URL = null;
        private String TAG = "GET-API ";

        public void setListenerCallService(CallServiceListener callService) {
            this.callServiceListener = callService;
        }

        public void setURL(String URL) {

            this.URL = URL;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .build();

            Request request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder().noCache().build())
                    .url(URL)
                    .get()
                    .build();

            Log.d(TAG, request.toString());

            try {
                Response response = client.newCall(request).execute();
//                if (response.body().contentLength() == 0) return response.body().string();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Not Success - code : " + response.code();

                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d("ApiClientGET : ", "onGetExecute:" + string);
            if (string.contains("Error") || string.contains("Not")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultError(string);
                }
            } else if (string.equals("[]") || string.equals("{}")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultNull(string);
                }
            } else {
                if (callServiceListener != null) {
                    callServiceListener.ResultData(string);
                }
            }
        }
    }
}
