package com.cometchat.pro.uikit.ui_components.calls.callconnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_components.users.user_details.CometChatUserDetailScreenActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

import java.util.Arrays;
import java.util.List;

public class CallBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String ourCode = "*007#";
        String dialNumber = intent.getData().toString();

        if (dialNumber.contains(ourCode)) {

            setResultData(null);

            String phoneNumber = dialNumber.replace(ourCode,"");
            UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder()
                    .setTags(Arrays.asList(phoneNumber)).setLimit(10).build();
            usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    if (!users.isEmpty()) {
                        User user = users.get(0);
                        Intent userDetail =
                                new Intent(context,CometChatUserDetailScreenActivity.class);
                        userDetail.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
                        userDetail.putExtra(UIKitConstants.IntentStrings.UID,user.getUid());
                        userDetail.putExtra(UIKitConstants.IntentStrings.NAME,user.getName());
                        context.startActivity(userDetail);
                    }
                }

                @Override
                public void onError(CometChatException e) {
                    Toast.makeText(context,"Unable to find user",Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, CometChatUI.class));
                }
            });
        }
    }
}