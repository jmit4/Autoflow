package com.example.sopas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class agregartarea extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregartarea);
        tv = findViewById(R.id.dispfecha);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void abrirCalendario(View view) {
        Calendar car = Calendar.getInstance();
        int year = car.get(Calendar.YEAR);
        int month = car.get(Calendar.MONTH);
        int day = car.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
                String fecha = dayofmonth + "/" + month + "/" + year;
                tv.setText(fecha);
                // java.util.Date papu = car.getTime();
            }
        },year, month, day);
        dpg.show();
    }
}