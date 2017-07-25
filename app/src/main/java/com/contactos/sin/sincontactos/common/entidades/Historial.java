package com.contactos.sin.sincontactos.common.entidades;

/**
 * Created by Rolando on 24/07/2017.
 */

public class Historial {

    private int idhistorial_respaldo;
    private int idusuario;
    private String fecha;
    private int cantidad_contactos;

    public int getIdhistorial_respaldo() {
        return idhistorial_respaldo;
    }

    public void setIdhistorial_respaldo(int idhistorial_respaldo) {
        this.idhistorial_respaldo = idhistorial_respaldo;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCantidad_contactos() {
        return cantidad_contactos;
    }

    public void setCantidad_contactos(int cantidad_contactos) {
        this.cantidad_contactos = cantidad_contactos;
    }
}
