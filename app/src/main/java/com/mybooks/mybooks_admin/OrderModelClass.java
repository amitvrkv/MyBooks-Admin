package com.mybooks.mybooks_admin;

/**
 * Created by am361000 on 27/06/17.
 */

public class OrderModelClass {
    String comment;
    String date;
    String deliveryaddress;
    String discount;
    String from;
    String grandtotal;
    String orderid;
    String status;

    public OrderModelClass() {

    }

    public OrderModelClass(String comment, String date, String deliveryaddress, String discount, String from, String grandtotal, String orderid, String status) {
        this.comment = comment;
        this.date = date;
        this.deliveryaddress = deliveryaddress;
        this.discount = discount;
        this.from = from;
        this.grandtotal = grandtotal;
        this.orderid = orderid;
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeliveryaddress() {
        return deliveryaddress;
    }

    public void setDeliveryaddress(String deliveryaddress) {
        this.deliveryaddress = deliveryaddress;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGrandtotal() {
        return grandtotal;
    }

    public void setGrandtotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
