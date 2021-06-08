package com.wayneyong.github_api_retrofit_app.model;

import com.google.gson.annotations.SerializedName;

public class GitHubUser {

    /**
     * @SerializedName -> "An annotation that indicates this member should be
     * serialized to JSON with the provided name value as its field name.
     *
     * API JSON key has to been matched with the name of class declaration(POJO)
     **/


    @SerializedName("login")
    private String login;

    @SerializedName("name")
    private String name;

    @SerializedName("followers")
    private String followers;

    @SerializedName("following")
    private String followings;

    @SerializedName("avatar_url")
    private String avatar;

    @SerializedName("email")
    private String email;

    public GitHubUser(String login, String name, String followers, String followings, String avatar, String email) {
        this.login = login;
        this.name = name;
        this.followers = followers;
        this.followings = followings;
        this.avatar = avatar;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowings() {
        return followings;
    }

    public void setFollowings(String followings) {
        this.followings = followings;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
