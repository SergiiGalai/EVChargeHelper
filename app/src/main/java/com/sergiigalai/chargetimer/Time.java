package com.sergiigalai.chargetimer;

import java.util.Objects;

public class Time{
    public Time(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public Time(int days, int hours, int minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    int days;
    int hours;
    int minutes;

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

    @Override
    public String toString() {
        return "Time{" +
                "days=" + days +
                ", hours=" + hours +
                ", minutes=" + minutes +
                '}';
    }
}
