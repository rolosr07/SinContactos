package com.contactos.sin.sincontactos.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.contactos.sin.sincontactos.common.entidades.Contacto;
import com.contactos.sin.sincontactos.common.entidades.Tienda;
import com.contactos.sin.sincontactos.common.entidades.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Rolando on 17/07/2017.
 */

public class Base extends AppCompatActivity {

    public final int ID_TIENDA = 2;

    //private String IP = "e-propiedadescr.com";
    private String IP = "192.168.43.195";
    public final String NAMESPACE_USER = "http://"+IP+"/webServicesSinContactos/servicio-clientes.php";
    public final String URL_USER = "http://"+IP+"/webServicesSinContactos/servicio-clientes.php?wsdl";
    public final String SOAP_ACTION_USER = "http://"+IP+"/webServicesSinContactos/servicio-clientes.php?wsdl";

    public final String NAMESPACE_TIENDA = "http://"+IP+"/webServicesSinContactos/servicio-tiendas.php";
    public final String URL_TIENDA = "http://"+IP+"/webServicesSinContactos/servicio-tiendas.php?wsdl";
    public final String SOAP_ACTION_TIENDA = "http://"+IP+"/webServicesSinContactos/servicio-tiendas.php?wsdl";

    public final String NAMESPACE_CUENTA = "http://"+IP+"/webServicesSinContactos/servicio-cuentas.php";
    public final String URL_CUENTA = "http://"+IP+"/webServicesSinContactos/servicio-cuentas.php?wsdl";
    public final String SOAP_ACTION_CUENTA = "http://"+IP+"/webServicesSinContactos/servicio-cuentas.php?wsdl";

    public final String NAMESPACE_CONTACTO = "http://"+IP+"/webServicesSinContactos/servicio-contactos.php";
    public final String URL_CONTACTO= "http://"+IP+"/webServicesSinContactos/servicio-contactos.php?wsdl";
    public final String SOAP_ACTION_CONTACTO = "http://"+IP+"/webServicesSinContactos/servicio-contactos.php?wsdl";

    public final String NAMESPACE_MENSAJE = "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php";
    public final String URL_MENSAJE = "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php?wsdl";
    public final String SOAP_ACTION_MENSAJE = "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php?wsdl";

    private static Usuario user = null;
    private static Tienda tienda = null;

    public Usuario getCurrentUser(){

        if(getUser() == null){
            SharedPreferences prefs = getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);

            if(!prefs.getString("USER_DATA","").equals(""))
            {
                Type collectionType = new TypeToken<List<Usuario>>(){}.getType();
                List<Usuario> usuarios = new Gson().fromJson( prefs.getString("USER_DATA","") , collectionType);

                setUser(usuarios.get(0));
            }
        }

        return getUser();
    }

    private static Usuario getUser() {
        return user;
    }

    private static Tienda getTienda() {
        return tienda;
    }

    public static void setUser(Usuario user) {
        Base.user = user;
    }

    public static void setTienda(Tienda tienda) {
        Base.tienda = tienda;
    }


    public SharedPreferences getSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);

        return prefs;
    }

    public List<Object> jsonToListObject(String json, Type collectionType){
        List<Object> list = new Gson().fromJson(json, collectionType);

        return list;
    }

    public Tienda getCurrentTienda(){

        if(getTienda() == null){
            SharedPreferences prefs = getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);

            if(!prefs.getString("TIENDA_APP","").equals(""))
            {
                Type collectionType = new TypeToken<List<Tienda>>(){}.getType();
                List<Tienda> tienda = new Gson().fromJson( prefs.getString("TIENDA_APP","") , collectionType);

                setTienda(tienda.get(0));
            }
        }

        return getTienda();
    }
}
