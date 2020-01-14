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
import android.widget.TextView;
import android.widget.Toast;

import com.example.styleomegafashionstore.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText Password, Number;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private TextView AdminLink, NotAdminLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_buttn);
        Password = (EditText) findViewById(R.id.login_password_input);
        Number = (EditText) findViewById(R.id.login_number_input);
        AdminLink= (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink= (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }

        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";

            }

        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }



    private void loginUser() {
        String phone = Number.getText().toString();
        String password = Password.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter your Contact Number to continue", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password to continue", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setMessage("Please Hold, we are checking your account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setTitle("Login Account");
            loadingBar.show();

            LoginAccess(phone, password);
        }
    }


    private void LoginAccess(final String phone, final String password) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    Users usersdata = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if(parentDbName.equals("Admins")){

                                Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                            }
                            else if(parentDbName.equals("Users"))
                                {
                                    Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }

                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Password, Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "No Account Found ", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Incorrect number or password", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}



