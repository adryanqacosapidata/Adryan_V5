package com.adryan.app.Entidades;

/**
 * Created by vquispe on 19/08/2014.
 */
public class Actividad {
    private String codigo;
    private String descripcion;
    private String centroCosto;
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

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }
}
