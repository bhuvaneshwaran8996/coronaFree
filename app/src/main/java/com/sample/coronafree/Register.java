package com.sample.coronafree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText fullName, email, pass, phoneNo;
    Button registerButton;
    TextView login;
    FirebaseAuth fAuth;
    ProgressBar progressbar;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpref;
    Switch aSwitch;
    Spinner spinner;
    String place = "";

    boolean affected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //location permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        sharedpref = this.getSharedPreferences("COVID_DATA", MODE_PRIVATE);
        editor = sharedpref.edit();
        fullName = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.registerPassword);
        phoneNo = findViewById(R.id.phoneNo);
        registerButton = findViewById(R.id.registerBtn);
        login = findViewById(R.id.alreadyRegister);
        progressbar = findViewById(R.id.progressBar);
        aSwitch = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinner);

        fAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        if(fAuth.getCurrentUser() != null){

            editor.putString("isLoggedin","true");
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                place = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    affected = true;
                }else {
                    affected = false;
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid = email.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if(TextUtils.isEmpty(emailid)){
                    email.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    pass.setError("password is required");
                    return;
                }

                if(password.length() < 6){
                    pass.setError("password must be greater than 6 characters");
                    return;
                }

                progressbar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase rootNode;
                            DatabaseReference databaseReference;
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uuid =  user.getUid();
                            //BPJvW8Lj9CeYeNt6Mv8EsfDNkBB2

                            rootNode = FirebaseDatabase.getInstance("https://coronafree-77bad-default-rtdb.firebaseio.com/");


                            databaseReference = rootNode.getReference("user");
                            String name = fullName.getText().toString();


                           // UserHelperClass customer = new UserHelperClass(name, place, affected, 0.0,0.0, uuid, ""); //pojo class

                            UserHelperClass customer = new UserHelperClass();
                            customer.setName(name);
                            customer.setPlace(place);
                            customer.setInfected(affected);
                            customer.setLatitude(0.0);
                            customer.setLatitude(0.0);
                            customer.setId(uuid);
                            customer.setFsm("");






                            databaseReference.child(uuid).setValue(customer);

                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}