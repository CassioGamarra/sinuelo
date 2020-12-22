package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent it = getIntent();
        token = it.getSerializableExtra("token").toString();
    }

    public void abrirRegistro(View v) {
        Intent it = new Intent(this, RegisterActivity.class);
        it.putExtra("token", token);
        startActivity(it);
    }

    public void abrirBusca(View v) {
        Intent it = new Intent(this, DetailActivity.class);
        it.putExtra("token", token);
        startActivity(it);
    }

}