package com.example.sinuelo;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class PairedDevicesActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    public ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_devices);
        ListView listaDispositivos = (ListView) findViewById(R.id.listaDispositivos);
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.text_header, listaDispositivos, false);
        ((TextView) header.findViewById(R.id.textView)).setText("\nDispositivos próximos\n");
        listaDispositivos.addHeaderView(header, null, false);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listaDispositivos.setAdapter(arrayAdapter);

        listaDispositivos.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                        String item = arrayAdapter.getItem(position-1);
                        String devName = item.substring(0, item.indexOf("\n"));
                        String devAddress = item.substring(item.indexOf("\n")+1, item.length());
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("btDevName", devName);
                        returnIntent.putExtra("btDevAddress", devAddress);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }
        );

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
            Toast.makeText(PairedDevicesActivity.this, "Ativando Bluetooth...", Toast.LENGTH_SHORT).show();
        } else {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                arrayAdapter.add("Nenhum dispositivo pareado...");
            }
        }
    }
}