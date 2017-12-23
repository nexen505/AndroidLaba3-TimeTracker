package com.komarov.androidlab3.domain;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Objects;

public class Photo implements Serializable {
    private int id;
    private String name;
    private Bitmap image;

    public Photo() {
    }

    public Photo(Bitmap image) {
        this.image = image;
    }

    public Photo(int id, Bitmap image) {
        this.id = id;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo photo = (Photo) o;
        return id == photo.id &&
                Objects.equals(name, photo.name) &&
                Objects.equals(image, photo.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, image);
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image=" + image +
                '}';
    }
}
