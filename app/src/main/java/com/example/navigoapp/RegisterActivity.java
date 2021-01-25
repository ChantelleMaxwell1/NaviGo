package com.example.navigoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    // variables
    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText password;

    private RadioGroup radioGroup;
    private RadioButton btn_units;
    private String units;

    private Button register;

    private static final String TAG = "RegisterActivity";

    // database
    private DatabaseReference mDBRef;
    private FirebaseDatabase mDB;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDB = FirebaseDatabase.getInstance();
        mDBRef = mDB.getReference().child("User");

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        name = (EditText) findViewById(R.id.reg_fname);
        surname = (EditText) findViewById(R.id.reg_Lname);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        radioGroup = (RadioGroup) findViewById(R.id.btnGroup_units);
        register = (Button) findViewById(R.id.btn_register);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkID) {
                btn_units = (RadioButton) findViewById(checkID);
                switch (btn_units.getId()){
                    case R.id.btn_metric: {
                        if(btn_units.isChecked()){
                            units = "Metric";
                        }
                    }
                    break;
                    case R.id.btn_imperial: {
                        if(btn_units.isChecked()){
                            units = "Imperial";
                        }
                    }
                    break;
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fname = name.getText().toString().trim();
                final String lname = surname.getText().toString().trim();
                final String mail = email.getText().toString().trim();
                final String pwd = password.getText().toString().trim();
                final String unit = units;

                if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(unit)){
                    mProgressDialog.setMessage("Creating account...");
                    mProgressDialog.show();

                    mAuth.createUserWithEmailAndPassword(mail,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressDialog.dismiss();
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDB = mDBRef.child(userID);

                            currentUserDB.child("Email").setValue(mail);
                            //currentUserDB.child("Password").setValue(pwd);
                            currentUserDB.child("Name").setValue(fname);
                            currentUserDB.child("Surname").setValue(lname);
                            currentUserDB.child("Settings").setValue(unit);

                            //Send users to home activity
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            //finish();
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill in all required information.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}