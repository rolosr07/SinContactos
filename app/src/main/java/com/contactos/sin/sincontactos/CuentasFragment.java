package com.contactos.sin.sincontactos;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.contactos.sin.sincontactos.common.adapter.LazyAdapter;
import com.contactos.sin.sincontactos.common.entidades.Cuenta;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CuentasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CuentasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CuentasFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView list;
    private LazyAdapter adapterList;

    private FloatingActionButton fab;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private final String METHOD_NAME_OBTENER_CUENTAS = "obtenerCuentas";
    private String webResponseCuentas = "";
    private Thread thread;
    private Handler handler = new Handler();
    private Type collectionTypeCuenta = new TypeToken<List<Cuenta>>(){}.getType();
    private static  MainActivity activity;

    public CuentasFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CuentasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CuentasFragment newInstance(String param1, String param2) {
        CuentasFragment fragment = new CuentasFragment();
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
        View v = inflater.inflate(R.layout.fragment_cuentas, container, false);

        activity = (MainActivity)getActivity();

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setCurrentFragment(1);
                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, new AgregarCuentaFragment(), "AgregarCuenta");
                ft.commit();
            }
        });

        list = (ListView)v.findViewById(R.id.list);

        activity.showProgress(true, "Cargando cuentas...");

        if(activity.isCargarCuentas()){
            loadCuentasList(activity.getCurrentUser().getIdusuario());
        }else{
            loadCuentasLocal();
        }

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    public void loadCuentasList(final int idUsuario){
        thread = new Thread(){
            public void run(){
                try {

                    SoapObject request = new SoapObject(activity.NAMESPACE_CUENTA, METHOD_NAME_OBTENER_CUENTAS);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("idusuario");
                    fromProp.setValue(idUsuario);
                    fromProp.setType(int.class);
                    request.addProperty(fromProp);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_CUENTA);

                    androidHttpTransport.call(activity.SOAP_ACTION_CUENTA, envelope);
                    Object response = envelope.getResponse();
                    webResponseCuentas = response.toString();

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

            if(!webResponseCuentas.equals("") && !webResponseCuentas.equals("[]")){

                List<Object> lcs = activity.jsonToListObject(webResponseCuentas,collectionTypeCuenta);

                SharedPreferences prefs = activity.getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);
                prefs.edit().putString("CUENTAS_APP", webResponseCuentas).apply();
                activity.setCargarCuentas(false);
                if(lcs.size() > 0){

                    adapterList = new LazyAdapter(activity, lcs);
                    list.setAdapter(adapterList);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {

                            activity.setCurrentFragment(1);

                            Fragment fragment = new AgregarCuentaFragment();
                            Bundle bundle = new Bundle();
                            Cuenta cuenta = (Cuenta)adapterList.getItem(position);
                            bundle.putInt("idcuenta", cuenta.getIdcuenta());
                            bundle.putString("nombreCuenta", cuenta.getNombre());
                            bundle.putString("cuenta", cuenta.getCuenta());
                            bundle.putString("contrasena", cuenta.getContrasena());
                            bundle.putString("esInicial", cuenta.getTipo());
                            fragment.setArguments(bundle);

                            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment, fragment, "AgregarCuenta");
                            ft.commit();
                        }
                    });

                    activity.showProgress(false, "");
                }else{

                    activity.showProgress(false, "");
                }
            }else{
                activity.showProgress(false, "");
            }
        }
    };

    private void loadCuentasLocal() {

        if(!activity.getSharedPreferences().getString("CUENTAS_APP","").equals("")){

            List<Object> lcs = activity.jsonToListObject(activity.getSharedPreferences().getString("CUENTAS_APP",""), collectionTypeCuenta);

            if (lcs.size() > 0) {

                adapterList = new LazyAdapter(activity, lcs);
                list.setAdapter(adapterList);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {

                        activity.setCurrentFragment(1);

                        Fragment fragment = new AgregarCuentaFragment();
                        Bundle bundle = new Bundle();
                        Cuenta cuenta = (Cuenta) adapterList.getItem(position);
                        bundle.putInt("idcuenta", cuenta.getIdcuenta());
                        bundle.putString("nombreCuenta", cuenta.getNombre());
                        bundle.putString("cuenta", cuenta.getCuenta());
                        bundle.putString("contrasena", cuenta.getContrasena());
                        bundle.putString("esInicial", cuenta.getTipo());
                        fragment.setArguments(bundle);

                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment, fragment, "AgregarCuenta");
                        ft.commit();
                    }
                });
            }
        }
        activity.showProgress(false, "");
    }
}
