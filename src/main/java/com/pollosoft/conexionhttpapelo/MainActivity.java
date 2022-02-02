package com.pollosoft.conexionhttpapelo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    TextView textViewRes;
    EditText editTextUrl;
    Button buttonDescargar;
    Button buttonLimpiar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewRes = findViewById(R.id.textViewResultado);
        textViewRes.setMovementMethod(new ScrollingMovementMethod());
        editTextUrl = findViewById(R.id.editTextUrl);
        buttonLimpiar = findViewById(R.id.buttonClear);
        buttonDescargar = findViewById(R.id.buttonDescargar);

        buttonDescargar.setOnClickListener(v -> descargar(v));
        buttonLimpiar.setOnClickListener(v -> editTextUrl.setText(""));
    }

    private void descargar(View v) {
        String url = editTextUrl.getText().toString().trim();
        new DescargaWeb().execute(url);
    }


    private static String readStream(InputStream is) {

        StringBuilder sb = new StringBuilder();

        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }



    private class DescargaWeb extends AsyncTask <String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection con;
                if (strings[0].startsWith("https")) {
                    con = (HttpsURLConnection) url.openConnection();
                } else {
                    con = (HttpURLConnection) url.openConnection();
                }

                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();

                int response = con.getResponseCode();
                //Toast.makeText(MainActivity.this, "Código de respuesta " + response, Toast.LENGTH_SHORT).show();

                return MainActivity.readStream(con.getInputStream()); //Tiene que devolver el texto que corresponde al documento web descargado


                //Tendremos que convertir el InputStream en String para devolverlo

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Hubo algún error en la conexión.";
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textViewRes.setText(s);

            //rellenar arry list de pelis con la info que tengo en el json s
        }
    }
}