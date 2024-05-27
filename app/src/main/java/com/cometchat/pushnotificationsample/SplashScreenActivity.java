package com.cometchat.pushnotificationsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.cometchat.pushnotificationsample.helper.ConstantFile;

public class SplashScreenActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initCometChatUIKit();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(callEventReceiver, new IntentFilter(ConstantFile.IntentStrings.COMETCHAT_CALL_EVENT));

    }

    private void initCometChatUIKit() {
        UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder().setRegion(AppConfig.AppDetails.REGION).setAppId(AppConfig.AppDetails.APP_ID).setAuthKey(AppConfig.AppDetails.AUTH_KEY).subscribePresenceForAllUsers().build();

        CometChatUIKit.init(this, uiKitSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
                if (CometChatUIKit.getLoggedInUser() != null) {
                    startActivity(new Intent(SplashScreenActivity.this, HomeScreenActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }

    private BroadcastReceiver callEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
}