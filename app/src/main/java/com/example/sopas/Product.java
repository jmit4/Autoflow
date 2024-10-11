package com.example.sopas;


public class Product {

    private String barcode;
    private String name;
    private String quantity;
    private String price;
    private String fecha;

    // Constructor vacío requerido para Firebase
    public Product() {
    }

    // Constructor con parámetros
    public Product(String barcode, String name, String quantity, String price, String fecha) {
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.fecha = fecha;
    }

    // Getters y setters para cada atributo

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
