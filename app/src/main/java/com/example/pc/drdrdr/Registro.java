package com.example.pc.drdrdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Registro extends AppCompatActivity implements View.OnClickListener {

    RequestQueue colaSolicitud;
    //Declaramos las variables
    Button btnLoginR, btnRegistrarseR;
    EditText etNombreR, etCorreoR, etClaveR;
    String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
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
        btnLoginR = (Button) findViewById(R.id.btLoginR);
        btnRegistrarseR = (Button) findViewById(R.id.btRegistrarseR);
        etClaveR = (EditText) findViewById(R.id.etClaveR);
        etCorreoR = (EditText) findViewById(R.id.etCorreoR);
        etNombreR = (EditText) findViewById(R.id.etNombreR);

        idToken = SharedPrefManager.getInstance(this).getToken();
        //Toast.makeText(getApplicationContext(), idToken, Toast.LENGTH_SHORT).show();

        btnRegistrarseR.setOnClickListener(this);
        btnLoginR.setOnClickListener(this);
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
        if(v.getId() == R.id.btRegistrarseR){
            if(etNombreR.getText().toString().isEmpty() || etCorreoR.getText().toString().isEmpty() || etClaveR.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Faltan datos", Toast.LENGTH_SHORT).show();
            }else{
                compararCorreo();
            }
        }
        if(v.getId() == R.id.btLoginR){
            Intent intent = new Intent(Registro.this ,MainActivity.class);
            startActivity(intent);
        }
    }

    private void compararCorreo() {
        //Hacemos una solicitud por GET
        String URL = "http://192.168.0.16/getCorreo.php?correo="+etCorreoR.getText().toString();
        // String URL = "http://192.168.0.16/getCorreo.php?correo=juan@gmail.com";
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
                                JSONArray arrayUsuarioJSON = respuestaUsuario.getJSONArray("usuario");
                                //quiero todos los objeos de  arrayJSON
                                for(int i=0; i<arrayUsuarioJSON.length();i++){
                                    //Obterner cada uno de ellos
                                    JSONObject empleadoJSOn = arrayUsuarioJSON.getJSONObject(i);
                                    String correo = empleadoJSOn.getString("correo");
                                    if(correo.equals("null")){
                                        agregarUsuario();
                                    } else{
                                        Toast.makeText(getApplicationContext(),"El correo ya fue usado ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else{
                                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
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

    public void agregarUsuario(){
        String URL = "http://192.168.0.16/addUsuario.php?%20correo="+etCorreoR.getText().toString()+"&nombre="+etNombreR.getText().toString()
                +"&clave="+etClaveR.getText().toString()+"&token="+idToken;
        //http://192.168.0.16/addUsuario.php?%20correo=ejemplo@gmail.com&nombre=Ejemplo&clave=ejemplo&token=ejmplo
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
                                Toast.makeText(getApplicationContext(),"Se agrego correctamente",Toast.LENGTH_SHORT).show();
                                etClaveR.setText("");
                                etCorreoR.setText("");
                                etNombreR.setText("");
                            } else{
                                Toast.makeText(getApplicationContext(),"No se agrego",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"ERROR: "+error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
        colaSolicitud.add(getSolicitudUsuario);
    }

}
