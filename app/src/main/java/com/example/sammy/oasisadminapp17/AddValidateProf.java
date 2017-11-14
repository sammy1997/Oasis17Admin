package com.example.sammy.oasisadminapp17;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddValidateProf extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_validate_prof);
        final Activity activity=this;
        Button addProfShow=(Button)findViewById(R.id.add_prof_show);
        Button validateProfShow=(Button)findViewById(R.id.validate_prof_show);
        addProfShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(activity,AddProfsPage.class);
                startActivity(intent);
            }
        });

        validateProfShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(activity,ProfsChoice1.class);
                startActivity(intent);
            }
        });
    }
}
