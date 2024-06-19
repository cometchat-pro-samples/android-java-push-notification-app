package com.cometchat.pushnotificationsample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatNotifications;
import com.cometchat.chat.enums.PushPlatforms;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKitHelper;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.cometchat.chatuikit.shared.resources.theme.Palette;
import com.cometchat.pushnotificationsample.helper.CometChatObjectCallback;
import com.cometchat.pushnotificationsample.helper.CometChatObjectHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeScreenActivity extends AppCompatActivity {

    private final String TAG = HomeScreenActivity.class.getSimpleName();
    private RelativeLayout conversationParentView;

    private final int PERMISSION_REQUEST_CODE = 99;

    CometChatNotification cometChatNotification;

    private final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.MANAGE_OWN_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.POST_NOTIFICATIONS};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        conversationParentView = findViewById(R.id.conversation_container);
        cometChatNotification = CometChatNotification.getInstance(this);
        setTheme();
        handleIntent(getIntent());
        loadFragment(new ConversationFragment());
        requestPermissions();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String pushToken = task.getResult();
                    CometChatNotifications.registerPushToken(pushToken, PushPlatforms.FCM_ANDROID, AppConfig.AppDetails.FCM_PROVIDER_ID, new CometChat.CallbackListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.v(TAG, "onSuccess:  CometChat Notification Registered : " + s);
                        }

                        @Override
                        public void onError(CometChatException e) {
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestPermissions() {
        //Required Permission
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);

        //For VOIP
        if (!cometChatNotification.checkAccountConnection(this)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.cometchat_VoIP_permission);
            alertDialog.setMessage(R.string.cometchat_VoIP_message);
            alertDialog.setPositiveButton(R.string.cometchat_VoIP_openSettings, (dialog, which) -> launchVoIPSetting(HomeScreenActivity.this));
            alertDialog.setNegativeButton(R.string.cometchat_VoIP_cancel, (dialog, which) -> dialog.dismiss());
            alertDialog.create().show();
        }
    }

    public void launchVoIPSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
        ComponentName telecomComponent = new ComponentName(getString(R.string.cometchat_android_telecom_package), getString(R.string.cometchat_android_telecom_package_class));
        intent.setComponent(telecomComponent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void handleIntent(Intent intent) {

        if (!CometChat.isInitialized()) {

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
        CometChatObjectHelper.process(intent, new CometChatObjectCallback() {
            @Override
            public void onUserMessage(User user) {
                CometChatUIKitHelper.onOpenChat(user, null);
            }

            @Override
            public void onGroupMessage(Group group) {
                CometChatUIKitHelper.onOpenChat(null, group);
            }

            @Override
            public void onCallMessage() {
            }

            @Override
            public void onNoMessage() {
                loadFragment(new ConversationFragment());
            }
        });
    }

    private void setTheme() {
        Palette palette = Palette.getInstance(this);
        palette.primary(getResources().getColor(R.color.colorPrimary, getTheme()));
        palette.secondary(getResources().getColor(R.color.colorSecondary, getTheme()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.conversation_container, fragment).commit();
    }
}