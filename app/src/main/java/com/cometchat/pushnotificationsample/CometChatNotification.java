package com.cometchat.pushnotificationsample;

import static android.content.Context.TELECOM_SERVICE;

import android.Manifest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.cometchat.chat.constants.CometChatConstants;
import com.cometchat.chat.core.Call;
import com.cometchat.chat.core.CometChat;
import com.cometchat.chat.core.CometChatNotifications;
import com.cometchat.chat.enums.PushPlatforms;
import com.cometchat.chat.exceptions.CometChatException;
import com.cometchat.chat.models.AppEntity;
import com.cometchat.chat.models.Group;
import com.cometchat.chat.models.User;
import com.cometchat.pushnotificationsample.helper.ConstantFile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class CometChatNotification {
    private static final String TAG = CometChatNotification.class.getSimpleName();
    private static Context context;
    private static CometChatNotification cometChatNotification;
    private static NotificationManager notificationManager;
    private static TelecomManager telecomManager;
    private static PhoneAccountHandle phoneAccountHandle;

    private CometChatNotification() {
    }

    public static CometChatNotification getInstance(Context c) {
        if (cometChatNotification == null) {
            cometChatNotification = new CometChatNotification();
            context = c;
            notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

            //For VoIP
            telecomManager = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
            ComponentName componentName = new ComponentName(context, CallConnectionService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                phoneAccountHandle = new PhoneAccountHandle(componentName, context.getPackageName());
                PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, context.getPackageName()).setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
                telecomManager.registerPhoneAccount(phoneAccount);
            }
        }
        return cometChatNotification;
    }


    public void registerCometChatNotification(final CometChat.CallbackListener<String> listener) {
        if (!isFirebaseAppInitialized()) {
            listener.onError(new CometChatException(ConstantFile.ErrorStrings.NOTIFICATION_NOT_REGISTERED, ConstantFile.ErrorStrings.FIREBASE_NOT_REGISTERED));
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, context.getString(R.string.cometchat_fcmtoken_failed), task.getException());
                    return;
                }
                String token = task.getResult();
                Log.i(TAG, "Push Notification Token = " + token);
                CometChatNotifications.registerPushToken(token, PushPlatforms.FCM_ANDROID, AppConfig.AppDetails.FCM_PROVIDER_ID, new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        listener.onSuccess(s);
                    }

                    @Override
                    public void onError(CometChatException e) {
                        listener.onError(e);
                    }
                });
            }
        });
    }

    public void renderCometChatNotification(RemoteMessage remoteMessage, final CometChat.CallbackListener<String> listener) {
        JSONObject data = new JSONObject(remoteMessage.getData());

        try {
            switch (data.getString(ConstantFile.IntentStrings.TYPE)) {
                case ConstantFile.IntentStrings.CHAT:
                    renderTextMessageNotification(data);
                    break;

                case CometChatConstants.CATEGORY_CALL:
                    handleCallNotification(data);
                    break;
                default:
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleCallNotification(JSONObject data) {

        try {
            long callTime = Long.parseLong(data.getString(ConstantFile.IntentStrings.SENT_AT));
            if (data.getString(ConstantFile.IntentStrings.CALL_ACTION).equals(CometChatConstants.CALL_STATUS_INITIATED) && System.currentTimeMillis() <= (callTime + 30000)) {
                Call call = new Call(data.getString(ConstantFile.IntentStrings.RECEIVER), data.getString(ConstantFile.IntentStrings.RECEIVER_TYPE), data.getString(ConstantFile.IntentStrings.CALL_TYPE));
                call.setSessionId(data.getString(ConstantFile.IntentStrings.SESSION_ID));
                if (data.getString(ConstantFile.IntentStrings.RECEIVER_TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    User user = new User();
                    user.setUid(data.getString(ConstantFile.IntentStrings.RECEIVER));
                    user.setName(data.getString(ConstantFile.IntentStrings.RECEIVER_NAME));
                    user.setAvatar(data.getString(ConstantFile.IntentStrings.RECEIVER_AVATAR));
                    call.setCallInitiator(user);
                    call.setCallReceiver(user);
                } else {
                    Group group = new Group();
                    group.setGuid(data.getString(ConstantFile.IntentStrings.RECEIVER));
                    group.setName(data.getString(ConstantFile.IntentStrings.RECEIVER_NAME));
                    group.setIcon(data.getString(ConstantFile.IntentStrings.RECEIVER_AVATAR));
                    call.setCallInitiator(group);
                    call.setCallReceiver(group);
                }
                startIncomingCall(call);
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && telecomManager != null) {
                    boolean isInCall = telecomManager.isInCall();
                    if (isInCall) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            telecomManager.endCall();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public void startIncomingCall(Call call) {
        AppEntity entity = call.getCallInitiator();

        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) == PackageManager.PERMISSION_GRANTED) {
            Bundle extras = new Bundle();
            Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, call.getSessionId().substring(0, 11), null);

            extras.putString(ConstantFile.IntentStrings.SESSION_ID, call.getSessionId());
            extras.putString(ConstantFile.IntentStrings.TYPE, call.getReceiverType());
            extras.putString(ConstantFile.IntentStrings.CALL_TYPE, call.getType());

            if (entity instanceof User) {
                extras.putString(ConstantFile.IntentStrings.NAME, ((User) entity).getName());
            } else {
                extras.putString(ConstantFile.IntentStrings.NAME, ((Group) entity).getName());
            }

            if (entity instanceof User) {
                extras.putString(ConstantFile.IntentStrings.ID, ((User) entity).getUid());
            } else {
                extras.putString(ConstantFile.IntentStrings.ID, ((Group) entity).getGuid());
            }

            if (call.getType().equalsIgnoreCase(CometChatConstants.CALL_TYPE_VIDEO)) {
                extras.putInt(TelecomManager.EXTRA_INCOMING_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL);
            } else {
                extras.putInt(TelecomManager.EXTRA_INCOMING_VIDEO_STATE, VideoProfile.STATE_AUDIO_ONLY);
            }

            extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telecomManager.isIncomingCallPermitted(phoneAccountHandle);
                }
                telecomManager.addNewIncomingCall(phoneAccountHandle, extras);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
    }

    private void renderTextMessageNotification(JSONObject message) {
        try {
            if (message.getString(ConstantFile.IntentStrings.RECEIVER_TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {

                if (message.has(ConstantFile.IntentStrings.SENDER_AVATAR)) {
                    showNotification(message.getInt(ConstantFile.IntentStrings.TAG), message.getString(ConstantFile.IntentStrings.SENDER_NAME), message.getString(ConstantFile.IntentStrings.BODY), message.getString(ConstantFile.IntentStrings.SENDER_AVATAR), message);
                } else {
                    showNotification(message.getInt(ConstantFile.IntentStrings.TAG), message.getString(ConstantFile.IntentStrings.SENDER_NAME), message.getString(ConstantFile.IntentStrings.BODY), "", message);
                }
            } else {
                if (message.has(ConstantFile.IntentStrings.SENDER_AVATAR)) {
                    showNotification(message.getInt(ConstantFile.IntentStrings.TAG), message.getString(ConstantFile.IntentStrings.RECEIVER_NAME), message.getString(ConstantFile.IntentStrings.BODY), message.getString(ConstantFile.IntentStrings.SENDER_AVATAR), message);
                } else {
                    showNotification(message.getInt(ConstantFile.IntentStrings.TAG), message.getString(ConstantFile.IntentStrings.RECEIVER_NAME), message.getString(ConstantFile.IntentStrings.BODY), "", message);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean checkAccountConnection(Context context) {
        boolean isConnected = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && telecomManager != null) {
                final List<PhoneAccountHandle> enabledAccounts = telecomManager.getCallCapablePhoneAccounts();
                for (PhoneAccountHandle account : enabledAccounts) {
                    if (account.getComponentName().getClassName().equals(CallConnectionService.class.getCanonicalName())) {
                        isConnected = true;
                        break;
                    }
                }
            }
        }
        return isConnected;
    }

    private void showNotification(int nid, String title, String text, String largeIconUrl, JSONObject payload) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ConstantFile.IntentStrings.MESSAGES, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Your messages!!");

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ConstantFile.IntentStrings.MESSAGES);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setAutoCancel(true);

        if (!TextUtils.isEmpty(largeIconUrl)) {
            builder.setLargeIcon(getBitmapFromURL(largeIconUrl));
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background));
        }

        Intent intent = new Intent(context, HomeScreenActivity.class);

        intent.putExtra(ConstantFile.IntentStrings.NOTIFICATION_PAYLOAD, payload.toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 501, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(nid, notification);
    }

    private static boolean isFirebaseAppInitialized() {
        return !FirebaseApp.getApps(context).isEmpty();
    }

    private Bitmap getBitmapFromURL(String strURL) {
        if (strURL != null) {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}