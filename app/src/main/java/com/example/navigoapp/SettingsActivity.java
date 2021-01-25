package com.example.navigoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private TextView fname;
    private TextView lname;
    private TextView email;
    private TextView units;
    private RadioGroup radioGroup;
    private RadioButton btn_units;
    private RadioButton btn_metric;
    private RadioButton btn_imperial;
    private String unit_setting;
    private String Settings;

    private Button btn_sBack;
    private Button btn_sUpdate;
    private ImageButton btnMenu;

    // database
    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fname = (TextView) findViewById(R.id.profile_name);
        lname = (TextView) findViewById(R.id.profile_surname);
        email = (TextView) findViewById(R.id.profile_email);
        units = (TextView) findViewById(R.id.profile_units);
        radioGroup = (RadioGroup) findViewById(R.id.btnGroup_units);
        btn_metric = (RadioButton) findViewById(R.id.btn_metric);
        btn_imperial = (RadioButton) findViewById(R.id.btn_imperial);

        btn_sBack = (Button) findViewById(R.id.btn_sBack);
        btn_sUpdate = (Button) findViewById(R.id.btn_sUpdate);
        btnMenu = (ImageButton) findViewById(R.id.btn_nav2);

        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User").child(userID);

        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Firstname = snapshot.child("Name").getValue().toString();
                String Lastname = snapshot.child("Surname").getValue().toString();
                String Email = snapshot.child("Email").getValue().toString();
                Settings = snapshot.child("Settings").getValue().toString();

                fname.setText(Firstname);
                lname.setText(Lastname);
                email.setText(Email);
                units.setText(Settings);
                //String user_units = Settings;

                if(Settings.equals("Metric")){
                    btn_metric.setChecked(true);
                    btn_imperial.setChecked(false);
                } else {
                    btn_metric.setChecked(false);
                    btn_imperial.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
                btn_units = (RadioButton) findViewById(checkID);
                switch (btn_units.getId()){
                    case R.id.btn_metric: {
                        if(btn_units.isChecked()){
                            unit_setting = "Metric";
                        }
                    }
                    break;
                    case R.id.btn_imperial: {
                        if(btn_units.isChecked()){
                            unit_setting = "Imperial";
                        }
                    }
                    break;
                }
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(SettingsActivity.this, btnMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu2, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_back:
                                startActivity(new Intent(SettingsActivity.this, MapActivity.class));
                                return true;
                            case R.id.menu_logout:
                                mAuth.signOut();
                                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                                return true;
                            default:
                                return false;

                        }
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        btn_sUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference currentUser = mDBRef;

                if(unit_setting != Settings){
                    currentUser.child("Settings").setValue(unit_setting);
                    Toast.makeText(SettingsActivity.this, "Settings update was successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "No settings changed", Toast.LENGTH_SHORT).show();
                }


            }
        });

        btn_sBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MapActivity.class));
            }
        });
    }
}