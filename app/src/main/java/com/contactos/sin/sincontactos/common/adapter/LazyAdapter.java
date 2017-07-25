package com.contactos.sin.sincontactos.common.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.contactos.sin.sincontactos.R;
import com.contactos.sin.sincontactos.common.entidades.Cuenta;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private List<Object> list;
    private static LayoutInflater inflater=null;


    public LazyAdapter(Activity a, List<Object> lt) {
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
            vi = inflater.inflate(R.layout.list_row, null);

        TextView nombreCuentaTextView = (TextView)vi.findViewById(R.id.nombreCuenta); // title
        TextView cuentaTextView = (TextView)vi.findViewById(R.id.cuenta); // title
        TextView contrasenaTextView = (TextView)vi.findViewById(R.id.pass); // artist name
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        Cuenta cuenta = (Cuenta) list.get(position);

        switch (cuenta.getNombre()) {
            case "Gmail":
                thumb_image.setImageResource(R.mipmap.gmail);
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Cuenta: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Cuenta:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Contraseña: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Contraseña:");
                }
                break;
            case "Facebook":
                thumb_image.setImageResource(R.mipmap.facebook);
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Cuenta: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Cuenta:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Contraseña: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Contraseña:");
                }   ;
                break;
            case "Twitter":
                thumb_image.setImageResource(R.mipmap.twitter);
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Cuenta: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Cuenta:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Contraseña: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Contraseña:");
                }
                break;
            case "Instagram":
                thumb_image.setImageResource(R.mipmap.instagram);
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Cuenta: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Cuenta:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Contraseña: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Contraseña:");
                }
                break;
            case "Sim":
                thumb_image.setImageResource(R.mipmap.sim);
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Pin: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Pin:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Pin: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Puk:");
                }
                break;
            default:
                thumb_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
                nombreCuentaTextView.setText(cuenta.getNombre());
                if(!cuenta.getCuenta().equals("")){
                    cuentaTextView.setText("Cuenta: "+cuenta.getCuenta());
                }else{
                    cuentaTextView.setText("Cuenta:");
                }
                if(!cuenta.getContrasena().equals("")){
                    contrasenaTextView.setText("Contraseña: "+cuenta.getContrasena());
                }else{
                    contrasenaTextView.setText("Contraseña:");
                }
                break;
        }

        return vi;
    }
}
