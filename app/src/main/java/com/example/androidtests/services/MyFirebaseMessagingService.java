package com.example.androidtests.services;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidtests.R;
import com.example.androidtests.models.User;
import com.example.androidtests.ui.MainActivity;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.UserViewModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private UserViewModel viewModel;
    @Override
    public void onCreate() {
        super.onCreate();
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
                .create(UserViewModel.class);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        User user = SaveSharedPreference.getLogedInUser(getBaseContext());
        if(user != null) {
            user.setFirebaseToken(s);
            viewModel.updateUser(user);
        } else {
            SaveSharedPreference.setFirebaseToken(getBaseContext(), s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MainActivity.FRIENDS_CHANNEL_ID)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.friend_request)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
