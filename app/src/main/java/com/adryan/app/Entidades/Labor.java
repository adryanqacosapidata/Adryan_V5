package com.adryan.app.Entidades;

/**
 * Created by vquispe on 19/08/2014.
 */
public class Labor {
    private String codigo;
    private String descripcion;
    private String actividad;
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

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getCentrocosto() {
        return centroCosto;
    }

    public void setCentrocosto(String centrocosto) {
        this.centroCosto = centrocosto;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }
}
