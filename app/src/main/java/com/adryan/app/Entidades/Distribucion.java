package com.adryan.app.Entidades;

/**
 * Created by vquispe on 27/08/2014.
 */
public class Distribucion {
    private String codTareador;
    private String centroCosto;
    private String actividad;
    private String labor;
    private String compania;
    private String periodo;
    private String fecha;
    private int count;

    public String getSupervisor() {
        return codTareador;
    }

    public void setSupervisor(String supervisor) {
        this.codTareador = supervisor;
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

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
