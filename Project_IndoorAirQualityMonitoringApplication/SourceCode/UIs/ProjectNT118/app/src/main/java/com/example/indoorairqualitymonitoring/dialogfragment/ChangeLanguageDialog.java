package com.example.indoorairqualitymonitoring.dialogfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import com.example.indoorairqualitymonitoring.HomeScreen;
import com.example.indoorairqualitymonitoring.R;

public class ChangeLanguageDialog extends DialogFragment
{
    public static final String TAG = "ChangeLanguageDialog";
    private Button btnConfirm;
    private Button btnCancel;
    private RadioButton rbEnglish, rbVietnamese, rbJapanese;
    private RadioGroup rgLanguages;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.change_language_dialog, container,false);
        initiate();

        if (getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_background);
        }

        // Do not let user dismiss dialog when touching outside dialog
        getDialog().setCancelable(false);

        // Check which language is used to set state of radio buttons
        String langCode = HomeScreen.localeHelper.getLanguageCode();
        if (langCode.equals("en"))
        {
            rbEnglish.setChecked(true);
        }
        else if (langCode.equals("vn"))
        {
            rbVietnamese.setChecked(true);
        }
        else
        {
            rbJapanese.setChecked(true);
        }

        rgLanguages.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(getContext()!= null)
                {
                    if (checkedId == R.id.rbEnglish)
                    {
                        HomeScreen.localeHelper.setLocale(getActivity(),"en");
                    }
                    else if (checkedId == R.id.rbVietnamese)
                    {
                        HomeScreen.localeHelper.setLocale(getActivity(),"vn");
                    }
                    else
                    {
                        HomeScreen.localeHelper.setLocale(getActivity(),"jp");
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Closing Change Language Dialog");
                getDialog().dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void initiate()
    {
        // Reference to widgets' ID
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        rgLanguages = view.findViewById(R.id.rgLanguages);
        rbEnglish = view.findViewById(R.id.rbEnglish);
        rbVietnamese = view.findViewById(R.id.rbVietnamese);
        rbJapanese = view.findViewById(R.id.rbJapanese);
    }
}