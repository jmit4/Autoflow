package com.example.sopas;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class agregartarea extends AppCompatActivity {
    EditText tv, etxtnomb, etxtfech, etxtnemp;
    Button btnCalendar;
    Button btnSaveTask;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregartarea);
        tv = findViewById(R.id.dispFech);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        etxtnomb = findViewById(R.id.eTxtNombreTask);
        etxtfech = findViewById(R.id.dispFech);
        etxtnemp = findViewById(R.id.eTxtNEmp);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCalendario(view);
            }
        });
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = etxtnomb.getText().toString().trim();
                String fecha = tv.getText().toString().trim();
                String nemp = etxtnemp.getText().toString().trim();
                Map<String, Object> tareas = new HashMap<>();
                tareas.put("nombre", nombre);
                tareas.put("encargado", nemp);
                tareas.put("fecha", fecha);

                if (nombre.isEmpty() || fecha.isEmpty() || nemp.isEmpty() ) {
                    Toast.makeText(agregartarea.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

// Add a new document with a generated ID
                db.collection("tareas")
                        .add(tareas)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(agregartarea.this, "Guardado correctamente", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(agregartarea.this, "Error", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
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