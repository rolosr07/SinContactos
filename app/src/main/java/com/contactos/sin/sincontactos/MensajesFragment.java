package com.contactos.sin.sincontactos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.contactos.sin.sincontactos.common.adapter.LazyAdapterMensajes;
import com.contactos.sin.sincontactos.common.entidades.Mensaje;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MensajesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MensajesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MensajesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String METHOD_NAME_OBTENER_MENSAJES = "obtenerMensajes";
    private String webResponse = "";
    private Thread thread;
    private Handler handler = new Handler();
    private Type collectionTypeMensaje = new TypeToken<List<Mensaje>>(){}.getType();
    private static  MainActivity activity;
    private ListView list;
    private LazyAdapterMensajes adapterList;

    private OnFragmentInteractionListener mListener;

    public MensajesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MensajesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MensajesFragment newInstance(String param1, String param2) {
        MensajesFragment fragment = new MensajesFragment();
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
        View v = inflater.inflate(R.layout.fragment_mensajes, container, false);

        activity = (MainActivity)getActivity();
        list = (ListView)v.findViewById(R.id.list);
        activity.showProgress(true,"Cargando ultimas notificaciones");

        obtenerMensajes(activity.getCurrentTienda().getIdtienda(),activity.getCurrentUser().getIdusuario());

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionMensajes(uri);
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
        void onFragmentInteractionMensajes(Uri uri);
    }

    public void obtenerMensajes(final int idTienda, final int idusuario){
        thread = new Thread(){
            public void run(){
                try {

                    SoapObject request = new SoapObject(activity.NAMESPACE_MENSAJE, METHOD_NAME_OBTENER_MENSAJES);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("idtienda");
                    fromProp.setValue(idTienda);
                    fromProp.setType(int.class);
                    request.addProperty(fromProp);

                    PropertyInfo fromProp2 = new PropertyInfo();
                    fromProp2.setName("idusuario");
                    fromProp2.setValue(idusuario);
                    fromProp2.setType(int.class);
                    request.addProperty(fromProp2);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(activity.URL_MENSAJE);

                    androidHttpTransport.call(activity.SOAP_ACTION_MENSAJE, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }
                handler.post(createUIMensajes);
            }
        };

        thread.start();
    }

    final Runnable createUIMensajes = new Runnable() {

        public void run(){

            if(!webResponse.equals("") && !webResponse.equals("[]")){

                List<Object> lcs = activity.jsonToListObject(webResponse,collectionTypeMensaje);

                if (lcs.size() > 0) {

                    adapterList = new LazyAdapterMensajes(activity, lcs);
                    list.setAdapter(adapterList);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {

                            activity.setCurrentFragment(2);

                            Fragment fragment = new VerMensajeFragment();
                            Bundle bundle = new Bundle();
                            Mensaje mensaje = (Mensaje) adapterList.getItem(position);
                            bundle.putInt("idmensaje", mensaje.getIdmensaje());
                            bundle.putString("texto", mensaje.getTexto());
                            bundle.putString("imagen", mensaje.getImagen());
                            bundle.putString("fecha", mensaje.getFecha());
                            fragment.setArguments(bundle);

                            final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment, fragment, "VerMensaje");
                            ft.commit();
                        }
                    });
                }

                activity.showProgress(false, "");

            }else{
                activity.showProgress(false, "");
            }
        }
    };
}
