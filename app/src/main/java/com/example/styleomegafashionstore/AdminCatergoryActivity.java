package com.example.styleomegafashionstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCatergoryActivity extends AppCompatActivity {

    private ImageView men, women, kids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_catergory);

        men=(ImageView) findViewById(R.id.men);
        women=(ImageView) findViewById(R.id.women);
        kids=(ImageView) findViewById(R.id.kids);

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCatergoryActivity.this, AdminAddNewProductActivity.class );
                intent.putExtra("category", "men");
                startActivity(intent);
            }
        });

        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCatergoryActivity.this, AdminAddNewProductActivity.class );
                intent.putExtra("category", "women");
                startActivity(intent);
            }
        });

        kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminCatergoryActivity.this, AdminAddNewProductActivity.class );
                intent.putExtra("category", "kids");
                startActivity(intent);
            }
        });

    }
}
