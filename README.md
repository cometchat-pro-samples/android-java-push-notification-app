<p align="center">
  <img alt="CometChat" src="https://assets.cometchat.io/website/images/logos/banner.png">
</p>

# Android Push Notification Sample App

The CometChat Android Push Notification Sample App is capable of handling push notifications for one-on-one (private), group messaging, and even call notifications. This sample app enables users to send and receive text messages, make and receive calls, and effectively displays push notifications for these interactions.

The sample app uses Firebase Cloud Messaging (FCM) for delivering push notifications to Android.

> [!NOTE]
> If you are using Push Notifications (Extension), please refer to our [Android Push Notifications (Extension)](https://github.com/cometchat/cometchat-push-notification-app-android/tree/v4-push-notifications-extension) sample app.

## Pre-requisite

1. Login to the [CometChat Dashboard](https://app.cometchat.com/).
2. Select an existing app or create a new one.
3. Click on the Notifications section from the menu on the left.
4. Enable Push Notifications by clicking on the toggle bar and configure the push notifications.
5. Add credentials for FCM.

## Run the Sample App

1. Clone this repository.
2. Add your app credentials like `APP_ID`, `REGION`, and `AUTH_KEY` in the `AppConfig.java` file.
3. Also add the `FCM_PROVIDER_ID` in `AppConfig.java` as that is required while registering push token.
4. Place the `google-services.json` file in the correct location as per FCM's documentation.
5. Once the app is running on your device or emulator, login with a user.
6. Allow the permission to display push notifications.
7. Put the app in the background or terminate it.
8. Send a message or call to the logged in user from another device.
9. You should see a push notification for a message and call notification for a call.
10. Tap on the notification to open the Sample app for message.
11. Tap on "Accept" to join an incoming call or tap on "Decline" to reject it.

## Help and Support

For issues running the project or integrating with our UI Kits, consult our [documentation](https://www.cometchat.com/docs/) or create a [support ticket](https://help.cometchat.com/hc/en-us) or seek real-time support via the [CometChat Dashboard](https://app.cometchat.com/).
