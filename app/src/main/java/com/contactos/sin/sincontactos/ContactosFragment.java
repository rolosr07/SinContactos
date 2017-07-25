package com.contactos.sin.sincontactos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.contactos.sin.sincontactos.common.entidades.Contacto;
import com.contactos.sin.sincontactos.common.entidades.Historial;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import android.provider.ContactsContract.Data;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String METHOD_NAME_OBTENER_HISTORIAL = "obtenerUltimoHistorial";
    private final String METHOD_NAME_OBTENER_CONTACTOS = "obtenerContactos";
    private String webResponse = "";
    private String webResponseObtenerContactos = "";
    private Thread thread;
    private Handler handler = new Handler();
    private Type collectionTypeHistorial = new TypeToken<List<Historial>>(){}.getType();
    private Type collectionTypeContactos= new TypeToken<List<Contacto>>(){}.getType();
    private static  MainActivity activity;
    private TextView titulo;
    private TextView telefono;
    private TextView cantidad;

    NotificationManager manager;
    Notification myNotication;

    public ContactosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactosFragment newInstance(String param1, String param2) {
        ContactosFragment fragment = new ContactosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contactos, container, false);
        activity = (MainActivity)getActivity();

        activity.showProgress(true,"Cargando información de contactos...");

        titulo = (TextView)v.findViewById(R.id.titulo);
        telefono = (TextView)v.findViewById(R.id.telefono);
        cantidad = (TextView)v.findViewById(R.id.cantidad);

        Button restaurar = (Button)v.findViewById(R.id.btnRestaurar);
        restaurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showProgress(true,"Restaurando contactos...");
                obtenerContactos(activity.getCurrentUser().getIdusuario());
            }
        });

        loadHistorial(activity.getCurrentUser().getIdusuario());

        return v;
    }

    private void addContact(Contacto contacto) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "")
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contacto.getNombre())
                .build());

        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contacto.getTelefono())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());
        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)

                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try{
            ContentProviderResult[] results = activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionContacto(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener{
        // TODO: Update argument type and name
        void onFragmentInteractionContacto (Uri uri);
    }

    public void loadHistorial(final int idUsuario){
        thread = new Thread(){
            public void run(){
                try {

                    SoapObject request = new SoapObject(activity.NAMESPACE_CONTACTO, METHOD_NAME_OBTENER_HISTORIAL);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("idusuario");
                    fromProp.setValue(idUsuario);
                    fromProp.setType(int.class);
                    request.addProperty(fromProp);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CONTACTO);

                    androidHttpTransport.call(activity.SOAP_ACTION_CONTACTO, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }
                handler.post(createUICuentas);
            }
        };

        thread.start();
    }

    final Runnable createUICuentas = new Runnable() {

        public void run(){

            if(!webResponse.equals("") && !webResponse.equals("[]")){

                List<Object> lcs = activity.jsonToListObject(webResponse,collectionTypeHistorial);

                Historial historial = (Historial)lcs.get(0);

                telefono.setText("Telefono: "+activity.getCurrentUser().getTelefono());
                titulo.setText("Fecha de la ultima copia de seguridad de sus contactos: "+historial.getFecha());
                cantidad.setText("Cantidad de contactos almacenados: "+ historial.getCantidad_contactos());

                activity.showProgress(false, "");

            }else{
                activity.showProgress(false, "");
            }
        }
    };

    public void obtenerContactos(final int idUsuario){
        thread = new Thread(){
            public void run(){
                try {

                    SoapObject request = new SoapObject(activity.NAMESPACE_CONTACTO, METHOD_NAME_OBTENER_CONTACTOS);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("idusuario");
                    fromProp.setValue(idUsuario);
                    fromProp.setType(int.class);
                    request.addProperty(fromProp);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CONTACTO);

                    androidHttpTransport.call(activity.SOAP_ACTION_CONTACTO, envelope);
                    Object response = envelope.getResponse();
                    webResponseObtenerContactos = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }
                handler.post(createUIObtenerContactos);
            }
        };

        thread.start();
    }

    final Runnable createUIObtenerContactos = new Runnable() {

        public void run(){

            if(!webResponseObtenerContactos.equals("") && !webResponseObtenerContactos.equals("[]")){

                List<Object> lcs = activity.jsonToListObject(webResponseObtenerContactos,collectionTypeContactos);

                for(Object contactoObject : lcs){
                    Contacto contacto = (Contacto) contactoObject;
                    addContact(contacto);
                }

                Toast.makeText(activity, "Contactos restaurados con exito!", Toast.LENGTH_LONG).show();

                activity.showProgress(false, "");

            }else{
                activity.showProgress(false, "");
            }
        }
    };

}
