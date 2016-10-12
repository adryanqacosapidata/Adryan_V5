package com.adryan.app.Entidades;

/**
 * Created by vquispe on 19/08/2014.
 */
public class Tareador {
    private String codigo;
    private String compania;
    private String matricula;
    private String nombre;
    private String usuario;
    private String tablet;
    private String idTablet;
    private String centroCosto;
    private String actividad;
    private String labor;
    private String periodo;
    private String nroDocumento;
    private int count;

    public String getCodigoUnico() {
        return codigo;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigo = codigoUnico;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTablet() {
        return tablet;
    }

    public void setTablet(String tablet) {
        this.tablet = tablet;
    }

    public String getIdTablet() {
        return idTablet;
    }

    public void setIdTablet(String idTablet) {
        this.idTablet = idTablet;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getLabor() {
        return labor;
    }

    public void setLabor(String labor) {
        this.labor = labor;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDocumento() {
        return nroDocumento;
    }

    public void setDocumento(String documento) {
        this.nroDocumento = documento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
