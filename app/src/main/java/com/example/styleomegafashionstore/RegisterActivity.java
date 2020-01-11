package com.example.styleomegafashionstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButtn;
    private EditText Name, Password, Number;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButtn =(Button) findViewById(R.id.register_buttn);
        Name =(EditText) findViewById(R.id.register_uname_input);
        Password =(EditText) findViewById(R.id.register_password_input);
        Number =(EditText) findViewById(R.id.register_number_input);
        loadingBar = new ProgressDialog(this);


        CreateAccountButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();

            }
        });


    }
    private void CreateAccount() {
    String name= Name.getText().toString();
    String phone= Number.getText().toString();
    String password= Password.getText().toString();

    if(TextUtils.isEmpty(name)){
        Toast.makeText(this, "Please enter your Name to continue",Toast.LENGTH_SHORT).show();
    }
    else if(TextUtils.isEmpty(phone)){
        Toast.makeText(this, "Please enter your Contact Number to continue",Toast.LENGTH_SHORT).show();
    }
    else if(TextUtils.isEmpty(password)){
        Toast.makeText(this, "Please enter a password to continue",Toast.LENGTH_SHORT).show();
    }
    else
        {
            loadingBar.setMessage("Please Hold, we are checking your account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setTitle("Create Account");
            loadingBar.show();

            ValidateNumber(name, phone, password);
        }
    }

    private void ValidateNumber(final String name, final String phone, final String pass) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object > userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", pass);
                    userdataMap.put("name", name);

                    Rootref.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);

                                    }else
                                        {
                                            loadingBar.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "This "+ phone + "is already registered", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

