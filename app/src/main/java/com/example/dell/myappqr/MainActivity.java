package com.example.dell.myappqr;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;
import android.location.Geocoder;
import android.location.Location;
import android.location.GnssNavigationMessage;
import android.location.LocationProvider;
import android.location.GnssStatus;
import android.location.Address;
import android.location.LocationManager;

public class MainActivity extends AppCompatActivity implements 
{
    String scannedData;
    Button scanBtn;
    private Geocoder map;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity activity =this;
        scanBtn = (Button)findViewById(R.id.scanB);
        scanBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan the QR code for Attendance");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            scannedData = result.getContents();
            if (scannedData != null)
            {
                new SendRequest().execute();
            }
            else
                {
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}
        protected String doInBackground(String... arg0)
        {
            try{
                URL url = new URL(""); //Paste here your AppScript URL that is generated after deploying it as a Web App
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("sdata",scannedData); //Variable same as declared in AppScript
                Log.e("params",postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line;
                    while((line = in.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();
                }
                else
                {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e)
            {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
    public String getPostDataString(JSONObject params) throws Exception
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext())
        {
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}

