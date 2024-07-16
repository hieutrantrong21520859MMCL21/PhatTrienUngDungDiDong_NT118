package com.example.indoorairqualitymonitoring.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.indoorairqualitymonitoring.R;

public class ResetPasswordDialog extends DialogFragment
{
    public static final String TAG = "ResetPasswordDialog";
    private EditText edtNewPassword;
    private EditText edtConfirmPassword;
    private Button btnConfirm;
    private Button btnCancel;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reset_password_dialog,container,false);
        initiate();

        if (getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }

        // Do not let user dismiss dialog when touching outside dialog
        getDialog().setCancelable(false);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Closing Reset Password Dialog");
                getDialog().dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsValidCredentials(edtNewPassword, edtConfirmPassword))
                {

                }
            }
        });

        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
    }

    private void showError(EditText edt, String message)
    {
        edt.setError(message);
        edt.requestFocus();
    }

    private boolean IsValidCredentials(EditText edtNewPassword, EditText edtConfirmPassword)
    {
        String password = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        if (password.isEmpty())
        {
            showError(edtNewPassword, getResources().getString(R.string.emptyPassword));
            return false;
        }

        if (confirmPassword.isEmpty())
        {
            showError(edtConfirmPassword, getResources().getString(R.string.emptyConfirmPass));
            return false;
        }

        if (password.length() > 20)
        {
            showError(edtNewPassword, getResources().getString(R.string.limitCharactersPass));
            return false;
        }

        if (password.contains(" "))
        {
            showError(edtNewPassword, getResources().getString(R.string.noSpacesPass));
            return false;
        }

        if (!confirmPassword.equals(password))
        {
            showError(edtConfirmPassword, getResources().getString(R.string.notEqualsPass));
            return false;
        }

        return true;
    }
}