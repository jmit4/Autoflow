package com.example.sopas;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class AgregarProducto extends AppCompatActivity {
    EditText eTxtProducto;
    ImageButton imgbtnScaner;
    ImageButton imgbtnSave;
    EditText eTxtNombre;
    EditText eTxtNCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_producto);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        eTxtProducto = findViewById(R.id.eTxtProducto);
        imgbtnScaner = findViewById(R.id.imgbtnScaner);
        imgbtnSave = findViewById(R.id.imgbtnSave);
        eTxtNombre = findViewById(R.id.eTxtNombreP);
        eTxtNCantidad = findViewById(R.id.eTxtNCantidad);

        imgbtnScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(AgregarProducto.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("Escanea el producto");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });
        imgbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String producto = eTxtProducto.getText().toString().trim();
                String nombre = eTxtNombre.getText().toString().trim();
                int cantidad = Integer.parseInt(eTxtNCantidad.getText().toString().trim());
                // Create a new user with a first and last name
                Map<String, Object> prod = new HashMap<>();
                prod.put("Codigo", producto);
                prod.put("Nombre", nombre);
                prod.put("Cantidad", cantidad);

// Add a new document with a generated ID
                db.collection("Productos")
                        .add(prod)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }
        });


    }

    protected void onActivityResult(int resquestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(resquestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG);
            }else{
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG);
                eTxtProducto.setText(result.getContents());
            }
        }else {
            super.onActivityResult(resquestCode, resultCode, data);
        }
    }
}