package com.cometchat.pushnotificationsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtUID;
    TextInputLayout inputLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initCometChatUIKit();
        initViews();
    }

    private void initViews() {
        edtUID = findViewById(R.id.etUID);
        inputLayout = findViewById(R.id.inputUID);
        progressBar = findViewById(R.id.loginProgress);

        inputLayout.setEndIconOnClickListener(v -> {
            if (Objects.requireNonNull(edtUID.getText()).toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.cometchat_fill_username, Toast.LENGTH_LONG).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                inputLayout.setEndIconVisible(false);
                login(edtUID.getText().toString());
            }
        });

        edtUID.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (edtUID.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.cometchat_fill_username, Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    inputLayout.setEndIconVisible(false);
                    login(edtUID.getText().toString());
                }
            }
            return true;
        });
    }

    private void login(String uid) {
        CometChatUIKit.login(uid, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                CometChatNotification.getInstance(LoginActivity.this).registerCometChatNotification(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onError(CometChatException e) {
                    }
                });
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }

    private void initCometChatUIKit() {
        UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder().setRegion(AppConfig.AppDetails.REGION).setAppId(AppConfig.AppDetails.APP_ID).setAuthKey(AppConfig.AppDetails.AUTH_KEY).subscribePresenceForAllUsers().build();

        CometChatUIKit.init(this, uiKitSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }
}