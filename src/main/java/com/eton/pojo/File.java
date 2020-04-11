package com.eton.pojo;

import java.util.Date;

public class File {

    private Integer user_id;
    private String id;
    private String name;
    private String type;
    private String size;
    private Date create_time;
    private String father_dir;
    private String real_path;
    private Long real_size;

    public File() {
    }

    public File(Integer user_id, String id, String name, String type, String size, Date create_time, String father_dir, String real_path, Long real_size) {
        this.user_id = user_id;
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
        this.create_time = create_time;
        this.father_dir = father_dir;
        this.real_path = real_path;
        this.real_size = real_size;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getReal_path() {
        return real_path;
    }

    public void setReal_path(String real_path) {
        this.real_path = real_path;
    }

    public Long getReal_size() {
        return real_size;
    }

    public void setReal_size(Long real_size) {
        this.real_size = real_size;
    }

    @Override
    public String toString() {
        return "File{" +
                "user_id=" + user_id +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size='" + size + '\'' +
                ", create_time=" + create_time +
                ", father_dir='" + father_dir + '\'' +
                ", real_path='" + real_path + '\'' +
                ", real_size='" + real_size + '\'' +
                '}';
    }
}
