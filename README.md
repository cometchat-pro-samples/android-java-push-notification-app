# Android Java Push Notification App

CometChat Push Notification Sample App is a fully functional push notification app capable of one-on-one (private) and group messaging, and Calling. This sample app enables users to send and receive push notifications for text and multimedia messages like **images, videos, documents** and **Custom Messages**. Also, users can make push notifications for Audio and Video calls to other users or groups.

<hr/>

## Pre-requisite :star:
1. Login to the <a href="https://app.cometchat.io/" target="_blank">CometChat Dashboard</a>.
2. Select an existing app or create a new one.
3. Go to the credentials section and copy the `Auth` key from the credentials tab.
4. Go to the "Extensions" section and Enable the Push Notifications extension.
5. Go to the "Installed" tab in the same section to open the settings for this extension and Set the version to Token-based(V2).
6. Also, save the `FCM Server key` (received from [Firebase Console](https://console.firebase.google.com/)) in the Settings and click on Save.
7. Copy the `APP_ID`, `REGION`, and `AUTH_KEY` for your app.
</br>

 
## Installation :wrench:

1. Clone the repository:
   ```sh
   git clone https://github.com/cometchat/cometchat-push-notification-app-android.git
    ```
2. Open the project in Android Studio
   

## Run the Sample App

To Run the sample push notifications app, do the following changes by Adding **APP_ID**, **AUTH_KEY**, **REGION**, and **google-services.json**

   You can obtain your  *APP_ID*, *AUTH_KEY* and *REGION* from [CometChat-Pro Dashboard](https://app.cometchat.io/)

   You can Obtain your  *google-services.json* from [Firebase Console](https://console.firebase.google.com/)

   - Open the project in Android Studio.

   - Replace the `APP_ID`, and `AUTH_KEY`, and `REGION` with your app credentials from [CometChat-Pro Dashboard](https://app.cometchat.io/).
   
   - Add `google-services.json` in **app** directory

   - Once the app is launched, select the demo users or enter the **UID** to log in.


Build and run the Sample App with Android Studio.
       

## Note
  
   You can Obtain your  *APP_ID* and *API_KEY* from [CometChat-Pro Dashboard](https://app.cometchat.com/)

   For more information read [CometChat-Pro Android SDK](https://prodocs.cometchat.com/docs/android-quick-start)
   Documentation

   You can Obtain your  *google-services.json* from [Firebase Console](https://console.firebase.google.com/)


## 📝 Documentation

CometChat provides to implement push notifications for your app.

 1. [Firebase](https://prodocs.cometchat.com/docs/android-extensions-enhanced-push-notification)

 2. [ConnectionService + Firebase](https://prodocs.cometchat.com/docs/android-push-notification-with-connectionservice) 


