package com.cometchat.pushnotificationsample;

import android.content.Intent;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;

import android.widget.Toast;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.cometchat.pushnotificationsample.helper.ConstantFile;

public class CallConnection extends Connection {

    CallConnectionService service;
    Call call;

    public CallConnection(CallConnectionService service, Call call) {
        this.service = service;
        this.call = call;
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        destroyConnection();
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL, ConstantFile.IntentStrings.MISSED));
        if (CometChat.getActiveCall() != null)
            onDisconnect(CometChat.getActiveCall());
    }

    void onDisconnect(Call call) {
        CometChat.rejectCall(call.getSessionId(), CometChatConstants.CALL_STATUS_CANCELLED, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(service, R.string.cometchat_disconnect_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void destroyConnection() {
        setDisconnected(new DisconnectCause(DisconnectCause.REMOTE, ConstantFile.IntentStrings.REJECTED));
        super.destroy();
    }

    @Override
    public void onAnswer(int videoState) {

        if (call.getSessionId() != null) {

            if (!CometChat.isInitialized()) {
                initializeCometChat();
            }
            CometChat.acceptCall(call.getSessionId(), new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    Intent intent = new Intent(service, CallScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConstantFile.IntentStrings.SESSION_ID, call.getSessionId());
                    intent.putExtra(ConstantFile.IntentStrings.RECEIVER_TYPE, call.getReceiverType());
                    intent.putExtra(ConstantFile.IntentStrings.CALL_ACTION, call.getAction());
                    intent.putExtra(ConstantFile.IntentStrings.CALL_TYPE, call.getType());
                    service.startActivity(intent);
                    destroyConnection();
                }

                @Override
                public void onError(CometChatException e) {
                    destroyConnection();
                    Toast.makeText(service, R.string.error + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onShowIncomingCallUi() {
    }

    @Override
    public void onAnswer() {
        if (call.getSessionId() != null) {
            if (!CometChat.isInitialized()) {
                initializeCometChat();
            }
            CometChat.acceptCall(call.getSessionId(), new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    service.sendBroadcast(getCallIntent());
                    Intent intent = new Intent(service, CallScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConstantFile.IntentStrings.SESSION_ID, call.getSessionId());
                    intent.putExtra(ConstantFile.IntentStrings.RECEIVER_TYPE, call.getReceiverType());
                    intent.putExtra(ConstantFile.IntentStrings.CALL_ACTION, call.getAction());
                    intent.putExtra(ConstantFile.IntentStrings.CALL_TYPE, call.getType());
                    service.startActivity(intent);
                    destroyConnection();
                }

                @Override
                public void onError(CometChatException e) {
                    destroyConnection();
                    Toast.makeText(service, R.string.error + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onUnhold() {
    }

    @Override
    public void onReject() {
        if (call.getSessionId() != null) {
            if (!CometChat.isInitialized()) {
                initializeCometChat();
            }
            CometChat.rejectCall(call.getSessionId(), CometChatConstants.CALL_STATUS_REJECTED, new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                    destroyConnection();
                    setDisconnected(new DisconnectCause(DisconnectCause.REJECTED, ConstantFile.IntentStrings.REJECTED));
                }

                @Override
                public void onError(CometChatException e) {
                    destroyConnection();
                    Toast.makeText(service, R.string.error + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private Intent getCallIntent() {
        Intent callIntent = new Intent(ConstantFile.IntentStrings.COMETCHAT_CALL_EVENT);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.putExtra(ConstantFile.IntentStrings.SESSION_ID, call.getSessionId());
        return callIntent;
    }

    private void initializeCometChat() {

        UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder()
                .setRegion(AppConfig.AppDetails.REGION)
                .setAppId(AppConfig.AppDetails.APP_ID)
                .setAuthKey(AppConfig.AppDetails.AUTH_KEY)
                .subscribePresenceForAllUsers().build();

        CometChatUIKit.init(service, uiKitSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successString) {
            }

            @Override
            public void onError(CometChatException e) {
            }
        });
    }
}