package com.example.androidtests.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;
import com.example.androidtests.R;
import com.example.androidtests.databinding.LoginBinding;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.ui.MainActivity;
import com.example.androidtests.utils.ContextWrapper;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.LoginViewModel;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private LoginBinding binding;
    Button signInButton;
    Button registerButton;
    SharedPreferences sharedPreferences;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        //SaveSharedPreference.setLoggedIn(getApplicationContext(),false);

        signInButton = binding.signInButton;
        registerButton = binding.registerButton;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            binding.loginForm.setVisibility(View.VISIBLE);
        }

        viewModel.getUser().observe(this , user -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            SaveSharedPreference.setLogedInUser(getApplicationContext(),user);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> {
            String password = binding.editTextTextPassword.getText().toString();
            String email = binding.editTextTextEmailAddress.getText().toString();
            UserLoginRequest requestBody = new UserLoginRequest(email, password);
            sendRequest(requestBody);
        });
    }

    private void sendRequest(UserLoginRequest infos) {
        viewModel.login(infos);
    }

    protected void attachBaseContext(Context newBase) {
        String locale = SaveSharedPreference.getLocale(newBase);

        Context context = ContextWrapper.wrap(newBase, new Locale(locale));
        super.attachBaseContext(context);
    }
}
