package com.example.pm2e18259.Procesos;

public class Pais {
    private String codigo;
    private String nombrePais;

    public Pais() {
    }

    public Pais(String codigo, String nombrePais) {
        this.codigo = codigo;
        this.nombrePais = nombrePais;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {this.nombrePais = nombrePais;}

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
