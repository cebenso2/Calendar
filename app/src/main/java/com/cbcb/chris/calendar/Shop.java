package com.cbcb.chris.calendar;

/**
 * Created by chris on 1/7/17.
 */

public class Shop {
    private int id;
    private String name;
    private String address;
    public Shop()
    {
    }
    public Shop(int id,String name,String address)
    {
        this.id=id;
        this.name=name;
        this.address=address;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public int getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
    public String getName() {
        return name;
    }
}