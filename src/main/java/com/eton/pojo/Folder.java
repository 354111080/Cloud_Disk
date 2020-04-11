package com.eton.pojo;

import java.util.Date;

public class Folder {

    private Integer user_id;
    private String id;
    private String name;
    private Date create_time;
    private String father_dir;

    public Folder() {
    }

    public Folder(Integer user_id, String id, String name, Date create_time, String father_dir) {
        this.user_id = user_id;
        this.id = id;
        this.name = name;
        this.create_time = create_time;
        this.father_dir = father_dir;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getFather_dir() {
        return father_dir;
    }

    public void setFather_dir(String father_dir) {
        this.father_dir = father_dir;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "user_id=" + user_id +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", create_time=" + create_time +
                ", father_dir='" + father_dir + '\'' +
                '}';
    }
}
