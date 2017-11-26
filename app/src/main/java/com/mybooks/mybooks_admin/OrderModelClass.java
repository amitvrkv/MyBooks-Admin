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
    String payable_amount;
    String orderid;
    String status;
    String deliverycharge;
    String paymentmode;
    String total;

    public OrderModelClass() {

    }

    public OrderModelClass(String comment, String date, String deliveryaddress, String discount, String from, String payable_amount, String orderid, String status, String deliverycharge, String paymentmode, String total) {
        this.comment = comment;
        this.date = date;
        this.deliveryaddress = deliveryaddress;
        this.discount = discount;
        this.from = from;
        this.payable_amount = payable_amount;
        this.orderid = orderid;
        this.status = status;
        this.deliverycharge = deliverycharge;
        this.paymentmode = paymentmode;
        this.total = total;
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

    public String getPayable_amount() {
        return payable_amount;
    }

    public void setPayable_amount(String payable_amount) {
        this.payable_amount = payable_amount;
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

    public String getDeliverycharge() {
        return deliverycharge;
    }

    public void setDeliverycharge(String deliverycharge) {
        this.deliverycharge = deliverycharge;
    }

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
