package com.cometchat.pushnotificationsample.helper;

import android.content.Intent;
import android.text.TextUtils;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.pushnotificationsample.R;

import org.json.JSONObject;

public class CometChatObjectHelper {
    public static void process(Intent intent, final CometChatObjectCallback listener) {
        try {
            String notificationPayload = intent.getStringExtra(ConstantFile.IntentStrings.NOTIFICATION_PAYLOAD);
            if (TextUtils.isEmpty(notificationPayload)) {
                listener.onNoMessage();
            } else {
                JSONObject jsonObject = new JSONObject(notificationPayload);
                String type = jsonObject.getString(ConstantFile.IntentStrings.RECEIVER_TYPE);
                String msgtype = jsonObject.getString(ConstantFile.IntentStrings.TYPE);
                if (!msgtype.equals(CometChatConstants.CATEGORY_CALL)) {
                    if (type.equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                        String uid = jsonObject.getString(ConstantFile.IntentStrings.SENDER);

                        CometChat.getUser(uid, new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                listener.onUserMessage(user);
                            }

                            @Override
                            public void onError(CometChatException e) {
                            }
                        });

                    } else {
                        String guid = jsonObject.getString(ConstantFile.IntentStrings.RECEIVER);

                        CometChat.getGroup(guid, new CometChat.CallbackListener<Group>() {
                            @Override
                            public void onSuccess(Group group) {
                                listener.onGroupMessage(group);
                            }

                            @Override
                            public void onError(CometChatException e) {
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}