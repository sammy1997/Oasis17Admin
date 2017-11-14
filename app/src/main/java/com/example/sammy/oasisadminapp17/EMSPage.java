package com.example.sammy.oasisadminapp17;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EMSPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    JSONObject authDetails;
    String username;
    String token;
    String password;
    Spinner eventList;
    ProgressBar progress;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ArrayList<String> events=new ArrayList<>();
    HashMap<String,String> idMap=new HashMap<>();
    String itemSelected;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemSelected=parent.getItemAtPosition(position).toString();
        ((TextView)parent.getChildAt(0)).setTextColor(0xFF000000);
        Log.d("Selected",itemSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        itemSelected="";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emspage);
        Bundle extras=getIntent().getExtras();
        progress=(ProgressBar)findViewById(R.id.progress_event);
        username=extras.getString("Username");
        password=extras.getString("Password");
        progress.setVisibility(View.VISIBLE);
        eventList=(Spinner)findViewById(R.id.event_list);
        eventList.setOnItemSelectedListener(this);
        authDetails=new JSONObject();
        preferences=this.getSharedPreferences("Id",MODE_PRIVATE);
        editor=preferences.edit();
        try {
            authDetails.put("username", username);
            authDetails.put("password", password);
        }
        catch (JSONException e){
            Log.d("AuthDetails",e.toString());
        }

        Button emsScan=(Button)findViewById(R.id.scan_ems);
        emsScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=idMap.get(itemSelected);
                editor.putString("Selected",id);
                editor.putString("username",username);
                editor.putString("password",password);
                editor.commit();
                Intent activity=new Intent(getApplicationContext(),EMScan.class);
                startActivity(activity);
            }
        });
        getToken(URLS.tokenUrl);
    }
    void fetchEvents(){
        Log.d("Here",token);
        AndroidNetworking.get(URLS.fetch_events).addHeaders("Authorization", "JWT "+token).build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Logged in. See events from Drop Down"
                                ,Toast.LENGTH_LONG).show();
                        try {
                        JSONArray resp=response.getJSONArray("events");
                        for (int i=0;i<resp.length();i++){

                                JSONObject jsonObject=resp.getJSONObject(i);
                                idMap.put(jsonObject.getString("name"),jsonObject.getString("id"));
                                events.add(jsonObject.getString("name"));

                        }
                        }catch (JSONException e){
                            Log.d("JSON Error",e.toString());
                        }
                        Log.e("items", events.toString());
                        ArrayAdapter<String> adapter=
                         new ArrayAdapter<>(getApplicationContext(),
                                 android.R.layout.simple_dropdown_item_1line, events);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        eventList.setAdapter(adapter);
                        eventList.setSelection(0);
                        Log.d("Map",idMap.toString());
                        Log.d("List",events.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getApplicationContext(),"Error. Not Authorized"
                                ,Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.INVISIBLE);
                        Log.d("Fetch Error", anError.getErrorDetail());
                    }
                });
    }
    void getToken(String url){
        AndroidNetworking.post(url).addJSONObjectBody(authDetails).build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                             token=response.getString("token");
                            Log.d("Token",token);
                            fetchEvents();
                        }catch (JSONException e){
                            Log.d("Token Error",e.toString());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error","Bitch");
                        Toast.makeText(getApplicationContext(),"Error. Enter correct Username & password"
                                ,Toast.LENGTH_LONG).show();
                    }
                });

    }
}
