package com.example.pc.drdrdr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RequestQueue colaSolicitud;
    //Declaramos las variables
    Button btnLogin, btnRegistro;
    EditText etCorreo, etClave;
    Context ctx;
    String correo, clave;
    String c="", cl="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx=this;
        addView();
        comprobarPreferencias();
        logeado();

        btnRegistro.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //Toast.makeText(getApplicationContext(),SharedPrefManager.getInstance(this).getToken(),Toast.LENGTH_LONG).show();
    }

    private void addView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        colaSolicitud = Volley.newRequestQueue(this);
        btnLogin = (Button) findViewById(R.id.btLogin);
        btnRegistro = (Button) findViewById(R.id.btRegistrarse);
        etClave = (EditText) findViewById(R.id.etClave);
        etCorreo = (EditText) findViewById(R.id.etCorreo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            startActivity(new Intent(this, PreferenciasActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btLogin){
            correo = etCorreo.getText().toString();
            clave = etClave.getText().toString();

            if(validar(correo) && validar(clave)){
                PreferenciasFragment.setString(ctx, PreferenciasFragment.getKeyCorreoEd(),correo);
                PreferenciasFragment.setString(ctx, PreferenciasFragment.getKeyClaveEd(), clave);

                PreferenciasFragment.showUserSettings(ctx);
                guardarDatos();
                consultarUsuario();
            }
            else{
                Toast.makeText(getApplicationContext(), "Datos incompletos", Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == R.id.btRegistrarse){
            startActivity(new Intent(this, Registro.class));
        }
    }

    public void comprobarPreferencias(){
        if(PreferenciasFragment.getBoolean(ctx, PreferenciasFragment.getKeyCorreoChk(), false))
            etCorreo.setText(PreferenciasFragment.getString(ctx, PreferenciasFragment.getKeyCorreoEd()));
        if (PreferenciasFragment.getBoolean(ctx, PreferenciasFragment.getKeyClaveChk(), false))
            etClave.setText(PreferenciasFragment.getString(ctx, PreferenciasFragment.getKeyClaveEd()));
        guardarDatos();
    }

    public void logeado(){
        int j=0;

        if(PreferenciasFragment.getBoolean(ctx, PreferenciasFragment.getKeySesion(), false)){
            //startActivity(new Intent(this, Main2Activity.class));
            try {
                BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("Datos.txt")));

                String texto = fin.readLine();
                for(int i=0; i<texto.length(); i++){
                    if(texto.charAt(i)==','){
                        j=i;
                        break;
                    }else
                        c+=texto.charAt(i);
                }
                for(int i=j+1; i<texto.length(); i++){
                    cl+=texto.charAt(i);
                }

                fin.close();

            } catch (Exception e) {

                //Toast.makeText(getBaseContext(),"Error al leer el fichero",Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(getBaseContext(),c+" "+cl,Toast.LENGTH_SHORT).show();

            String URL = "http://192.168.0.16/getUsuario.php?correo="+c+"&clave="+cl;
            //String URL = "http://192.168.0.16/getUsuario.php?correo=juan@gmail.com&clave=juan98";

            JsonObjectRequest getSolicitudUsuario = new JsonObjectRequest(
                    Request.Method.GET,
                    URL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject respuestaUsuario = response;
                            try {
                                int estado = respuestaUsuario.getInt("estado");
                                String mensaje = respuestaUsuario.getString("mensaje");

                                if(estado==1) {
                                    //Toast.makeText(getApplicationContext(),"2", Toast.LENGTH_SHORT).show();
                                    JSONArray arrayUsuarioJSON = respuestaUsuario.getJSONArray("usuario");

                                    //quiero todos los objeos de  arrayJSON
                                    for(int i=0; i<arrayUsuarioJSON.length();i++){
                                        //Obterner cada uno de ellos
                                        JSONObject empleadoJSOn = arrayUsuarioJSON.getJSONObject(i);
                                        String correo = empleadoJSOn.getString("correo");
                                        String clave = empleadoJSOn.getString("clave");

                                        if(correo.equals("null") || clave.equals("null")){
                                            Toast.makeText(getApplicationContext(),"Datos Incorrectos", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException e){
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
            );
            colaSolicitud.add(getSolicitudUsuario);
        }
    }

    public void guardarDatos(){
        try {
            OutputStreamWriter fout = new OutputStreamWriter(openFileOutput(
                    "Datos.txt", Context.MODE_PRIVATE));

            fout.write(etCorreo.getText().toString()+","+etClave.getText().toString());
            fout.close();
        } catch (Exception e){
            Toast.makeText(getBaseContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validar(String t){
        return t!=null && t.trim().length()>3;
    }

    public void consultarUsuario() {
        //Hacemos una solicitud por GET
        String URL = "http://192.168.0.16/getUsuario.php?correo="+etCorreo.getText().toString()+"&clave="+etClave.getText().toString();

        JsonObjectRequest getSolicitudUsuario = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject respuestaUsuario = response;
                        try {
                            int estado = respuestaUsuario.getInt("estado");
                            String mensaje = respuestaUsuario.getString("mensaje");

                            if(estado==1) {
                                //Toast.makeText(getApplicationContext(),"2", Toast.LENGTH_SHORT).show();
                                JSONArray arrayUsuarioJSON = respuestaUsuario.getJSONArray("usuario");

                                //quiero todos los objeos de  arrayJSON
                                for(int i=0; i<arrayUsuarioJSON.length();i++){
                                    //Obterner cada uno de ellos
                                    JSONObject empleadoJSOn = arrayUsuarioJSON.getJSONObject(i);
                                    String correo = empleadoJSOn.getString("correo");
                                    String clave = empleadoJSOn.getString("clave");

                                    if(correo.equals("null") || clave.equals("null")){
                                        Toast.makeText(getApplicationContext(),"Datos Incorrectos", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
        colaSolicitud.add(getSolicitudUsuario);
    }
}
