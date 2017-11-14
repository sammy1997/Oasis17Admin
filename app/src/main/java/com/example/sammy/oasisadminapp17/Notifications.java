package com.example.sammy.oasisadminapp17;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sammy.oasisadminapp17.CONSTANTS.notificationKey;

public class Notifications extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        final EditText notice=(EditText)findViewById(R.id.editText);
        Button push=(Button)findViewById(R.id.button2);
        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noticeBody=notice.getText().toString();
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject body=new JSONObject();
                    body.put("to","/topics/all");
                    JSONObject message=new JSONObject();
                    message.put("message",noticeBody);
                    body.put("data",message);
                    notification(body);
                }catch (JSONException e){

                }
            }
        });
    }
    void notification(JSONObject body){
        AndroidNetworking.post(URLS.notify).addHeaders("Content-Type","application/json")
                .addHeaders("Authorization",notificationKey).addJSONObjectBody(body).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Notification Response",response.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            if (response.get("message_id")!=null){
                                Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("Notification Error",anError.toString());
                    }
                });
    }
}
