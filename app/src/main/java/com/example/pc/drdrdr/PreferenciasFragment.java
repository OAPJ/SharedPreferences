package com.example.pc.drdrdr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PreferenciasFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String KEY_CORREO_ED = "correoKey";
    private static final String KEY_CLAVE_ED = "claveKey";
    private static final String KEY_CORREO_CHK = "recordarCorreo";
    private static final String KEY_CLAVE_CHK = "recordarClave";
    private static final String KEY_SESION = "Sesion";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }

    public static String getString(Context context, final String key){
        SharedPreferences shaPre = PreferenceManager.getDefaultSharedPreferences(context);
        return shaPre.getString(key, "");
    }

    public static void setString(Context context, final String key, final String value){
        SharedPreferences shaPre = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shaPre.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static Boolean getBoolean(Context context, final String key, final Boolean defaultValue){
        SharedPreferences shaPre = PreferenceManager.getDefaultSharedPreferences(context);
        return shaPre.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context context, final String key, final Boolean value){
        SharedPreferences shaPre = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shaPre.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static void showUserSettings(Context ctx){
        SharedPreferences shaPre = PreferenceManager.getDefaultSharedPreferences(ctx);
        StringBuilder builder = new StringBuilder();
        builder.append("\n Correo: "+shaPre.getString(KEY_CORREO_ED, "NULL"));
        builder.append("\n Recordar Correo: "+shaPre.getBoolean(KEY_CORREO_CHK, false));
        builder.append("\n Clave: "+shaPre.getString(KEY_CLAVE_ED, "NULL"));
        builder.append("\n Recordar Clave: "+shaPre.getBoolean(KEY_CLAVE_CHK, false));
        builder.append("\n Sesion: "+shaPre.getBoolean(KEY_SESION, false));
    }

    public static String getKeyCorreoEd() {
        return KEY_CORREO_ED;
    }

    public static String getKeyClaveEd() {
        return KEY_CLAVE_ED;
    }

    public static String getKeyCorreoChk() {
        return KEY_CORREO_CHK;
    }

    public static String getKeyClaveChk() {
        return KEY_CLAVE_CHK;
    }

    public static String getKeySesion() {
        return KEY_SESION;
    }

    public void onResume(){
        super.onResume();
        //Encender el escuchador de eventos
        //por si alguna clave de preferecias cambia
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause(){
        super.onPause();
        //apagar el escuchador de
        //eventos de preferencias
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        switch (key){
            case KEY_CORREO_ED:
                //String valorCorreoEd =""+shaPref.getString(KEY_CORREO_ED,"NULL");
                Toast.makeText(getActivity().getApplicationContext(),"Se cambio el correo",Toast.LENGTH_SHORT).show();
                break;
            case KEY_CLAVE_ED:
                Toast.makeText(getActivity().getApplicationContext(),"Se cambio la clave",Toast.LENGTH_SHORT).show();
                break;
            case KEY_CORREO_CHK:
                //Toast.makeText(getActivity().getApplicationContext(),"Cambio guardado",Toast.LENGTH_SHORT).show();
            case KEY_CLAVE_CHK:
                //Toast.makeText(getActivity().getApplicationContext(),"Cambio guardado",Toast.LENGTH_SHORT).show();
                break;
            case KEY_SESION:
                //Toast.makeText(getActivity().getApplicationContext(),"Cambio guardado",Toast.LENGTH_SHORT).show();
                if(sharedPreferences.getBoolean(KEY_SESION, false))
                {
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }
}
