package com.wayneyong.github_api_retrofit_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.bumptech.glide.Glide;
import com.wayneyong.github_api_retrofit_app.databinding.ActivityLogInBinding;
import com.wayneyong.github_api_retrofit_app.databinding.ActivityUserBinding;
import com.wayneyong.github_api_retrofit_app.model.GitHubUser;
import com.wayneyong.github_api_retrofit_app.model.ImageDownloader;
import com.wayneyong.github_api_retrofit_app.rest.APIClient;
import com.wayneyong.github_api_retrofit_app.rest.GitHubUserEndPoints;

import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    ActivityUserBinding userBinding;
    Bundle extra;
    String newString;
    Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userBinding = ActivityUserBinding.inflate(getLayoutInflater()); //inflate, create instance of binding class for activity to use
        View view = userBinding.getRoot();
        setContentView(view);

        extra = getIntent().getExtras();
        newString = extra.getString("userName");

        System.out.println(newString);
        loadData();
    }

    private void loadData() {

        final GitHubUserEndPoints apiService =
                APIClient.getClient().create(GitHubUserEndPoints.class);

        Call<GitHubUser> call = apiService.getUser(newString);
        call.enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                //what to do if response is given back from server

//                Glide.with(getApplicationContext())
//                        .load(response.body().getAvatar())
//                        .centerCrop()
//                        .into(userBinding.avatar);

                ImageDownloader task = new ImageDownloader();
                try {
                    myBitmap = task.execute(response.body().getAvatar()).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userBinding.avatar.setImageBitmap(myBitmap);
                userBinding.avatar.getLayoutParams().height = 220;
                userBinding.avatar.getLayoutParams().width = 220;

                if (response.body().getName() == null) {
                    userBinding.username.setText("No username provided");
                } else {
                    userBinding.username.setText("Username: " + response.body().getName());
                }

                userBinding.followers.setText("Followers: " + response.body().getFollowers());
                userBinding.following.setText("Following: " + response.body().getFollowings());
                userBinding.login.setText("LogIn: " + response.body().getLogin());

                if (response.body().getEmail() == null) {
                    userBinding.email.setText("No email provided");
                } else {
                    userBinding.email.setText("Email: " + response.body().getEmail());
                }

            }
            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {

            }
        });

    }

    public void loadOwnRepos(View view) {
        Intent intent = new Intent(UserActivity.this, Repositories.class);
        intent.putExtra("userName", newString);
        startActivity(intent);
    }
}