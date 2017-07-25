package com.contactos.sin.sincontactos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;

import com.contactos.sin.sincontactos.common.Base;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

public class MainActivity extends Base implements CuentasFragment.OnFragmentInteractionListener,
                                                  TiendaFragment.OnFragmentInteractionListener,
                                                  AgregarCuentaFragment.OnFragmentInteractionListener,
                                                  ContactosFragment.OnFragmentInteractionListener{
    private TextView mTextMessage;
    private String nombre = "";

    private View mProgressView;
    private View mLoginFormView;
    private TextView textProgress;
    private int currentFragment = 0;

    private static final String TAG = "MainActivity";
    private static final int RC_PLAY_SERVICES = 123;

    public static final String TASK_TAG_PERIODIC = "periodic_task";

    private GcmNetworkManager mGcmNetworkManager;
    private BroadcastReceiver mReceiver;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private boolean cargarCuentas = true;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ft.replace(R.id.fragment, new CuentasFragment(), "Cuentas");
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ft.replace(R.id.fragment, new ContactosFragment(), "Contactos");
                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    return true;
                case R.id.navigation_tienda:
                    ft.replace(R.id.fragment, new TiendaFragment(), "Tienda");
                    ft.commit();
                    return true;
            }
            return false;
        }

    };

    public int getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(int currentFragment1) {
        currentFragment = currentFragment1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if(getCurrentUser()!= null){
            nombre = getCurrentUser().getNombre()+" " +getCurrentUser().getApellido();
        }

        try{
            this.getSupportActionBar().setTitle("Bienvenido - "+ nombre);
        }catch (Exception ex){
            ex.getMessage();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CuentasFragment fragment = new CuentasFragment();

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, fragment, "Cuentas");
        ft.commit();

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        textProgress = (TextView) findViewById(R.id.textProgress);

        // [START get_gcm_network_manager]
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        // [END get_gcm_network_manager]

        // BroadcastReceiver to get information from MyTaskService about task completion.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(MyTaskService.ACTION_DONE)) {
                    String tag = intent.getStringExtra(MyTaskService.EXTRA_TAG);
                    int result = intent.getIntExtra(MyTaskService.EXTRA_RESULT, -1);

                    //String msg = String.format("DONE: %s (%d)", tag, result);
                    //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        };

        checkPlayServicesAvailable();

        if(!isMyServiceRunning(MyTaskService.class))
        {
            startPeriodicTask();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final String text) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            textProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            textProgress.setText(text);

            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            textProgress.setText(text);
            textProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onFragmentInteractionTienda(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onFragmentInteractionAgregarCuenta(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onBackPressed() {
        if(getCurrentFragment() == 1){
            setCurrentFragment(0);
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment, new CuentasFragment(), "Cuentas");
            ft.commit();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void onFragmentInteractionContacto(Uri uri) {

    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyTaskService.ACTION_DONE);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();

        //LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
       // manager.unregisterReceiver(mReceiver);

        // For the purposes of this sample, cancel all tasks when the app is stopped.
        //mGcmNetworkManager.cancelAllTasks(MyTaskService.class);
    }

    public void startPeriodicTask() {
        Log.d(TAG, "startPeriodicTask");

        // [START start_periodic_task]
        // 86400000L
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(MyTaskService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(86400000L)
                .build();

        mGcmNetworkManager.schedule(task);

        // [END start_periodic_task]
    }

    public void stopPeriodicTask() {
        Log.d(TAG, "stopPeriodicTask");

        // [START stop_periodic_task]
        mGcmNetworkManager.cancelTask(TASK_TAG_PERIODIC, MyTaskService.class);
        // [END stop_per
    }

    private void checkPlayServicesAvailable() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int resultCode = availability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(resultCode)) {
                // Show dialog to resolve the error.
                availability.getErrorDialog(this, resultCode, RC_PLAY_SERVICES).show();
            } else {
                // Unresolvable error
                Toast.makeText(this, "Google Play Services error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isCargarCuentas() {
        return cargarCuentas;
    }

    public void setCargarCuentas(boolean cargarCuentas) {
        this.cargarCuentas = cargarCuentas;
    }
}
