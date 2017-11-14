package com.example.sammy.oasisadminapp17;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

public class ProfsChoice1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Spinner spinner;
    String selectedItem;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedItem=parent.getItemAtPosition(position).toString();
        Log.d("Selected",selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profs_choice1);
        spinner=(Spinner)findViewById(R.id.select_profs);
        spinner.setOnItemSelectedListener(this);
        final Button next=(Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=getApplicationContext().getSharedPreferences("SelectItem",0);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("SelectedItem",selectedItem);
                editor.commit();
                Intent intent=new Intent(getApplicationContext(),ValidatePage.class);
                startActivity(intent);
            }
        });
    }
}
