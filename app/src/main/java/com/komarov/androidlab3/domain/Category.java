package com.komarov.androidlab3.domain;

import java.io.Serializable;
import java.util.Objects;

public class Category implements Serializable
{
    private Integer id;
    private String title;
    private Photo photo;

    public Category() {

    }

    public Category(Integer id, String title, Photo photo) {
        this.id = id;
        this.title = title;
        this.photo = photo;
    }

    public Category(String title, Photo photo) {
        this(null, title, photo);
    }

    public Category(String title) {
        this(null, title, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(title, category.title) &&
                Objects.equals(photo, category.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, photo);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
