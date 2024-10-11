package com.example.sopas;

public class empleado {
    String uid, nombre;

    public empleado() {

    }

    public empleado(String nombre, String uid) {
        this.nombre = nombre;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
