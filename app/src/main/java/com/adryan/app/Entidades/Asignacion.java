package com.adryan.app.Entidades;

/**
 * Created by vquispe on 27/08/2014.
 */
public class Asignacion {
    private String tablet;
    private String desTablet;
    private String codTareador;
    private String periodo;
    private String compania;
    private int count;

    public String getMac() {
        return tablet;
    }

    public void setMac(String mac) {
        this.tablet = mac;
    }

    public String getTablet() {
        return desTablet;
    }

    public void setTablet(String tablet) {
        this.desTablet = tablet;
    }

    public String getSupervisor() {
        return codTareador;
    }

    public void setSupervisor(String supervisor) {
        this.codTareador = supervisor;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }
}
