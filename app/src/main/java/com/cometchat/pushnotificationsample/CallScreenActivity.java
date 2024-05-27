package com.cometchat.pushnotificationsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chatuikit.calls.ongoingcall.CometChatOngoingCall;
import com.cometchat.pushnotificationsample.helper.ConstantFile;

public class CallScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        CometChatOngoingCall ongoingCall = findViewById(R.id.ongoing_call);

        Intent intent = getIntent();
        String sessionID = intent.getStringExtra(ConstantFile.IntentStrings.SESSION_ID);
        String receiverType = intent.getStringExtra(ConstantFile.IntentStrings.RECEIVER_TYPE);
        String action = intent.getStringExtra(ConstantFile.IntentStrings.CALL_ACTION);
        String type = intent.getStringExtra(ConstantFile.IntentStrings.CALL_TYPE);

        if (sessionID != null && receiverType != null && action != null) {

            if (action.equals(CometChatConstants.CALL_STATUS_ONGOING)) {
                ongoingCall.setSessionId(sessionID);
                ongoingCall.setReceiverType(receiverType);
                ongoingCall.setCallType(type);
                ongoingCall.startCall();
            }
        }
    }
}