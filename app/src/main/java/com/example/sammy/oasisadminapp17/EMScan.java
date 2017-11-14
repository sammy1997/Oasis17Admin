package com.example.sammy.oasisadminapp17;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class EMScan extends AppCompatActivity {
    String csv;
    Activity activity;
    ImageView status;
    SharedPreferences preferences,preference2;
    TextView csvList;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emscan);
        activity=this;
        preferences=activity.getSharedPreferences("Prefs",MODE_PRIVATE);
        preference2=getSharedPreferences("Id",0);
        csv="";
        editor=preferences.edit();
        csvList=(TextView)findViewById(R.id.list);
        Button scan=(Button)findViewById(R.id.scan_ems_main);
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
        status=(ImageView)findViewById(R.id.imageView);
        Button formTeam=(Button)findViewById(R.id.form_team);
        formTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamUp();
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
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                final String contents=result.getContents();
                String x=preferences.getString("csv","");
                csv=  x +contents + ",";
                editor.putString("csv",csv);
                editor.commit();
                csvList.setText(csv);
                Log.d("Content",csv);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void teamUp(){
        csv=csvList.getText().toString();

        try {
            csv=csv.substring(0,csv.length()-1);
        }catch(Exception e){
            csv="";
        }
        String eventID=preference2.getString("Selected","");
        String username=preference2.getString("username","");
        String password=preference2.getString("password","");
        requestAdd(csv,eventID,username,password);
        Log.d("CSV",csv);
    }
    void requestAdd(String csv,String eventID,String username,String password){
        JSONObject auth=new JSONObject();
        final JSONObject body=new JSONObject();
        try{

                auth.put("username",username);
                auth.put("password",password);
                body.put("team",csv);
                body.put("event_id",eventID);
            }catch (JSONException e){
                Log.d("JSON error in ADD",e.getStackTrace().toString());
            }


        AndroidNetworking.post(URLS.tokenUrl).addJSONObjectBody(auth).build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token=response.getString("token");
                            Log.d("Token",token);

                            AndroidNetworking.post(URLS.registerTeam)
                                    .addHeaders("Authorization", "JWT "+token).addJSONObjectBody(body)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    csvList.setText("");
                                    editor.clear();
                                    editor.commit();
                                    String statusCode="";
                                    String message="";
                                    try {
                                        statusCode=response.getString("status");
                                        message=response.getString("message");
                                    }catch (JSONException e){

                                    }
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    if(statusCode.equals("1")){
                                        status.setImageResource(R.drawable.correct);
                                    }
                                    else {
                                        status.setImageResource(R.drawable.cancel);
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("AnError",anError.getErrorDetail());
                                    Toast.makeText(getApplicationContext(),"Error. Try again",Toast.LENGTH_SHORT)
                                            .show();
                                    status.setImageResource(R.drawable.cancel);
                                }
                            });


                        }catch (JSONException e){
                            Log.d("Token Error",e.toString());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error","To get Token");
                        status.setImageResource(R.drawable.not_applicable);
                    }
                });
    }
}
