package com.cometchat.pushnotificationsample;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatNotifications;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chatuikit.conversationswithmessages.CometChatConversationsWithMessages;

public class ConversationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        CometChatConversationsWithMessages conversationWithMessages = view.findViewById(R.id.conversationWithMessages);
        View menu = getLayoutInflater().inflate(R.layout.view_menu, null);
        ImageView logout = menu.findViewById(R.id.img_logout);
        logout.setOnClickListener(v -> {
            CometChatNotifications.unregisterPushToken(new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                }

                @Override
                public void onError(CometChatException e) {
                }
            });
            CometChat.logout(new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                @Override
                public void onError(CometChatException e) {
                }
            });
        });
        conversationWithMessages.setMenu(menu);
        return view;
    }
}