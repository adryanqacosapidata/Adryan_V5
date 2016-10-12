package com.adryan.app.Entidades;

/**
 * Created by vquispe on 27/08/2014.
 */
public class Compania {
    private String idCia;
    private String desCia;
    private int count;

    public String getId() {
        return idCia;
    }

    public void setId(String id) {
        this.idCia = id;
    }

    public String getDescripcion() {
        return desCia;
    }

    public void setDescripcion(String descripcion) {
        this.desCia = descripcion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
