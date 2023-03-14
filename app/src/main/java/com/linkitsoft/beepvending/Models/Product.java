package com.linkitsoft.beepvending.Models;

public class Product {

    public String image;
    public int qty;
    public Boolean isselected;
    public int position;
    public String itemname;
    public double price;
    public  int prodImg;
    public int isAdded;


    public Product() {
    }

    public Product(String image, int qty, Boolean isselected, int position, String itemname, double price , int prodImg) {
        this.image = image;
        this.qty = qty;
        this.isselected = isselected;
        this.position = position;
        this.itemname = itemname;
        this.price = price;
        this.prodImg = prodImg;
    }



    public Product(double price, String itemname, int image, int prodQuantityCount, int position) {
        this.prodImg = image;
        this.price = price;
        this.itemname = itemname;
        this.qty = prodQuantityCount;
        this.position = position;
    }

    public int getProdImg() {
        return prodImg;
    }

    public void setProdImg(int prodImg) {
        this.prodImg = prodImg;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Boolean getIsselected() {
        return isselected;
    }

    public void setIsselected(Boolean isselected) {
        this.isselected = isselected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
