package com.example.InAdvance;

public class RecyclerViewRow {
    private String name;
    private String address;
    private String rating;

    public RecyclerViewRow(String name, String address, String rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }


}
