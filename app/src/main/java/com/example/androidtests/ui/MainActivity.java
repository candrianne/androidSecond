package com.example.androidtests.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.example.androidtests.R;
import com.example.androidtests.utils.ContextWrapper;
import com.example.androidtests.utils.General;
import com.example.androidtests.utils.General.*;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private AppBarConfiguration bottomAppBarConfiguration;
    private BottomNavigationView navView;
    Map config = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navView = findViewById(R.id.bottomNavView);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_settings)
                .setDrawerLayout(drawer)
                .build();

        bottomAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_profil, R.id.navigation_ranking, R.id.navigation_challenges)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, bottomAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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

    private void configCloudinary() {
        config.put("cloud_name", "dq4qdktus");
        config.put("api_key", "632125386656348");
        config.put("api_secret", "1E8__30XzPAWGU1H-ZJlx7ZZC7o");
        MediaManager.init(this, config);
    }
}