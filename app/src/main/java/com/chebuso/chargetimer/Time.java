package com.chebuso.chargetimer;

import android.support.annotation.NonNull;

import java.util.Objects;

public class Time{
    final int days;
    final int hours;
    final int minutes;

    public Time(int hours, int minutes) {
        this.days = 0;
        this.hours = hours;
        this.minutes = minutes;
    }

    public Time(int days, int hours, int minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return  days == time.days &&
                hours == time.hours &&
                minutes == time.minutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(days, hours, minutes);
    }

    @NonNull
    @Override
    public String toString() {
        return "Time{" +
                "days=" + days +
                ", hours=" + hours +
                ", minutes=" + minutes +
                '}';
    }
}
