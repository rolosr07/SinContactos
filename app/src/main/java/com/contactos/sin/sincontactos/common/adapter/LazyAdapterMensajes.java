package com.contactos.sin.sincontactos.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.contactos.sin.sincontactos.R;
import com.contactos.sin.sincontactos.common.entidades.Cuenta;
import com.contactos.sin.sincontactos.common.entidades.Mensaje;

import java.util.List;

public class LazyAdapterMensajes extends BaseAdapter {

    private Activity activity;
    private List<Object> list;
    private static LayoutInflater inflater=null;


    public LazyAdapterMensajes(Activity a, List<Object> lt) {
        this.activity = a;
        this.list = lt;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row_mensajes, null);

        TextView texto = (TextView)vi.findViewById(R.id.texto); // title

        Mensaje mensaje = (Mensaje) list.get(position);

        texto.setText(mensaje.getTexto());

        return vi;
    }
}
