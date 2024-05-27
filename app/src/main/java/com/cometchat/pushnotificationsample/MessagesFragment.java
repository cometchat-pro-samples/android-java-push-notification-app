package com.cometchat.pushnotificationsample;

import android.os.Bundle;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.chatuikit.messages.CometChatMessages;
import com.cometchat.chatuikit.shared.cometchatuikit.CometChatUIKit;
import com.cometchat.chatuikit.shared.cometchatuikit.UIKitSettings;
import com.cometchat.pushnotificationsample.helper.ConstantFile;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagesFragment extends Fragment {
    private CometChatMessages messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (!CometChat.isInitialized()) {
            UIKitSettings uiKitSettings = new UIKitSettings.UIKitSettingsBuilder().setRegion(AppConfig.AppDetails.REGION).setAppId(AppConfig.AppDetails.APP_ID).setAuthKey(AppConfig.AppDetails.AUTH_KEY).subscribePresenceForAllUsers().build();
            CometChatUIKit.init(getContext(), uiKitSettings, new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String successString) {
                }

                @Override
                public void onError(CometChatException e) {
                }
            });
        }
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        messages = view.findViewById(R.id.message_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String payload = this.getArguments().getString(ConstantFile.IntentStrings.NOTIFICATION_PAYLOAD);
        if (!TextUtils.isEmpty(payload)) {
            User user = User.fromJson(payload);
            messages.setUser(user);
            try {
                JSONObject payloadObject = new JSONObject(payload);
                if (payloadObject.has(CometChatConstants.GroupKeys.KEY_GROUP_GUID)) {
                    Group group = Group.fromJson(payloadObject.toString());
                    messages.setGroup(group);
                } else {
                    messages.setUser(User.fromJson(payloadObject.toString()));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}