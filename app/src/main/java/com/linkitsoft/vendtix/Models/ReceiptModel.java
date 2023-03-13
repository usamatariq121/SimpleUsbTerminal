package com.linkitsoft.vendtix.Models;

public class ReceiptModel {
    public String productName,prodCal,quantity,totalPrice,indexNo;

    public ReceiptModel(String productName, String prodCal, String quantity, String totalPrice, String indexNo) {
        this.productName = productName;
        this.prodCal = prodCal;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.indexNo = indexNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProdCal() {
        return prodCal;
    }

    public void setProdCal(String prodCal) {
        this.prodCal = prodCal;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }
}
