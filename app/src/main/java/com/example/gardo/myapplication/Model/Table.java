package com.example.gardo.myapplication.Model;

/**
 * Created by gardo on 16/11/2016.
 */

public class Table {
    private boolean status;
    private String table_name;

    public Table(String table_name, boolean status) {
        this.table_name = table_name;
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
}
