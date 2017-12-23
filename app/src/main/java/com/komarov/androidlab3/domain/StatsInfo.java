package com.komarov.androidlab3.domain;

import java.util.Objects;

public class StatsInfo
{
    private String category;
    private long time;

    public StatsInfo(String category, long time) {
        this.category = category;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsInfo)) return false;
        StatsInfo pieData = (StatsInfo) o;
        return time == pieData.time &&
                Objects.equals(category, pieData.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, time);
    }

    @Override
    public String toString() {
        return "StatsInfo{" +
                "category='" + category + '\'' +
                ", time=" + time +
                '}';
    }
}
