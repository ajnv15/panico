package wap.com.panico;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;


import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
// android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;



public class panico extends Activity {
    EditText txtLat;
    EditText txtLong;
    LocationManager ubicacion;
    private EditText telefono;
    String provider;
    Button panico;
    int panic=0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panico);





    }

    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "imposible leer ubicacion desde el servidor");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }





    public void btnFisico() {

        ubicacion= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria=new Criteria();
        String proveedor=ubicacion.getBestProvider(criteria,true);
        Location posActual=ubicacion.getLastKnownLocation(proveedor);
        String latitud=""+posActual.getLatitude();
        String longitud=""+posActual.getLongitude();





        new ReadLocationJSONFeedTask().execute(
                "http://maps.googleapis.com/maps/api/geocode/json?latlng=" +latitud+","+
                       longitud+"&sensor=false");
        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("http://panico.webatu.com/marker.php?lat="+latitud+"&lon="+longitud+"&folio=444444" +""));
        startActivity(i);
                }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_panico, menu);
        return true;





    }




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //este motodo intercepta la tecla de volumen


        switch(keyCode){



            case KeyEvent.KEYCODE_VOLUME_UP:

                event.startTracking();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                btnFisico();
                //Toast.makeText(this,"Volumen abajo"+numero, Toast.LENGTH_SHORT).show();
                return true;
        }



        return super.onKeyDown(keyCode, event);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public static String remove1(String input) {

        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        //funcion para quitar acentos y caracteres especiales y solo enviar mensaje
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    private class ReadLocationJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                String direccion="";
                JSONObject jsonObject = new JSONObject(result);
                direccion=""+jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                direccion=remove1(direccion);

                Toast.makeText(getBaseContext(),"Direccion Actual: "+direccion  ,Toast.LENGTH_SHORT).show();
                SmsManager sms = SmsManager.getDefault();
                //sms.sendTextMessage(telefono.getText().toString(), null, "Estoy en peligro en " + direccion,null, null);

            } catch (Exception e) {
                Log.d("Error de ubicacion", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }
    }

}



