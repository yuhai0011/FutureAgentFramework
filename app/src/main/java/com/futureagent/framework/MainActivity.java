package com.futureagent.framework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.futureagent.framework.constant.URLConfig;
import com.futureagent.lib.config.URLConstant;
import com.futureagent.lib.entity.HttpResponseEntity;
import com.futureagent.lib.network.HttpManager;
import com.futureagent.lib.network.handler.GsonHttpResonsedHandler;
import com.futureagent.lib.utils.LogUtils;
import com.futureagent.lib.view.recyclerview.SuperRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.recycler_view)
    SuperRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list);
        ButterKnife.bind(this);

        URLConstant.setDevEnv(new URLConfig());

        TextView textView = findViewById(R.id.btn_post);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToServer();
            }
        });
    }

    private void uploadToServer() {
        JSONObject paramMap = new JSONObject();
        try {
            paramMap.put("pkg", "pkg");
            paramMap.put("vc", 123);
            paramMap.put("vn","1.1.1");
            paramMap.put("filename", "filename123");
            paramMap.put("des", "test desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpManager.getInstance(this).httpPost(this, "add", paramMap, new GsonHttpResonsedHandler<Object>(this) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, String rawJsonResponse, HttpResponseEntity<Object> response) {
                super.onSuccess(statusCode, rawJsonResponse, response);
                if (statusCode == 200) {
                    LogUtils.e(TAG, "uploadToServer onSuccess:" + rawJsonResponse);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, String rawJsonData, HttpResponseEntity<Object> response) {
                super.onFailure(statusCode, throwable, rawJsonData, response);
                LogUtils.e(TAG, "uploadToServer onFailure:" + rawJsonData);
            }
        });
    }
}
