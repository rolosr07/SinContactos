package com.contactos.sin.sincontactos.common.entidades;

/**
 * Created by Rolando on 20/07/2017.
 */

public class Cuenta {

    private int idcuenta;
    private int idusuario;
    private String nombre;
    private String tipo;
    private String cuenta;
    private String contrasena;
    private String fecha_creacion;
    private String fecha_ult_modificacion;

    public int getIdcuenta() {
        return idcuenta;
    }

    public void setIdcuenta(int idcuenta) {
        this.idcuenta = idcuenta;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getFecha_ult_modificacion() {
        return fecha_ult_modificacion;
    }

    public void setFecha_ult_modificacion(String fecha_ult_modificacion) {
        this.fecha_ult_modificacion = fecha_ult_modificacion;
    }
}
