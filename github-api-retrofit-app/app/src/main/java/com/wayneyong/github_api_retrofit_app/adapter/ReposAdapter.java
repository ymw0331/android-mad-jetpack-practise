package com.wayneyong.github_api_retrofit_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wayneyong.github_api_retrofit_app.R;
import com.wayneyong.github_api_retrofit_app.model.GitHubRepo;

import java.util.List;

//parameter as invoking viewHolder after extends recyclerView
public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.ReposViewHolder> {

    //build the basic structure of the adapter
    private List<GitHubRepo> repos; //list of repository
    private int rowLayout;
    private Context context;

    //OOP structure, once fields defined, we need to define constructor and getter and setter
    public ReposAdapter(List<GitHubRepo> repos, int rowLayout, Context context) {
        this.repos = repos;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public List<GitHubRepo> getRepos() {
        return repos;
    }

    public void setRepos(List<GitHubRepo> repos) {
        this.repos = repos;
    }

    public int getRowLayout() {
        return rowLayout;
    }

    public void setRowLayout(int rowLayout) {
        this.rowLayout = rowLayout;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //what exactly need to be taken from where, view need to be create from the current context, then inflate ht layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(rowLayout, parent, false);
        return new ReposViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReposViewHolder holder, int position) {

        holder.repoName.setText(repos.get(position).getName());
        holder.repoDescription.setText(repos.get(position).getDescription());
        holder.repoLanguage.setText(repos.get(position).getLanguage());
    }

    @Override
    public int getItemCount() {
        return repos.size(); //prevent list of repos from growing
    }


    //create structure that is going to find the xml in list_item_repo
    public static class ReposViewHolder extends RecyclerView.ViewHolder {
        //match the component of the repo item list, reference when search for widget
        LinearLayout reposLayout;
        TextView repoName;
        TextView repoDescription;
        TextView repoLanguage;

        public ReposViewHolder(@NonNull View itemView) {
            super(itemView); //viewHolder of recyclerView
            reposLayout = (LinearLayout) itemView.findViewById(R.id.repo_item_layout);
            repoName = (TextView) itemView.findViewById(R.id.repoName);
            repoDescription = (TextView) itemView.findViewById(R.id.repoDescription);
            repoLanguage = (TextView) itemView.findViewById(R.id.repoLanguage);
        }
    }
}
