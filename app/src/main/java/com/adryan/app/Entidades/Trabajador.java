package com.adryan.app.Entidades;

/**
 * Created by vquispe on 19/08/2014.
 */
public class Trabajador {
    private String codigoUnico;
    private String matricula;
    private String nombres;
    private String compania;
    private String nroDocumento;
    private String fotocheck;
    private int count;

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombres;
    }

    public void setNombre(String nombre) {
        this.nombres = nombre;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getDocumento() {
        return nroDocumento;
    }

    public void setDocumento(String documento) {
        this.nroDocumento = documento;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFotocheck() {
        return fotocheck;
    }

    public void setFotocheck(String fotocheck) {
        this.fotocheck = fotocheck;
    }
}
