package com.example.InAdvance;

public class Order_RecyclerViewRow {

    private String orderList, orderTime,priceTotal, restaurantAddress, restaurantName,orderID;
    private String name, imageUrl;
//    Timestamp orderTime;
    public Order_RecyclerViewRow(String orderList, String orderID, String orderTime, String priceTotal, String restaurantName,String restaurantAddress){
        this.orderList = orderList;
        this.orderID = orderID;
        this.orderTime = orderTime;
        this.priceTotal = priceTotal;
        this.restaurantAddress = restaurantAddress;
        this.restaurantName = restaurantName;
    }
    public Order_RecyclerViewRow(){}
    public Order_RecyclerViewRow(String name, String imageUrl){
        this.name = name;
        this.imageUrl = imageUrl;
    }


    public void setOrderList(String orderList) {
        this.orderList = orderList;
    }
//    public void setOrderTime(Timestamp orderTime){this.orderTime = orderTime;}
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }
    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public void setOrderID(String orderID) {
            this.orderID = orderID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getOrderList(){
        return orderList;
    }
    public String getOrderTime() {
        return orderTime;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
    public String getOrderID() {
        return orderID;
    }

    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
