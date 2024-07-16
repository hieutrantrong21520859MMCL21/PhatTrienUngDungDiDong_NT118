package com.example.indoorairqualitymonitoring;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.TokenService;
import com.example.indoorairqualitymonitoring.model.Token;
import com.example.indoorairqualitymonitoring.support.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingScreen extends AppCompatActivity {
    private TextView tvWait;
    private ProgressBar progressBar;
    private WebView webView;
    private TokenService tokenService;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private SessionManager sessionManager;

    // Count the amount of redirection to https://uiot.ixxc.dev/manager/
    private int count = 0;

    //@SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initiate();
        SharedPreferences shrPrefs = getSharedPreferences("languages", MODE_PRIVATE);
        String langCode = shrPrefs.getString("langCode","en");

        // Check language being used
        if (langCode == "en") tvWait.setText(getString(R.string.waitLoadingEN));
        else if (langCode == "vn") tvWait.setText(getString(R.string.waitLoadingVN));
        else tvWait.setText(getString(R.string.waitLoadingJP));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("loading",url);
                if (url.contains("openid-connect/auth"))
                {
                    String redirectScript = "javascript:document.getElementsByTagName('a')[0].click();";
                    view.evaluateJavascript(redirectScript,null);
                }

                if (url.contains("login-actions/registration"))
                {
                    String errorScript = "document.getElementsByClassName('helper-text')[0].getAttribute('data-error');";
                    view.evaluateJavascript(errorScript, err -> {
                        if (err.equals("null"))
                        {
                            String script = "javascript:document.getElementById('username').value = '" + username + "';" +
                                    "document.getElementById('email').value = '" + email + "';" +
                                    "document.getElementById('password').value = '" + password + "';" +
                                    "document.getElementById('password-confirm').value = '" + confirmPassword + "';" +
                                    "document.getElementById('kc-register-form').submit();";
                            view.evaluateJavascript(script,null);
                        }
                        else
                        {
                            Intent backToRegisterScreen = new Intent(LoadingScreen.this, RegisterScreen.class);
                            backToRegisterScreen.putExtra("registerErr", err);
                            setResult(100, backToRegisterScreen);
                            finish();
                        }
                    });
                }

                if (url.contains("manager"))
                {
                    Log.d("loading","enter manager");
                    count++;
                    if (count > 2)
                    {
                        Log.d("loading","enter manager " + count + " times");
                        accessHomeScreen();
                    }
                }
            }
        });

        // Progress bar is executing
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        webView.loadUrl("https://uiot.ixxc.dev/");
    }

    private void initiate()
    {
        // Reference to widgets' ID
        tvWait = findViewById(R.id.tvWait);
        progressBar = findViewById(R.id.progressBar);
        webView = new WebView(getBaseContext());

        // Create an instance to check session
        sessionManager = new SessionManager(this);

        CookieManager.getInstance().removeAllCookies(null);

        // Create an API instance
        tokenService = ApiClient.getClient("https://uiot.ixxc.dev/").create(TokenService.class);

        Bundle bundle = getIntent().getBundleExtra("register");
        username = bundle.getString("username");
        email = bundle.getString("email");
        password = bundle.getString("password");
        confirmPassword = bundle.getString("confirmPassword");
    }

    private void accessHomeScreen()
    {
        Call<Token> call = tokenService.getToken(getString(R.string.classId), username, password, getString(R.string.grantType));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful())
                {
                    // Save session
                    Token token = response.body();
                    sessionManager.saveSession(token, username, password);

                    Intent openingHome = new Intent(LoadingScreen.this, HomeScreen.class);
                    startActivity(openingHome);
                    finish();
                }
                else
                {
                    Log.d("loading","registration fails");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.d("Call api register error", t.toString());
            }
        });
    }
}