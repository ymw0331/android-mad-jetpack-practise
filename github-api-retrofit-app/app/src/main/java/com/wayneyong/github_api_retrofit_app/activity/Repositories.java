package com.wayneyong.github_api_retrofit_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wayneyong.github_api_retrofit_app.R;
import com.wayneyong.github_api_retrofit_app.adapter.ReposAdapter;
import com.wayneyong.github_api_retrofit_app.databinding.ActivityRepositoriesBinding;
import com.wayneyong.github_api_retrofit_app.databinding.ListItemRepoBinding;
import com.wayneyong.github_api_retrofit_app.model.GitHubRepo;
import com.wayneyong.github_api_retrofit_app.rest.APIClient;
import com.wayneyong.github_api_retrofit_app.rest.GitHubRepoEndPoint;
import com.wayneyong.github_api_retrofit_app.rest.GitHubUserEndPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repositories extends AppCompatActivity {

    private ActivityRepositoriesBinding repoBinding;
    private ListItemRepoBinding itemBinding;
    Bundle extra;


    String receivedName;

    List<GitHubRepo> myDataSource = new ArrayList<>();
    RecyclerView.Adapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_repositories);

        repoBinding = ActivityRepositoriesBinding.inflate(getLayoutInflater());
        View view = repoBinding.getRoot();
        setContentView(view);

        extra = getIntent().getExtras();
        receivedName = extra.getString("userName");
        repoBinding.userNameTV.setText("User: " + receivedName);

        repoBinding.reposRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new ReposAdapter(myDataSource, R.layout.list_item_repo, getApplicationContext());

        repoBinding.reposRecyclerView.setAdapter(myAdapter);

        loadRepositories();

    }

    private void loadRepositories() {

        GitHubRepoEndPoint apiService =
                APIClient.getClient().create(GitHubRepoEndPoint.class);

        Call<List<GitHubRepo>> call = apiService.getRepo(receivedName);
        call.enqueue(new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {

                myDataSource.clear();
                myDataSource.addAll(response.body());
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}