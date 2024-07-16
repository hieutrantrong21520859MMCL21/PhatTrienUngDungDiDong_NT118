package com.example.indoorairqualitymonitoring;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.indoorairqualitymonitoring.support.LocaleHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RegisterScreen extends AppCompatActivity
{
    private Button btnSignUp;
    private Button btnBack;
    private FloatingActionButton fabLanguage;
    private TextView tvRegister;
    private TextView tvAlreadyHaveAccount;
    private TextView tvSignInOption;
    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private CheckBox chkShowPassword;
    private String[] languages;

    // Language code (such as "vn" for Vietnamese, "en" for English, etc)
    private String langCode;

    // Index of language in Alert Dialog
    private int selectedLangPos;

    // Error if registration fails
    private String registerErr;
    private LocaleHelper localeHelper;

    // Receive only intent from Loading Screen
    private ActivityResultLauncher<Intent> loadingIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 100)
                    {
                        Intent receiveFromLoadingScreen = result.getData();
                        registerErr = receiveFromLoadingScreen.getStringExtra("registerErr");
                        Log.d("register", registerErr);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initiate();
        Log.d("register","on create");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check validation of input
                if (isValidCredentials(edtUsername, edtEmail, edtPassword, edtConfirmPassword))
                {
                    Intent loading = new Intent(RegisterScreen.this, LoadingScreen.class);

                    String username = edtUsername.getText().toString();
                    String email = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();
                    String confirmPassword = edtConfirmPassword.getText().toString();

                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("email", email);
                    bundle.putString("password", password);
                    bundle.putString("confirmPassword", confirmPassword);

                    loading.putExtra("register", bundle);
                    loadingIntentLauncher.launch(loading);
                }
            }
        });

        tvSignInOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signingIn = new Intent(RegisterScreen.this, LoginScreen.class);
                startActivity(signingIn);
            }
        });

        chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    edtPassword.setTransformationMethod(null);
                    edtConfirmPassword.setTransformationMethod(null);
                }
                else
                {
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    edtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fabLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterScreen.this);
                alertDialog.setTitle(getResources().getString(R.string.langDialogTitle))
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
                alertDialog.setCancelable(false);
                alertDialog.create().show();
            }
        });
    }

    // Get data from shared preferences and set up language
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("register","on resume");
        langCode = localeHelper.getLanguageCode();
        selectedLangPos = localeHelper.getSelectedLanguagePosition();
        if (langCode.equals("en"))
        {
            Log.d("register","resume en");
            localeHelper.setLocale(getBaseContext(),"en");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.britain_flag);
        }
        else if (langCode.equals("vn"))
        {
            Log.d("register","resume vn");
            localeHelper.setLocale(getBaseContext(),"vn");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.vietnam_flag);
        }
        else
        {
            Log.d("register","resume jp");
            localeHelper.setLocale(getBaseContext(),"jp");
            changeConfig();
            fabLanguage.setImageResource(R.drawable.japan_flag);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("register","on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("register","on stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("register","on destroy");
    }

    private void initiate()
    {
        // Reference to ID of the widget
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);
        tvSignInOption = findViewById(R.id.tvSignInOption);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        chkShowPassword = findViewById(R.id.chkShowPassword);
        tvRegister = findViewById(R.id.tvRegister);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
        fabLanguage = findViewById(R.id.fabLanguage);

        // Create instance of class LocaleHelper
        localeHelper = new LocaleHelper(getBaseContext());
    }

    private void showError(EditText input, String message)
    {
        input.setError(message);
        input.requestFocus();
    }

    private boolean isValidCredentials(EditText edtUsername, EditText edtEmail, EditText edtPassword, EditText edtConfirmPassword)
    {
        String username = edtUsername.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (username.isEmpty())
        {
            showError(edtUsername,getResources().getString(R.string.emptyUsername));
            return false;
        }

        if (email.isEmpty())
        {
            showError(edtEmail, getResources().getString(R.string.emptyEmail));
            return false;
        }

        if (password.isEmpty())
        {
            showError(edtPassword, getResources().getString(R.string.emptyPassword));
            return false;
        }

        if (confirmPassword.isEmpty())
        {
            showError(edtConfirmPassword, getResources().getString(R.string.emptyConfirmPass));
            return false;
        }

        if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
        {
            showError(edtEmail, getResources().getString(R.string.invalidEmail));
            return false;
        }

        if (username.length() > 20)
        {
            showError(edtUsername, getResources().getString(R.string.limitCharactersUsername));
        }

        if (password.length() > 20)
        {
            showError(edtPassword, getResources().getString(R.string.limitCharactersPass));
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

        if (!confirmPassword.equals(password))
        {
            showError(edtConfirmPassword, getResources().getString(R.string.notEqualsPass));
            return false;
        }

        return true;
    }

    private void changeConfig()
    {
        tvRegister.setText(getResources().getString(R.string.register));
        edtUsername.setHint(getResources().getString(R.string.username));
        edtEmail.setHint(getResources().getString(R.string.email));
        edtPassword.setHint(getResources().getString(R.string.pwd));
        edtConfirmPassword.setHint(getResources().getString(R.string.confirmPWD));
        chkShowPassword.setText(getResources().getString(R.string.showPWD));
        btnSignUp.setText(getResources().getString(R.string.createAccount));
        tvAlreadyHaveAccount.setText(getResources().getString(R.string.alreadyHaveAccount));
        tvSignInOption.setText(Html.fromHtml("<u>" + getResources().getString(R.string.signIn_Option) + "</u>"));
        btnBack.setText(getResources().getString(R.string.back));

        // Set languages in Alert Dialog by order
        languages = new String[]{getResources().getString(R.string.langEN), getResources().getString(R.string.langVN), getResources().getString(R.string.langJP)};
    }
}