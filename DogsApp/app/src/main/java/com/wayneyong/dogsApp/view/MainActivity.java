package com.wayneyong.dogsApp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.wayneyong.dogsApp.R;
import com.wayneyong.dogsApp.databinding.FragmentDetailBinding;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_SEND_SMS = 342;

    private NavController navController;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
        //put a back on toolbar, back to the hierarchy
    }

    public void checkSmsPermission() {
        //check if we have sms permission or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //ask system do we need to provide user with rationale
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Send SMS permission")
                        .setMessage("This app requires access to send an SMS")
                        .setPositiveButton("Ask me", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSmsPermission();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notifyDetailFragment(false);
                            }
                        });
            } else {
                requestSmsPermission();
            }
        } else {
            notifyDetailFragment(true);
        }
    }

    private void requestSmsPermission() {
        String[] permissions = {Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_SEND_SMS);
    }

    //result of the request, will receive in
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)  //check permission granted or refused
                {
                    notifyDetailFragment(true);
                } else {
                    notifyDetailFragment(false);
                }
                break;
            }
        }
    }

    private void notifyDetailFragment(Boolean permisssionGranted) {
        //retrieve active fragment to check if it is detailFragment, at this moment, we could be in detail, list or settings fragment
        Fragment activeFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (activeFragment instanceof DetailFragment) {
            ((DetailFragment) activeFragment).onPermissionResult(permisssionGranted); //detail fragment parameter(onPermissionResult)
        }
    }
}