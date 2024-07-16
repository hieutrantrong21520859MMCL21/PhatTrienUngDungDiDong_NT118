package com.example.indoorairqualitymonitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.TokenService;
import com.example.indoorairqualitymonitoring.support.LocaleHelper;
import com.example.indoorairqualitymonitoring.model.Token;
import com.example.indoorairqualitymonitoring.support.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity
{
    private Button btnSignIn;
    private Button btnBack;
    private FloatingActionButton fabLanguage;
    private TextView tvSignUpOption;
    private TextView tvForgetPassword;
    private TextView tvWelcome;
    private TextView tvHaveAnyAccount;
    private EditText edtUsername;
    private EditText edtPassword;
    private CheckBox chkShowPassword;
    private TokenService tokenService;
    private String[] languages;

    // Index of language in Alert Dialog
    private int selectedLangPos;
    private LocaleHelper localeHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initiate();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check validation of input
                if (isValidCredentials(edtUsername, edtPassword))
                {
                    accessHomeScreen();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSignUpOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signingUp = new Intent(LoginScreen.this, RegisterScreen.class);
                startActivity(signingUp);
            }
        });

        chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    edtPassword.setTransformationMethod(null);
                }
                else
                {
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        fabLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
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
        Log.d("login","on resume");

        // Language code (such as "vn" for Vietnamese, "en" for English, etc)
        String langCode = localeHelper.getLanguageCode();
        selectedLangPos = localeHelper.getSelectedLanguagePosition();

        if (langCode.equals("en"))
        {
            Log.d("login","resume en");
            localeHelper.setLocale(getBaseContext(),"en");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.britain_flag);
        }
        else if (langCode.equals("vn"))
        {
            Log.d("login","resume vn");
            localeHelper.setLocale(getBaseContext(),"vn");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.vietnam_flag);
        }
        else
        {
            Log.d("login","resume jp");
            localeHelper.setLocale(getBaseContext(),"jp");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.japan_flag);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("login","on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("login","on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("login","on destroy");
    }

    private void initiate()
    {
        // Reference to ID of the widget
        btnSignIn = findViewById(R.id.btnSignIn);
        btnBack = findViewById(R.id.btnBack);
        tvSignUpOption = findViewById(R.id.tvSignUpOption);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvHaveAnyAccount = findViewById(R.id.tvHaveAnyAccount);
        chkShowPassword = findViewById(R.id.chkShowPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        fabLanguage = findViewById(R.id.fabLanguage);

        // Create instance of class LocaleHelper
        localeHelper = new LocaleHelper(getBaseContext());

        // Create instance to call API
        tokenService = ApiClient.getClient("https://uiot.ixxc.dev/").create(TokenService.class);

        // Create an instance to check session
        sessionManager = new SessionManager(this);
    }

    private void showError(EditText input, String message)
    {
        input.setError(message);
        input.requestFocus();
    }

    private boolean isValidCredentials(EditText edtUsername, EditText edtPassword)
    {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (username.isEmpty())
        {
            showError(edtUsername, getResources().getString(R.string.emptyUsername));
            return false;
        }

        if (password.isEmpty())
        {
            showError(edtPassword, getResources().getString(R.string.emptyPassword));
            return false;
        }

        if (password.length() > 20)
        {
            showError(edtPassword, getResources().getString(R.string.limitCharactersPass));
            return false;
        }

        if (username.length() > 20)
        {
            showError(edtUsername, getResources().getString(R.string.limitCharactersUsername));
            return false;
        }

        if (password.contains(" "))
        {
            showError(edtPassword, getResources().getString(R.string.noSpacesPass));
            return false;
        }

        if (username.contains(" "))
        {
            showError(edtUsername, getResources().getString(R.string.noSpacesUsername));
            return false;
        }

        return true;
    }

    // Call API to get token using Retrofit2
    private void accessHomeScreen() {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        Call<Token> call = tokenService.getToken(getString(R.string.classId), username, password, getString(R.string.grantType));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Log.d("login", "successfully");

                    // Save session
                    Token token = response.body();
                    sessionManager.saveSession(token, username, password);

                    Intent openingHomeScreen = new Intent(LoginScreen.this, HomeScreen.class);
                    startActivity(openingHomeScreen);
                    finish();
                }
                else
                {
                    Log.d("login", "account not exists");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.d("Call api login error", t.toString());
            }
        });
    }

    private void changeConfig()
    {
        tvWelcome.setText(getResources().getString(R.string.welcome));
        edtUsername.setHint(getResources().getString(R.string.username));
        edtPassword.setHint(getResources().getString(R.string.pwd));
        tvForgetPassword.setText(getResources().getString(R.string.forgetPWD));
        chkShowPassword.setText(getResources().getString(R.string.showPWD));
        btnSignIn.setText(getResources().getString(R.string.signIn));
        tvHaveAnyAccount.setText(getResources().getString(R.string.haveAnyAccount));
        tvSignUpOption.setText(Html.fromHtml("<u>" + getResources().getString(R.string.signUp_Option) + "</u>"));
        btnBack.setText(getResources().getString(R.string.back));

        // Set languages in Alert Dialog by order
        languages = new String[]{getResources().getString(R.string.langEN), getResources().getString(R.string.langVN), getResources().getString(R.string.langJP)};
    }
}