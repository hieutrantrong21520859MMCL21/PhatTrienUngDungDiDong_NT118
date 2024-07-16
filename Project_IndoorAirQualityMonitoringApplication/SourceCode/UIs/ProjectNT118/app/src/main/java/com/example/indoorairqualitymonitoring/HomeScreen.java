package com.example.indoorairqualitymonitoring;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.indoorairqualitymonitoring.fragment.AccountFragment;
import com.example.indoorairqualitymonitoring.fragment.FeaturesFragment;
import com.example.indoorairqualitymonitoring.fragment.ChartFragment;
import com.example.indoorairqualitymonitoring.fragment.MapFragment;
import com.example.indoorairqualitymonitoring.support.LocaleHelper;
import com.example.indoorairqualitymonitoring.support.SessionManager;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeScreen extends AppCompatActivity
{
    private ChipNavigationBar chipNavigationBar;
    private Fragment activeFragment, mapFragment, featureFragment, graphFragment, accountFragment;
    public static LocaleHelper localeHelper;
    public static SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initiate();

        // Map screen presents at first
        chipNavigationBar.setItemSelected(R.id.map,true);
        mapFragment = new MapFragment();
        featureFragment = new FeaturesFragment();
        graphFragment = new ChartFragment();
        accountFragment = new AccountFragment();
        activeFragment = mapFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment, MapFragment.TAG)
                .add(R.id.fragment_container, featureFragment, FeaturesFragment.TAG).hide(featureFragment)
                .add(R.id.fragment_container, graphFragment, ChartFragment.TAG).hide(graphFragment)
                .add(R.id.fragment_container, accountFragment, AccountFragment.TAG).hide(accountFragment)
                .commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                if (index == R.id.map)
                {
                    fragmentTransaction.hide(activeFragment).show(mapFragment);
                    activeFragment = mapFragment;
                }
                else if (index == R.id.feature)
                {
                    fragmentTransaction.hide(activeFragment).show(featureFragment);
                    activeFragment = featureFragment;
                }
                else if (index == R.id.chart)
                {
                    fragmentTransaction.hide(activeFragment).show(graphFragment);
                    activeFragment = graphFragment;
                }
                else if (index == R.id.account)
                {
                    fragmentTransaction.hide(activeFragment).show(accountFragment);
                    activeFragment = accountFragment;
                }

                fragmentTransaction.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String langCode = localeHelper.getLanguageCode();

        if (langCode.equals("en"))
        {
            localeHelper.setLocale(this,"en");
        }
        else if (langCode.equals("vn"))
        {
            localeHelper.setLocale(this,"vn");
        }
        else
        {
            localeHelper.setLocale(this,"jp");
        }
    }

    private void initiate()
    {
        // Reference to widgets' ID
        chipNavigationBar = findViewById(R.id.bottomNav);
        localeHelper = new LocaleHelper(this);
        sessionManager = new SessionManager(this);
    }
}