package com.contactos.sin.sincontactos.common.entidades;

/**
 * Created by Rolando on 26/07/2017.
 */

public class Notificacion {
    private int idnotificacion;
    private int idtienda;
    private int idmensaje;
    private int idusuario;
    private String titulo;
    private String texto;
    private boolean enviado;

    public int getIdnotificacion() {
        return idnotificacion;
    }

    public void setIdnotificacion(int idnotificacion) {
        this.idnotificacion = idnotificacion;
    }

    public int getIdtienda() {
        return idtienda;
    }

    public void setIdtienda(int idtienda) {
        this.idtienda = idtienda;
    }

    public int getIdmensaje() {
        return idmensaje;
    }

    public void setIdmensaje(int idmensaje) {
        this.idmensaje = idmensaje;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
}
