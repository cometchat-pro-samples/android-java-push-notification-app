<p align="center">
  <img alt="CometChat" src="https://assets.cometchat.io/website/images/logos/banner.png">
</p>

# Android Push Notifications (Extension) Sample App

The CometChat Android [Push Notifications (Extension)](https://www.cometchat.com/docs-beta/extensions/android-connection-service) Sample App is a fully functional push notifications app capable of one-on-one (private) and group messaging, and Calling. This sample app enables users to send and receive push notifications for text and multimedia messages like **images, videos, documents** and **Custom Messages**. Also, users can make push notifications for Audio and Video calls to other users or groups.

> [!NOTE]
> If you wish to try Enhanced Push Notifications, please refer to our [Android Enhanced Push Notifications (Beta)](https://github.com/cometchat/cometchat-push-notification-app-android) sample app.


## Pre-requisite :star:

1. Login to the <a href="https://app.cometchat.com/" target="_blank">CometChat Dashboard</a>.
2. Select an existing app or create a new one.
3. Under the Extensions section, enable Push Notifications.
4. Configure Push Notifications by saving the required FCM credentials.

## Installation :wrench:

1. Clone the repository
2. Open the project in Android Studio

## Run the Sample App

1. To run the sample push notifications app, make the following changes by Adding **APP_ID**, **AUTH_KEY**, **REGION**, and **google-services.json**. You can obtain your **google-services.json** from [Firebase Console](https://console.firebase.google.com/)

- Open the project in Android Studio.

- Replace the `APP_ID`, and `AUTH_KEY`, and `REGION` with your app credentials from [CometChat Dashboard](https://app.cometchat.com/).

- Add `google-services.json` in **app** directory

- Once the app is launched, select the demo users or enter the **UID** to log in.

Build and run the Sample App with Android Studio.

## Help and Support

For issues running the project or integrating with our UI Kits, consult our [documentation](https://www.cometchat.com/docs-beta/extensions/android-connection-service) or create a [support ticket](https://help.cometchat.com/hc/en-us) or seek real-time support via the [CometChat Dashboard](https://app.cometchat.com/).
