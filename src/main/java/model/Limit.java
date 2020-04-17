package model;

import java.sql.Timestamp;

public class Limit {


    protected String name;
    protected long value;
    protected Timestamp date;


    public Limit(String name, long value, Timestamp date) {
        this.name = name;
        this.value = value;
        this.date = date;
    }


    //getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
