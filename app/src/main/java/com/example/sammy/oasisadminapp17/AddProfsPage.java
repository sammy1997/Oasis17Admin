package com.example.sammy.oasisadminapp17;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AddProfsPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ImageView imageView;
    Activity activity;
    String selectedItem;
    String token;
    String selectedId;
    HashMap<String, String> map = new HashMap();
    EditText n2000 ;
    EditText n500 ;
    EditText n200 ;
    EditText n100 ;
    EditText n50 ;
    EditText n20 ;
    EditText n10 ;
    EditText tickets ;
    EditText creator ;
    EditText BitsID;
    String contents;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem = parent.getItemAtPosition(position).toString();
        Log.d("Selected", selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profs_page);
        activity = this;
        n2000 = (EditText) findViewById(R.id.n_2000);
        n500 = (EditText) findViewById(R.id.n_500);
        n200 = (EditText) findViewById(R.id.n_200);
        n100 = (EditText) findViewById(R.id.n_100);
        n50 = (EditText) findViewById(R.id.n_50);
        n20 = (EditText) findViewById(R.id.n_20);
        n10 = (EditText) findViewById(R.id.n_10);
        BitsID=(EditText)findViewById(R.id.bits_id);
        tickets = (EditText) findViewById(R.id.count);
        creator = (EditText) findViewById(R.id.member_name);
        Button scan = (Button) findViewById(R.id.scan_add_prof);
        Spinner spinner = (Spinner) findViewById(R.id.prof_list);
        spinner.setOnItemSelectedListener(this);
        imageView = (ImageView) findViewById(R.id.status_image);


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }

    void getToken(JSONObject authDetails) {


        AndroidNetworking.post(URLS.tokenUrl).addJSONObjectBody(authDetails).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.getString("token");
                            Log.d("Token",token);
                            getProfs(token);
                        } catch (JSONException e) {
                            Log.d("Token Error", e.getStackTrace().toString());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("Error", "Bitch");
                    }
                });
    }


    void getProfs(String token_loc) {
        AndroidNetworking.get(URLS.profshows).addHeaders("Authorization", "JWT " + token_loc)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Here",response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject jsonObject = response.getJSONObject(i);
                        map.put(jsonObject.getString("name"), jsonObject.getString("id"));

                    } catch (JSONException e) {
                        Log.d("JSON Error", e.toString());
                    }
                }
                selectedId=map.get(selectedItem);
                try {
                    JSONObject requestBody=new JSONObject();
                    requestBody.put("barcode",contents);
                    requestBody.put("prof_show",selectedId);
                    requestBody.put("count",tickets.getText().toString());
                    requestBody.put("n_2000",n2000.getText().toString());
                    requestBody.put("n_500",n500.getText().toString());
                    requestBody.put("n_200",n200.getText().toString());
                    requestBody.put("n_100",n100.getText().toString());
                    requestBody.put("n_50",n50.getText().toString());
                    requestBody.put("n_20",n20.getText().toString());
                    requestBody.put("n_10",n10.getText().toString());
                    String createdBy=creator.getText().toString();
                    String bitsID=BitsID.getText().toString();
                    if (createdBy.equals("")){
                        createdBy="DLE";
                    }
                    requestBody.put("bits_id",bitsID);
                    requestBody.put("created_by",createdBy);
                    Log.e("Request",requestBody.toString());
                    addProf(requestBody);
                }catch (JSONException e){

                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Prof Error", anError.getErrorBody());
            }
        });
    }

    void addProf(JSONObject auths) {
        Log.d("Request", auths.toString());
        AndroidNetworking.post(URLS.addProf).addHeaders("Authorization", "JWT " + token)
                .addJSONObjectBody(auths).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                imageView.setImageResource(R.drawable.correct);
            }

            @Override
            public void onError(ANError anError) {
                Log.d("Error in Add", anError.getErrorBody());
                imageView.setImageResource(R.drawable.cancel);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        final JSONObject authDetails = new JSONObject();
        try {
            authDetails.put("username", adminName);
            authDetails.put("password", adminPass);
        } catch (JSONException e) {
            Log.d("AuthDetails", e.getStackTrace().toString());
        }
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled scan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                 contents = result.getContents();
                getToken(authDetails);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
