package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    String token = "";
    String nomeDispositivo = "";
    String enderecoDispositivo = "";
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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

    public void conexoesBluetooh(View v) {
        Intent searchPairedDevicesIntent = new Intent(this, PairedDevicesActivity.class);
        startActivityForResult(searchPairedDevicesIntent, 2); //SELECT_PAIRED_DEVICE
    }

    public void gerenciarBastao(View v) {
        if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(MainActivity.this, "Ative seu bluetooth", Toast.LENGTH_SHORT).show();
        } else {
             if (nomeDispositivo.equals("") || enderecoDispositivo.equals("")) {
                 Toast.makeText(MainActivity.this, "Nenhum dispositivo conectado!!!", Toast.LENGTH_SHORT).show();
             } else if(!nomeDispositivo.equals("HC-06")) {
                 Toast.makeText(MainActivity.this, "Por favor, selecione o bastão: HC-06", Toast.LENGTH_SHORT).show();
             } else {
                 if(conectarDispositivo(enderecoDispositivo)) {
                     Intent it = new Intent(this, HandheldStickActivity.class);
                     it.putExtra("status", "Conectado");
                     startActivity(it);
                 } else {
                     Toast.makeText(MainActivity.this, "Ocorreu um erro ao conectar", Toast.LENGTH_SHORT).show();
                 }
             }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) { //SELECT_PAIRED_DEVICE
            if (resultCode == RESULT_OK) {
                String msg = "Você selecionou " + data.getStringExtra("btDevName") + "\n" + data.getStringExtra("btDevAddress");
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                nomeDispositivo = data.getStringExtra("btDevName");
                enderecoDispositivo = data.getStringExtra("btDevAddress");
            } else {
                Toast.makeText(MainActivity.this, "Nenhum dispositivo selecionado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean conectarDispositivo(String enderecoDispositivo) {
        BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(enderecoDispositivo);
        BluetoothSocket btSocket;
        try{
            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));
        } catch(Exception e){
            return false;
        }
        bluetoothAdapter.cancelDiscovery();
        try{
            if(btSocket != null) {
                btSocket.connect();
                return true;
            } else {
                return false;
            }
        } catch(IOException connectEcption){
            try{
                btSocket.close();
            }catch(IOException closEception){
                return false;
            }
        }
        return false;
    }

}