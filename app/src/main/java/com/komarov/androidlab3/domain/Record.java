package com.komarov.androidlab3.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Record implements Serializable {
    private Integer id;
    private String desc;
    private long interval;
    private long begin;
    private long end;
    private Integer categoryRef;
    private List<Photo> photos;
    private List<String> photosId;
    private String categoryTitle;

    public Record() {
    }

    public Record(String desc, long interval, long begin, long end, Integer categoryRef) {
        this(desc, interval, begin, end, categoryRef, null);
    }

    public Record(String desc, long interval, long begin, long end, Integer category, String categoryTitle) {
        this(desc, interval, begin, end, category, categoryTitle, null);
    }

    public Record(String desc) {
        this.desc = desc;
    }

    public Record(List<Photo> photos, String desc, long interval, long begin, long end, Integer category) {
        this(desc, interval, begin, end, category, "", photos);
    }

    public Record(String desc, long interval, long begin, long end, Integer category, String categoryTitle, List<Photo> photos) {
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.categoryRef = category;
        this.photos = photos;
        this.categoryTitle = categoryTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Integer getCategoryRef() {
        return categoryRef;
    }

    public void setCategoryRef(Integer categoryRef) {
        this.categoryRef = categoryRef;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<String> getPhotosId() {
        return photosId;
    }

    public void setPhotosId(List<String> photosId) {
        this.photosId = photosId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return Objects.equals(id, record.id) &&
                interval == record.interval &&
                begin == record.begin &&
                end == record.end &&
                Objects.equals(categoryRef, record.categoryRef) &&
                Objects.equals(desc, record.desc) &&
                Objects.equals(photos, record.photos) &&
                Objects.equals(photosId, record.photosId) &&
                Objects.equals(categoryTitle, record.categoryTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, desc, interval, begin, end, categoryRef, photos, photosId, categoryTitle);
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                ", interval=" + interval +
                ", begin=" + begin +
                ", end=" + end +
                ", categoryRef=" + categoryRef +
                ", photos=" + photos +
                ", photosId=" + photosId +
                ", categoryTitle='" + categoryTitle + '\'' +
                '}';
    }
}
