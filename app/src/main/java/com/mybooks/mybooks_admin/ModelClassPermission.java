package com.mybooks.mybooks_admin;

/**
 * Created by am361000 on 28/08/17.
 */

public class ModelClassPermission {
    String addExe;

    String addBook;
    String updateBook;

    String manageOrder;
    String orderInProcess;
    String outForDelivery;
    String delivered;
    String deleteOrder;
    String orderRollBack;

    public ModelClassPermission() {
    }

    public ModelClassPermission(String addExe, String addBook, String updateBook, String manageOrder, String orderInProcess, String outForDelivery, String delivered, String deleteOrder, String orderRollBack) {
        this.addExe = addExe;
        this.addBook = addBook;
        this.updateBook = updateBook;
        this.manageOrder = manageOrder;
        this.orderInProcess = orderInProcess;
        this.outForDelivery = outForDelivery;
        this.delivered = delivered;
        this.deleteOrder = deleteOrder;
        this.orderRollBack = orderRollBack;
    }

    public String getAddBook() {
        return addBook;
    }

    public void setAddBook(String addBook) {
        this.addBook = addBook;
    }

    public String getUpdateBook() {
        return updateBook;
    }

    public void setUpdateBook(String updateBook) {
        this.updateBook = updateBook;
    }

    public String getManageOrder() {
        return manageOrder;
    }

    public String getAddExe() {
        return addExe;
    }

    public void setAddExe(String addExe) {
        this.addExe = addExe;
    }

    public void setManageOrder(String manageOrder) {
        this.manageOrder = manageOrder;
    }

    public String getOrderInProcess() {
        return orderInProcess;
    }

    public void setOrderInProcess(String orderInProcess) {
        this.orderInProcess = orderInProcess;
    }

    public String getOutForDelivery() {
        return outForDelivery;
    }

    public void setOutForDelivery(String outForDelivery) {
        this.outForDelivery = outForDelivery;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getDeleteOrder() {
        return deleteOrder;
    }

    public void setDeleteOrder(String deleteOrder) {
        this.deleteOrder = deleteOrder;
    }

    public String getOrderRollBack() {
        return orderRollBack;
    }

    public void setOrderRollBack(String orderRollBack) {
        this.orderRollBack = orderRollBack;
    }
}
