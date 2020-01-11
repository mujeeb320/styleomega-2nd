package com.example.styleomegafashionstore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButtn;
    private EditText Name, Password, Number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButtn =(Button) findViewById(R.id.register_buttn);
        Name =(EditText) findViewById(R.id.register_uname_input);
        Password =(EditText) findViewById(R.id.register_password_input);
        Number =(EditText) findViewById(R.id.register_number_input);



    }
}
