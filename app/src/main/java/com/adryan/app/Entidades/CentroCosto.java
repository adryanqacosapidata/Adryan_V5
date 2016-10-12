package com.adryan.app.Entidades;

/**
 * Created by vquispe on 19/08/2014.
 */
public class CentroCosto {
    private String codigo;
    private String descripcion;
    private String compania;

    public String getId() {
        return codigo;
    }

    public void setId(String id) {
        this.codigo = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }
}
