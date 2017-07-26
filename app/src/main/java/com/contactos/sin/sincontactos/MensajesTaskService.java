package com.contactos.sin.sincontactos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.contactos.sin.sincontactos.common.entidades.Notificacion;
import com.contactos.sin.sincontactos.common.entidades.Tienda;
import com.contactos.sin.sincontactos.common.entidades.Usuario;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Rolando on 24/07/2017.
 */

public class MensajesTaskService extends GcmTaskService {

    private static final String TAG = "MensajesTaskService";

    public static final String ACTION_DONE = "GcmTaskService#ACTION_DONE";
    public static final String EXTRA_TAG = "extra_tag";
    public static final String EXTRA_RESULT = "extra_result";

    private String IP = "192.168.43.195";

    private final String METHOD_NAME = "obtenerNotificacion";
    public final String NAMESPACE_MENSAJES = "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php";
    public final String URL_MENSAJES= "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php?wsdl";
    public final String SOAP_ACTION_MENSAJES = "http://"+IP+"/webServicesSinContactos/servicio-mensajes.php?wsdl";

    private String webResponse = "";

    private Thread thread;
    private Handler handler = new Handler();

    private Type collectionTypeNotificacion = new TypeToken<List<Notificacion>>(){}.getType();

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
        //Log.d(TAG, "onRunTask: " + taskParams.getTag());

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

        return obtenerNotificacion();
    }

    private int obtenerNotificacion() {

        try {
            SharedPreferences prefs = getSharedPreferences("com.contactos.sin.sincontactos", Context.MODE_PRIVATE);

            Usuario usuario = new Usuario();

            if(!prefs.getString("USER_DATA","").equals(""))
            {
                Type collectionType = new TypeToken<List<Usuario>>(){}.getType();
                List<Usuario> usuarios = new Gson().fromJson( prefs.getString("USER_DATA","") , collectionType);

                usuario = usuarios.get(0);
            }

            Tienda tienda = new Tienda();

            if(!prefs.getString("TIENDA_APP","").equals(""))
            {
                Type collectionType = new TypeToken<List<Tienda>>(){}.getType();
                List<Tienda> tiendas = new Gson().fromJson( prefs.getString("TIENDA_APP","") , collectionType);

                tienda = tiendas.get(0);
            }

            obtenerNotificacion(tienda.getIdtienda(), usuario.getIdusuario());

        } catch (Exception e) {
            Log.e(TAG, "Mensajes Service: error" + e.toString());
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public void obtenerNotificacion(final int idtienda, final int idusuario){
        thread = new Thread(){
            public void run(){
                try {
                    SoapObject request = new SoapObject(NAMESPACE_MENSAJES, METHOD_NAME);

                    PropertyInfo fromProp = new PropertyInfo();
                    fromProp.setName("idtienda");
                    fromProp.setValue(idtienda);
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
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_MENSAJES);

                    androidHttpTransport.call(SOAP_ACTION_MENSAJES, envelope);
                    Object response = envelope.getResponse();
                    webResponse = response.toString();

                    if(!webResponse.equals("") && !webResponse.equals("[]")){

                        List<Object> noticaciones = jsonToListObject(webResponse, collectionTypeNotificacion);
                        Notificacion notificacion = (Notificacion)noticaciones.get(0);
                        showNotification(notificacion);
                    }

                }catch(Exception e){
                    Log.d(TAG, "obtenerNotificacion: Exception:"+ e.getMessage() );
                }

            }
        };

        thread.start();
    }

    public List<Object> jsonToListObject(String json, Type collectionType){
        List<Object> list = new Gson().fromJson(json, collectionType);

        return list;
    }

    private void showNotification(Notificacion notificacion) {

        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("APP_ES_NOTIFICACION",true);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification myNotification = new NotificationCompat.Builder(this)
                .setContentTitle(notificacion.getTitulo())
                .setContentText(notificacion.getTexto())
                .setTicker("Notificación!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                .build();

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(11, myNotification);
    }
}