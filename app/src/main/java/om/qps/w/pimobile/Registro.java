package om.qps.w.pimobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Registro extends AppCompatActivity {

    private EditText txt_clave;
    private String user, password;
    private Button buscar;
    private SQLiteDatabase db;
    private UsuariosDB usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //BASE DE DATOS
        usuarios = new UsuariosDB(this, "Usuarios", null, 1);
        db = usuarios.getWritableDatabase();

        String query = "SELECT * FROM Registro;";
        Cursor c = db.rawQuery(query, null);
        if(c.getCount() > 0) {
            lanzarLogin();
        }else{
            txt_clave = (EditText) findViewById(R.id.key_activation);
            buscar = (Button) findViewById(R.id.acceso);
            buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String clave = txt_clave.getText().toString();
                    if(!clave.isEmpty()) {
                        new KeyTask().execute("http://192.168.1.155/api/usuariostomobileservice/?llave="+clave);
                    }else{
                        Toast.makeText(getApplicationContext(), "CAMPO VACÍO", Toast.LENGTH_SHORT).show();
                        txt_clave.setFocusable(true);
                    }
                }
            });
        }
    }

    //CLASE PARA REALIZAR LA PETICION
    private class KeyTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conexion = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                conexion = (HttpURLConnection) url.openConnection();
                conexion.connect();

                InputStream stream = conexion.getInputStream();
                reader  = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null ){
                    buffer.append(line);
                }

                String json = buffer.toString();

                return json;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                conexion.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            procesar(s);
        }

        private void procesar(String json){
            JSONObject objeto = null;
            try {
                objeto = new JSONObject(json);
                if(objeto != null) {
                    user = objeto.getString("usuario");
                    password = objeto.getString("contrasena");
                    String msj = "USUARIO REGISTRADO";
                    db.execSQL("INSERT INTO Registro VALUES('"+user+"', 1)");
                    Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_SHORT).show();
                    finish();
                    lanzarLogin();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Clave inválida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void lanzarLogin(){
        Intent login = new Intent(getApplicationContext(), Login.class).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(login);
        finish();
    }
}