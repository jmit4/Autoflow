package com.example.sopas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgregarProducto extends AppCompatActivity {
    EditText eTxtProducto, eTxtNombre, eTxtNCantidad, eTxtPrecio;
    Button imgbtnScaner, imgbtnSave;

    // Referencia a la base de datos
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Inicializar Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("products");

        eTxtProducto = findViewById(R.id.eTxtProducto);
        eTxtNombre = findViewById(R.id.eTxtNombreP);
        eTxtNCantidad = findViewById(R.id.eTxtNCantidad);
        eTxtPrecio = findViewById(R.id.precio);
        imgbtnScaner = findViewById(R.id.imgbtnScaner);
        imgbtnSave = findViewById(R.id.imgbtnSave);

        // Escáner de código de barras
        imgbtnScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrador = new IntentIntegrator(AgregarProducto.this);
                integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrador.setPrompt("Escanea el código de barras");
                integrador.setCameraId(0);
                integrador.setBeepEnabled(true);
                integrador.setBarcodeImageEnabled(true);
                integrador.initiateScan();
            }
        });

        // Guardar el producto en la base de datos
        imgbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigo = eTxtProducto.getText().toString().trim();
                String nombre = eTxtNombre.getText().toString().trim();
                String cantidad = eTxtNCantidad.getText().toString().trim();
                String precio = eTxtPrecio.getText().toString().trim();

                if (codigo.isEmpty() || nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty()) {
                    Toast.makeText(AgregarProducto.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Formatear la fecha de registro actual
                String fechaRegistro = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                // Crear un objeto con los datos del producto
                Map<String, Object> producto = new HashMap<>();
                producto.put("barcode", codigo);
                producto.put("name", nombre);
                producto.put("quantity", Integer.parseInt(cantidad));
                producto.put("price", Integer.parseInt(precio));
                producto.put("registration_date", fechaRegistro);  // Fecha de registro

                // Guardar el producto en la base de datos bajo el código de barras
                dbRef.child(codigo).setValue(producto).addOnSuccessListener(aVoid -> {
                    Toast.makeText(AgregarProducto.this, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
                    clearFields(); // Limpiar los campos
                }).addOnFailureListener(e -> {
                    Toast.makeText(AgregarProducto.this, "Error al guardar el producto", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Método para limpiar los campos después de guardar
    private void clearFields() {
        eTxtProducto.setText("");
        eTxtNombre.setText("");
        eTxtNCantidad.setText("");
        eTxtPrecio.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG).show();
            } else {
                eTxtProducto.setText(result.getContents());  // Mostrar el código de barras en el campo de texto
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
