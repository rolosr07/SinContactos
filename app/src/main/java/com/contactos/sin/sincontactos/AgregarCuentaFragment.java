package com.contactos.sin.sincontactos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgregarCuentaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AgregarCuentaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarCuentaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button buttonRegistrar;
    private Button buttonBorrar;

    private OnFragmentInteractionListener mListener;

    private EditText nombreCuenta;
    private EditText cuenta;
    private EditText contrasena;
    private TextView titulo;

    private String webResponse = "";

    private Thread thread;
    private Handler handler = new Handler();

    private final String METHOD_NAME = "registrarCuenta";
    private final String METHOD_NAME_ACTUALIZAR = "actualizarCuenta";
    private final String METHOD_NAME_BORRAR = "borrarCuenta";

    private MainActivity activity;

    private boolean esActualizacion = false;

    private int idCuenta;
    private String nombreCuentat;
    private String cuentat;
    private String contrasenat;
    private String esInicial;

    public AgregarCuentaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarCuentaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarCuentaFragment newInstance(String param1, String param2) {
        AgregarCuentaFragment fragment = new AgregarCuentaFragment();
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

        View v = inflater.inflate(R.layout.fragment_agregar_cuenta, container, false);

        activity = (MainActivity) getActivity();

        nombreCuenta = (EditText) v.findViewById(R.id.nombreCuenta);
        cuenta = (EditText) v.findViewById(R.id.cuenta);
        contrasena = (EditText) v.findViewById(R.id.contrasena);

        buttonBorrar = (Button) v.findViewById(R.id.btnBorrar);
        buttonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cuenta.getWindowToken(), 0);

                activity.setCurrentFragment(0);
                activity.showProgress(true, "Borrando cuenta...");
                borrarCuenta(idCuenta);
            }
        });

        buttonRegistrar = (Button) v.findViewById(R.id.btnRegistrar);
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(cuenta.getWindowToken(), 0);
                if(esActualizacion){
                    activity.setCurrentFragment(0);
                    activity.showProgress(true, "Actualizando cuenta...");
                    actualizarCuenta();
                }else{
                    activity.setCurrentFragment(0);
                    activity.showProgress(true, "Creando cuenta...");
                    registrarCuenta();
                }
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idCuenta = bundle.getInt("idcuenta", 0);
            nombreCuentat = bundle.getString("nombreCuenta", "");
            cuentat = bundle.getString("cuenta", "");
            contrasenat = bundle.getString("contrasena", "");
            esInicial = bundle.getString("esInicial", "");

            if(!nombreCuenta.equals("")){
                titulo = (TextView) v.findViewById(R.id.tituloAgregarCuenta);
                titulo.setText(nombreCuentat);
                cuenta.setText(cuentat);
                contrasena.setText(contrasenat);

                if(!esInicial.equals("cuenta_inicial")){
                    nombreCuenta.setText(nombreCuentat);
                    buttonBorrar.setVisibility(View.VISIBLE);
                }else{
                    nombreCuenta.setVisibility(View.GONE);
                }

                esActualizacion = true;
                buttonRegistrar.setText("Actualizar Datos");
            }
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionAgregarCuenta(uri);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionAgregarCuenta(Uri uri);
    }

    public void registrarCuenta() {

        // Reset errors.

        nombreCuenta.setError(null);
        cuenta.setError(null);
        contrasena.setError(null);

        // Store values at the time of the login attempt.
        String nombreCuentat = nombreCuenta.getText().toString();
        String cuentat = cuenta.getText().toString();
        String contrasenat = contrasena.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nombreCuentat)) {
            nombreCuenta.setError(getString(R.string.error_field_required));
            focusView = nombreCuenta;
            cancel = true;
        }

        if (TextUtils.isEmpty(cuentat)) {
            cuenta.setError(getString(R.string.error_field_required));
            focusView = cuenta;
            cancel = true;
        }

        if (TextUtils.isEmpty(contrasenat)) {
            contrasena.setError(getString(R.string.error_field_required));
            focusView = contrasena;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            activity.showProgress(false,"");
        } else {

            registrar(activity.getCurrentUser().getIdusuario(), nombreCuentat, cuentat, contrasenat);
        }
    }

    public void actualizarCuenta() {

        // Reset errors.

        nombreCuenta.setError(null);
        cuenta.setError(null);
        contrasena.setError(null);

        // Store values at the time of the login attempt.
        String nombreCuentaActualizar = nombreCuenta.getText().toString();
        String cuentaActualizar = cuenta.getText().toString();
        String contrasenaActualizar= contrasena.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!esInicial.equals("cuenta_inicial")){
            if (TextUtils.isEmpty(nombreCuentaActualizar)) {
                nombreCuenta.setError(getString(R.string.error_field_required));
                focusView = nombreCuenta;
                cancel = true;
            }
        }else{
            nombreCuentaActualizar = nombreCuentat;
        }

        if (TextUtils.isEmpty(cuentaActualizar)) {
            cuenta.setError(getString(R.string.error_field_required));
            focusView = cuenta;
            cancel = true;
        }

        if (TextUtils.isEmpty(contrasenaActualizar)) {
            contrasena.setError(getString(R.string.error_field_required));
            focusView = contrasena;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            activity.showProgress(false,"");
        } else {

            actualizar(idCuenta, nombreCuentaActualizar, cuentaActualizar, contrasenaActualizar);
        }
    }

    public void registrar(final int idUsuario, final String nombreCuenta, final String cuenta, final String contrasena){
        thread = new Thread(){
            public void run(){
                try {
                    SoapObject request = new SoapObject(activity.NAMESPACE_CUENTA, METHOD_NAME);

                    PropertyInfo fromProp0 = new PropertyInfo();
                    fromProp0.setName("idusuario");
                    fromProp0.setValue(idUsuario);
                    fromProp0.setType(int.class);
                    request.addProperty(fromProp0);

                    PropertyInfo fromProp1 = new PropertyInfo();
                    fromProp1.setName("nombre");
                    fromProp1.setValue(nombreCuenta);
                    fromProp1.setType(String.class);
                    request.addProperty(fromProp1);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("cuenta");
                    fromProp.setValue(cuenta);
                    fromProp.setType(String.class);
                    request.addProperty(fromProp);

                    PropertyInfo fromProp2 = new PropertyInfo();
                    fromProp2.setName("contrasena");
                    fromProp2.setValue(contrasena);
                    fromProp2.setType(String.class);
                    request.addProperty(fromProp2);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CUENTA);

                    androidHttpTransport.call(activity.SOAP_ACTION_CUENTA, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }

                handler.post(createUI);
            }
        };

        thread.start();
    }

    final Runnable createUI = new Runnable() {

        public void run(){

            boolean result = Boolean.valueOf(webResponse);
            if(result){
                activity.setCargarCuentas(true);
                Toast.makeText(activity, "Cuenta registrada con exito!", Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new CuentasFragment(), "Cuentas");
                ft.commit();

            }else{
                Toast.makeText(activity, getString(R.string.error_server), Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
            }
        }
    };

    public void actualizar(final int idCuenta,final String nombreCuenta, final String cuenta, final String contrasena){
        thread = new Thread(){
            public void run(){
                try {
                    SoapObject request = new SoapObject(activity.NAMESPACE_CUENTA, METHOD_NAME_ACTUALIZAR);

                    PropertyInfo fromProp0 = new PropertyInfo();
                    fromProp0.setName("idcuenta");
                    fromProp0.setValue(idCuenta);
                    fromProp0.setType(int.class);
                    request.addProperty(fromProp0);

                    PropertyInfo fromProp1 = new PropertyInfo();
                    fromProp1.setName("nombre");
                    fromProp1.setValue(nombreCuenta);
                    fromProp1.setType(String.class);
                    request.addProperty(fromProp1);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("cuenta");
                    fromProp.setValue(cuenta);
                    fromProp.setType(String.class);
                    request.addProperty(fromProp);

                    PropertyInfo fromProp2 = new PropertyInfo();
                    fromProp2.setName("contrasena");
                    fromProp2.setValue(contrasena);
                    fromProp2.setType(String.class);
                    request.addProperty(fromProp2);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CUENTA);

                    androidHttpTransport.call(activity.SOAP_ACTION_CUENTA, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }

                handler.post(createUIActualizar);
            }
        };

        thread.start();
    }

    final Runnable createUIActualizar = new Runnable() {

        public void run(){

            boolean result = Boolean.valueOf(webResponse);
            if(result){
                activity.setCargarCuentas(true);
                Toast.makeText(activity, "Cuenta actualizada con exito!", Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new CuentasFragment(), "Cuentas");
                ft.commit();

            }else{
                Toast.makeText(activity, getString(R.string.error_server), Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
            }
        }
    };

    public void borrarCuenta(final int idCuenta){
        thread = new Thread(){
            public void run(){
                try {
                    SoapObject request = new SoapObject(activity.NAMESPACE_CUENTA, METHOD_NAME_BORRAR);

                    PropertyInfo fromProp0 = new PropertyInfo();
                    fromProp0.setName("idcuenta");
                    fromProp0.setValue(idCuenta);
                    fromProp0.setType(int.class);
                    request.addProperty(fromProp0);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CUENTA);

                    androidHttpTransport.call(activity.SOAP_ACTION_CUENTA, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }

                handler.post(createUIBorrar);
            }
        };

        thread.start();
    }

    final Runnable createUIBorrar = new Runnable() {

        public void run(){

            boolean result = Boolean.valueOf(webResponse);
            if(result){
                activity.setCargarCuentas(true);
                Toast.makeText(activity, "Cuenta borrada con exito!", Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new CuentasFragment(), "Cuentas");
                ft.commit();

            }else{
                Toast.makeText(activity, getString(R.string.error_server), Toast.LENGTH_LONG).show();
                activity.showProgress(false,"");
            }
        }
    };
}
