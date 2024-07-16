package com.example.indoorairqualitymonitoring.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.LoginScreen;
import com.example.indoorairqualitymonitoring.OpeningScreen;
import com.example.indoorairqualitymonitoring.R;
import com.example.indoorairqualitymonitoring.api.ApiClient;
import com.example.indoorairqualitymonitoring.api.AssetService;
import com.example.indoorairqualitymonitoring.dialogfragment.ChangeLanguageDialog;
import com.example.indoorairqualitymonitoring.dialogfragment.ResetPasswordDialog;
import com.example.indoorairqualitymonitoring.model.User;
import com.example.indoorairqualitymonitoring.support.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = AccountFragment.class.getName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    private AppCompatButton btnLanguage, btnResetPassword, btnSignOut;
    private TextView tvUsername, tvEmail, tvCreatedOn;
    private View view;
    private AssetService assetService;
    private Context context;
    private String token;

    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireActivity().getBaseContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        initiate();
        setUserDetails();

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLanguageDialog dialog = new ChangeLanguageDialog();
                dialog.show(getParentFragmentManager(), ChangeLanguageDialog.TAG);
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPasswordDialog dialog = new ResetPasswordDialog();
                dialog.show(getParentFragmentManager(), ResetPasswordDialog.TAG);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getResources().getString(R.string.signOutDialogTitle));
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HomeScreen.sessionManager.clearSession();

                        // Go back to Opening Screen
                        Activity currActivity = (HomeScreen)getActivity();
                        Intent backToOpeningScreen = new Intent(currActivity, OpeningScreen.class);
                        startActivity(backToOpeningScreen);
                        currActivity.finish();
                    }
                });

                builder.create().show();
            }
        });

        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCreatedOn = view.findViewById(R.id.tvCreatedOn);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        // Create an API instance
        assetService = ApiClient.getClient("https://uiot.ixxc.dev/").create(AssetService.class);

        // Get token
        token = HomeScreen.sessionManager.getToken();
    }

    private void setUserDetails()
    {
        Call<User> call = assetService.getUser("Bearer " + token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful())
                {
                    User userDetails = response.body();
                    tvUsername.setText(userDetails.getUsername());
                    tvEmail.setText(userDetails.getEmail());

                    Date date = new Date(userDetails.getCreatedOn());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a", Locale.getDefault());
                    tvCreatedOn.setText(sdf.format(date));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}