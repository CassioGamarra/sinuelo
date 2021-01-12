package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.UUID;

public class HandheldStickActivity extends AppCompatActivity {

    TextView tvStatusDispositivo;
    String status, endereco;
 
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld_stick);
        Intent it = getIntent();
        status = it.getSerializableExtra("status").toString();

        tvStatusDispositivo = findViewById(R.id.statusDispositivo);
        tvStatusDispositivo.setText(status);
    }

}