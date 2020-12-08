package com.example.androidtests.ui.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.androidtests.R;
import com.example.androidtests.ui.MainActivity;
import com.example.androidtests.ui.login.LoginActivity;
import com.example.androidtests.utils.sharedPreferences.SaveSharedPreference;

import java.util.Locale;

import static com.example.androidtests.utils.General.triggerRebirth;

public class Settings extends Fragment implements AdapterView.OnItemSelectedListener {
    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        Spinner spinnerLanguages = root.findViewById(R.id.sLanguages);
        int array = SaveSharedPreference.getLocale(getContext()).equals("en") ? R.array.languages : R.array.languagesInverted;
        ArrayAdapter<CharSequence> adapterStringSpinner = ArrayAdapter.createFromResource(getContext(), array, android.R.layout.simple_spinner_item);
        adapterStringSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguages.setAdapter(adapterStringSpinner);
        spinnerLanguages.setOnItemSelectedListener(this);
        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedLanguage = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getContext(), selectedLanguage, Toast.LENGTH_SHORT).show();

        String locale = selectedLanguage.equals("Français") || selectedLanguage.equals("French") ?
                "fr" : "en";

        if(!SaveSharedPreference.getLocale(getContext()).equals(locale)) {
            SaveSharedPreference.setLocale(getContext(),locale);
            triggerRebirth(getContext());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //méthode autoincrémentée
    }
}