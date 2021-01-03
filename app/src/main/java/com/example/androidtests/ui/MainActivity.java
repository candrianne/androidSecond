package com.example.androidtests.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.cloudinary.android.MediaManager;
import com.example.androidtests.R;
import com.example.androidtests.models.User;
import com.example.androidtests.utils.ContextWrapper;
import com.example.androidtests.utils.General;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.UserVModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private AppBarConfiguration bottomAppBarConfiguration;
    public static final String FRIENDS_CHANNEL_ID = "friendsChannelId";
    private UserVModel userViewModel;
    private User logedInUser;
    Map config = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        logedInUser = SaveSharedPreference.getLogedInUser(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView navView = findViewById(R.id.bottomNavView);

        userViewModel = new ViewModelProvider(this).get(UserVModel.class);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_settings,
                R.id.navigation_profil, R.id.navigation_ranking, R.id.navigation_challenges, R.id.navigation_friends,
                R.id.navigation_friendRequests)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(navView, navController);

        createFriendsNotificationsChannel();
        checkAndChangeFirebaseToken();
        configCloudinary();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String locale = SaveSharedPreference.getLocale(newBase);

        Context context = ContextWrapper.wrap(newBase, new Locale(locale));
        super.attachBaseContext(context);
    }

    public boolean clickLogOut(MenuItem item) {
        General.logout(this);
        return true;
    }

    private void checkAndChangeFirebaseToken() {
        if(!(logedInUser.getFirebaseToken() != null && logedInUser.getFirebaseToken().equals(
                SaveSharedPreference.getFirebaseToken(this))
        )) {
            User user = logedInUser;
            user.setFirebaseToken(SaveSharedPreference.getFirebaseToken(this));
            userViewModel.updateUser(user);
        }
    }

    private void configCloudinary() {
        config.put("cloud_name", "dq4qdktus");
        config.put("api_key", "632125386656348");
        config.put("api_secret", "1E8__30XzPAWGU1H-ZJlx7ZZC7o");
        MediaManager.init(this, config);
    }

    public void createFriendsNotificationsChannel() {
        NotificationChannel friendsChannel = new NotificationChannel(FRIENDS_CHANNEL_ID,
                "Friends",
                NotificationManager.IMPORTANCE_DEFAULT);
        friendsChannel.setLightColor(Color.GREEN);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.createNotificationChannel(friendsChannel);
    }
}