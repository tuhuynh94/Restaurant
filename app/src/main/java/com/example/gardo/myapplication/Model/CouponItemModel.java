package com.example.gardo.myapplication.Model;

import java.util.Date;

/**
 * Created by gardo on 11/12/2016.
 */

public class CouponItemModel {
    private final String name;
    private Double value;
    private Date date_start, date_end;
    private final Double point;

    public CouponItemModel(String name, Double point) {
        this.name = name;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public Double getPoint() {
        return point;
    }
}
