package com.contactos.sin.sincontactos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.contactos.sin.sincontactos.common.entidades.Contacto;
import com.contactos.sin.sincontactos.common.entidades.Usuario;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
 * Created by Rolando on 24/07/2017.
 */

public class ContactosTaskService extends GcmTaskService {

    private static final String TAG = "ContactosTaskService";

    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_RESULT = "extra_result";

    private String IP = "192.168.43.195";

    private final String METHOD_NAME = "registrarContactos";
    public final String NAMESPACE_CONTACTO = "http://"+IP+"/webServicesSinContactos/servicio-contactos.php";
    public final String URL_CONTACTO= "http://"+IP+"/webServicesSinContactos/servicio-contactos.php?wsdl";
    public final String SOAP_ACTION_CONTACTO = "http://"+IP+"/webServicesSinContactos/servicio-contactos.php?wsdl";

    private String webResponse = "";

    private Thread thread;
    private Handler handler = new Handler();

    @Override
    public void onInitializeTasks() {
        // When your package is removed or updated, all of its network tasks are cleared by
        // the GcmNetworkManager. You can override this method to reschedule them in the case of
        // an updated package. This is not called when your application is first installed.
        //
        // This is called on your application's main thread.

        // TODO(developer): In a real app, this should be implemented to re-schedule important tasks.
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "onRunTask: " + taskParams.getTag());

        String tag = taskParams.getTag();

        // Default result is success.
        int result = GcmNetworkManager.RESULT_SUCCESS;

        // Choose method based on the tag.
        if (MainActivity.TASK_TAG_PERIODIC.equals(tag)) {
            result = doPeriodicTask();
        }

        // Create Intent to broadcast the task information.
        Intent intent = new Intent();
        intent.setAction(ACTION_DONE);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_RESULT, result);

        // Send local broadcast, running Activities will be notified about the task.
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.sendBroadcast(intent);

        return result;
    }

    private int doPeriodicTask() {

        return respaldarContactos();
    }

    private int respaldarContactos() {

        try {
            SharedPreferences prefs = getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);

            Usuario usuario = null;

            if(!prefs.getString("USER_DATA","").equals(""))
            {
                Type collectionType = new TypeToken<List<Usuario>>(){}.getType();
                List<Usuario> usuarios = new Gson().fromJson( prefs.getString("USER_DATA","") , collectionType);

                usuario = usuarios.get(0);
            }

            List<Contacto> list = getContactos(usuario.getIdusuario());
            Log.d(TAG, "respaldarContactos: Cantidad: " +  list.size());

            String jsonList = listObjectToJson(list);

            registrarContactos(jsonList, usuario.getIdusuario());

        } catch (Exception e) {
            Log.e(TAG, "respaldarContactos: error" + e.toString());
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private List<Contacto> getContactos(int idusuario) {

        List<Contacto> contacts = new ArrayList<>();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contacto contacto = new Contacto();
            contacto.setIdusuario(idusuario);
            contacto.setNombre(name);
            contacto.setTelefono(phoneNumber);

            contacts.add(contacto);

        }
        phones.close();


        return contacts;
    }

    public String listObjectToJson(List<Contacto> list){

        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();

        return myCustomArray.toString();
    }

    public void registrarContactos(final String json, final int idusuario){
        thread = new Thread(){
            public void run(){
                try {
                    SoapObject request = new SoapObject(NAMESPACE_CONTACTO, METHOD_NAME);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("json");
                    fromProp.setValue(json);
                    fromProp.setType(String.class);
                    request.addProperty(fromProp);

                    PropertyInfo fromProp2 = new PropertyInfo();
                    fromProp2.setName("idusuario");
                    fromProp2.setValue(idusuario);
                    fromProp2.setType(int.class);
                    request.addProperty(fromProp2);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_CONTACTO);

                    androidHttpTransport.call(SOAP_ACTION_CONTACTO, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                }catch(Exception e){
                    Log.d(TAG, "respaldarContactos: Exception:"+ e.getMessage() );
                }

            }
        };

        thread.start();
    }
}