package com.eton.pojo;

import java.io.Serializable;

public class User implements Serializable {

    private Integer id;
    private String email;
    private String username;
    private String password;
    private String profile_pic;

    public User() {
    }

    public User(Integer id, String email, String username, String password, String profile_pic) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profile_pic = profile_pic;
    }

    /**
     * 重载有参构造方法，用于验证登录时返回的对象
     * @param email
     * @param password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * 重载有参构造方法，用于用户信息显示
     * @param email
     * @param username
     * @param profile_pic
     */
    public User(String email, String username, String profile_pic) {
        this.email = email;
        this.username = username;
        this.profile_pic = profile_pic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profile_pic='" + profile_pic + '\'' +
                '}';
    }
}
