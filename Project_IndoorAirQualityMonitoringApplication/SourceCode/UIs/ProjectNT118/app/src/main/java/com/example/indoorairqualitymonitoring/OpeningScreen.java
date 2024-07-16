package com.example.indoorairqualitymonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indoorairqualitymonitoring.support.LocaleHelper;
import com.example.indoorairqualitymonitoring.support.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OpeningScreen extends AppCompatActivity
{
    private Button btnSignIn;
    private Button btnSignUp;
    private FloatingActionButton fabLanguage;
    private TextView tvIntro;
    private TextView tvOr;
    private TextView tvResetPassword;
    private String[] languages;

    // Index of language in Alert Dialog
    private int selectedLangPos;
    private LocaleHelper localeHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        initiate();

        // Check login session
        long loginTime = sessionManager.getTheLatestLoginTime();
        int expiration = sessionManager.getExpiration();

        if ((System.currentTimeMillis() / 1000) - loginTime < expiration) // Session' s expiration is 24 hours
        {
            Intent openingHomeScreen = new Intent(OpeningScreen.this, HomeScreen.class);
            startActivity(openingHomeScreen);
            finish();
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openingLoginScreen = new Intent(OpeningScreen.this, LoginScreen.class);
                startActivity(openingLoginScreen);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openingRegisterScreen = new Intent(OpeningScreen.this, RegisterScreen.class);
                startActivity(openingRegisterScreen);
            }
        });

        fabLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OpeningScreen.this);
                builder.setTitle(getResources().getString(R.string.langDialogTitle))
                        .setSingleChoiceItems(languages, selectedLangPos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int pos) {
                            switch (pos)
                            {
                                case 0:
                                    selectedLangPos = 0;
                                    localeHelper.setLocale(getBaseContext(),"en");
                                    fabLanguage.setImageResource(R.drawable.britain_flag);
                                    break;

                                case 1:
                                    selectedLangPos = 1;
                                    localeHelper.setLocale(getBaseContext(),"vn");
                                    fabLanguage.setImageResource(R.drawable.vietnam_flag);
                                    break;

                                case 2:
                                    selectedLangPos = 2;
                                    localeHelper.setLocale(getBaseContext(),"jp");
                                    fabLanguage.setImageResource(R.drawable.japan_flag);
                                    break;
                            }

                            changeConfig();
                        }
                        })
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int pos) {
                                dialogInterface.dismiss();
                            }
                        });

                // Do not let user dismiss dialog when touching outside dialog
                builder.setCancelable(false);
                builder.create().show();
            }
        });
    }

    // Get data from shared preferences and set up language
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("opening","on resume");

        // Language code (such as "vn" for Vietnamese, "en" for English, etc)
        String langCode = localeHelper.getLanguageCode();
        selectedLangPos = localeHelper.getSelectedLanguagePosition();

        if (langCode.equals("en"))
        {
            Log.d("opening","resume en");
            localeHelper.setLocale(this,"en");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.britain_flag);
        }
        else if (langCode.equals("vn"))
        {
            Log.d("opening","resume vn");
            localeHelper.setLocale(this,"vn");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.vietnam_flag);
        }
        else
        {
            Log.d("opening","resume jp");
            localeHelper.setLocale(this,"jp");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.japan_flag);
        }
    }

    private void initiate()
    {
        // Reference to ID of the widget
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvIntro = findViewById(R.id.tvIntro);
        tvOr = findViewById(R.id.tvOr);
        fabLanguage = findViewById(R.id.fabLanguage);

        // Create instance of class LocaleHelper
        localeHelper = new LocaleHelper(this);

        // Create an instance to check session
        sessionManager = new SessionManager(this);
    }

    private void changeConfig()
    {
        tvIntro.setText(getResources().getString(R.string.intro));
        tvOr.setText(getResources().getString(R.string.or));
        btnSignIn.setText(getResources().getString(R.string.signIn));
        btnSignUp.setText(getResources().getString(R.string.signUp));

        // Set languages in Alert Dialog by order
        languages = new String[]{getResources().getString(R.string.langEN),
                                getResources().getString(R.string.langVN),
                                getResources().getString(R.string.langJP)};
    }
}