package com.wayneyong.github_api_retrofit_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wayneyong.github_api_retrofit_app.databinding.ActivityLogInBinding;

public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding logInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_log_in);/

        // Use view binding in activity
        logInBinding = ActivityLogInBinding.inflate(getLayoutInflater()); //inflate, create instance of binding class for activity to use
        View view = logInBinding.getRoot(); //getRoot, get a reference to root view
        setContentView(view); //pass root view to setContent

    }

    public void getUser(View view) {

        Intent intent = new Intent(LogInActivity.this, UserActivity.class);
        intent.putExtra("userName", logInBinding.inputUsername.getText().toString());
        startActivity(intent);
    }
}