package com.adryan.app.Entidades;

/**
 * Created by vquispe on 28/10/2014.
 */
public class Usuario {
    private int id;
    private String usuario;
    private String codigoUnico;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }
}
