package com.example.b10709053_hw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Add extends AppCompatActivity {
    private EditText etName;
    private EditText etNumber;
    private MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        etName = (EditText) findViewById(R.id.name);
        etNumber = (EditText) findViewById(R.id.number);
        if(helper == null){
            helper = new MySQLiteOpenHelper(this);
        }
    }

    public void insert(View view){
        String name = etName.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        if(name.length() <= 0 || number.length() <=0) {
            Toast.makeText(this, "Name or number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        Reservation reservation = new Reservation(name, number);
        long rowId = helper.insert(reservation);
        if(rowId != 1){
            Toast.makeText(this, "Insert success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Insert fail", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void cancel(View view){
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(helper != null){
            helper.close();
        }
    }
}
