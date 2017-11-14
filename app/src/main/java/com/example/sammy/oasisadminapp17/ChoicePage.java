package com.example.sammy.oasisadminapp17;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChoicePage extends AppCompatActivity {

    private String password;
    TextView tv;
    String username;
    String checkName="DLE";
    String checkPass="department12345";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dole:
                    checkName="DLE";
                    checkPass="department12345";
                    tv.setText("Dept. of Live Events");
                    return true;
                case R.id.navigation_controlz:
                    checkName="controls";
                    checkPass="department123";
                    tv.setText("Dept. of Controls");
                    return true;
                case R.id.navigation_ems:
                    checkPass="EMS";
                    checkName="EMS";
                    tv.setText("Event Management System");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_page);
        tv=(TextView)findViewById(R.id.textView2);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation1);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Button signIn=(Button)findViewById(R.id.signIn);
        final EditText user=(EditText)findViewById(R.id.username);
        final EditText pass=(EditText)findViewById(R.id.password);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                username=user.getText().toString().trim();
                password=pass.getText().toString().trim();
                if (username.equals(checkName) && password.equals(checkPass)){
                    Toast.makeText(getApplicationContext(),"Logged in as Dept:" + username,Toast.LENGTH_SHORT).show();
                    if (username.equals("DLE")){
                        intent=new Intent(getApplicationContext(),AddValidateProf.class);

                    }
                    else{
                        intent=new Intent(getApplicationContext(),Notifications.class);
                    }
                    startActivity(intent);
                }
                else if(checkName.equals("EMS")){
                    intent=new Intent(getApplicationContext(),EMSPage.class);
                    intent.putExtra("Username",username);
                    intent.putExtra("Password",password);
                    startActivity(intent);
                }
                else {
                    Log.d("Username",checkName);
                    Log.d("Password",checkPass);
                    Toast.makeText(getApplicationContext(),"Failed to login. Enter correct password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
