package com.example.androidtests.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;
import com.example.androidtests.R;
import com.example.androidtests.databinding.LoginBinding;
import com.example.androidtests.models.User;
import com.example.androidtests.models.UserLoginRequest;
import com.example.androidtests.ui.MainActivity;
import com.example.androidtests.utils.ContextWrapper;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;
import com.example.androidtests.viewModels.LoginViewModel;

import java.util.Date;
import java.util.Locale;

import static com.example.androidtests.utils.General.triggerRebirth;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private LoginBinding binding;
    Button signInButton;
    Button registerButton;
    private String firstName, lastName, email, password, confirm;
    SharedPreferences sharedPreferences;
    private LoginViewModel viewModel;
    private Spinner yearSpinner;
    Integer birthYear;

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
        yearSpinner = binding.yearSpinner;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
             if(SaveSharedPreference.getLogedInUser(getApplicationContext()).getExpDate()
                    .after(new Date())) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                binding.loginForm.setVisibility(View.VISIBLE);
            }
        } else {
            binding.loginForm.setVisibility(View.VISIBLE);
        }

        viewModel.getUser().observe(this , user -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            SaveSharedPreference.setLogedInUser(getApplicationContext(),user);
            startActivity(intent);
        });

        viewModel.getUserNotFound().observe(this, res -> {
            Toast.makeText(this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
        });

        viewModel.getRegistered().observe(this, registered -> {
            binding.loginForm.setVisibility(View.VISIBLE);
            binding.signUpForm.setVisibility(View.GONE);
        });

        binding.backButton.setOnClickListener(V -> {
            binding.loginForm.setVisibility(View.VISIBLE);
            binding.signUpForm.setVisibility(View.GONE);
        });

        registerButton.setOnClickListener(v -> {
           binding.loginForm.setVisibility(View.GONE);
           binding.signUpForm.setVisibility(View.VISIBLE);
        });

        signInButton.setOnClickListener(this::validateLoginForm);
        binding.validateButton.setOnClickListener(this::validateRegisterForm);

        Integer[] array = new Integer[50];
        for(int i = 0; i < array.length; i++)
            array[i]= 2010 - i;

        ArrayAdapter<Integer> adapterStringSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapterStringSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapterStringSpinner);
        yearSpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        birthYear = (Integer) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //méthode autoincrémentée
    }

    private void sendRequest(UserLoginRequest infos) {
        viewModel.login(infos);
    }

    protected void attachBaseContext(Context newBase) {
        String locale = SaveSharedPreference.getLocale(newBase);

        Context context = ContextWrapper.wrap(newBase, new Locale(locale));
        super.attachBaseContext(context);
    }

    private void validateRegisterForm(View view) {
        firstName = binding.firstNameText.getText().toString();
        lastName = binding.lastNameText.getText().toString();
        email = binding.emailText.getText().toString();
        password = binding.passwordText.getText().toString();
        confirm = binding.confirmText.getText().toString();

        if(!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty()
                && !confirm.isEmpty()) {
            if(email.matches("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$")) {
                if(confirm.equals(password)) {
                    User newUser = new User(firstName, lastName, email, password, birthYear);
                    viewModel.register(newUser);
                    Toast.makeText(getBaseContext(), getString(R.string.user_created), Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.email_not_valid), Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.formular_incomplete), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void validateLoginForm(View view) {
        String password = binding.editTextTextPassword.getText().toString();
        String email = binding.editTextTextEmailAddress.getText().toString();
        //String firebaseToken = SaveSharedPreference.getFirebaseToken(getBaseContext());
        if(!password.isEmpty() && !email.isEmpty()) {
            if(email.matches("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$")) {
                UserLoginRequest requestBody = new UserLoginRequest(email, password);
                sendRequest(requestBody);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.email_not_valid), Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.formular_incomplete), Toast.LENGTH_LONG)
                    .show();
        }
    }
}
