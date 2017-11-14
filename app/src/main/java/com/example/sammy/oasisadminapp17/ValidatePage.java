package com.example.sammy.oasisadminapp17;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import static com.example.sammy.oasisadminapp17.CONSTANTS.adminName;
import static com.example.sammy.oasisadminapp17.CONSTANTS.adminPass;

public class ValidatePage extends AppCompatActivity {
    String token;
    ProgressBar progressBar;
    String selectedItem;
    String contents;
    ImageView status;
    Activity activity;
    HashMap<String,String> map=new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_page);
        activity=this;
        progressBar=(ProgressBar)findViewById(R.id.progressBar3);
        status=(ImageView)findViewById(R.id.status);
        Button scan=(Button)findViewById(R.id.scan_qr_code);
        SharedPreferences preferences=getSharedPreferences("SelectItem",0);
        selectedItem=preferences.getString("SelectedItem","");
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator=new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }


    void getProfs(String token_loc){
        AndroidNetworking.get(URLS.profshows).addHeaders("Authorization", "JWT "+token_loc)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        map.put(jsonObject.getString("name"),jsonObject.getString("id"));

                    }catch (JSONException e){
                        Log.d("JSON Error",e.toString());
                    }
                }


                String prof_id=map.get(selectedItem);
                try {
                    JSONObject authDetails=new JSONObject();
                    authDetails.put("barcode",contents);
                    authDetails.put("prof_show",prof_id);
                    validate(authDetails);
                }catch (JSONException e){

                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Prof Error",anError.getStackTrace().toString());
            }
        });
    }


    void getToken(JSONObject authDetails){


        AndroidNetworking.post(URLS.tokenUrl).addJSONObjectBody(authDetails).build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token=response.getString("token");
                            getProfs(token);
                        }catch (JSONException e){
                            Log.d("Token Error",e.getStackTrace().toString());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error","Bitch");
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents()==null){
                Toast.makeText(this, "You cancelled scan", Toast.LENGTH_SHORT).show();
            }
            else {
                contents=result.getContents();
                Log.d("Content",contents);
                progressBar.setVisibility(View.VISIBLE);
                JSONObject authDetails=new JSONObject();
                try {
                    authDetails.put("username", adminName);
                    authDetails.put("password", adminPass);
                }
                catch (JSONException e){
                    Log.d("AuthDetails",e.getStackTrace().toString());
                }
                getToken(authDetails);

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void validate(JSONObject authDetails){

            Log.d("In Validate",authDetails.toString());
            AndroidNetworking.post(URLS.validateProf).addHeaders("Authorization", "JWT "+token)
                    .addJSONObjectBody(authDetails).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    try {

                        Log.d("Response",response.toString());
                        String message=response.getString("message");
                        Log.e("dfds", message);
                        //Log.e("dxfds", response.getString("bitsian"));
                        if (message.equals("Invalid Prof Show")){
                            status.setImageResource(R.drawable.not_applicable);
                            Log.e("lslf", "sdf");
                        }else if(message.equals("No more passes left. Please register at DoLE Booth.")){
                            status.setImageResource(R.drawable.cancel);
                            Log.e("lslf", "sdfsdfsdfsdfwd");
                        }
                        else if(response.has("participant") || response.has("bitsian")){
                            status.setImageResource(R.drawable.correct);
                            Log.e("lslf", "sddsfds");
                        }


                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(getApplicationContext(),"Some Error Occurred",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("Error Validate","AneError");
                }
            });


    }
}
