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
    String codigo, nombre, cantidad, precio;

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
        // Guardar el producto en la base de datos
        imgbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigo = eTxtProducto.getText().toString().trim();
                nombre = eTxtNombre.getText().toString().trim();
                cantidad = eTxtNCantidad.getText().toString().trim();
                precio = eTxtPrecio.getText().toString().trim();

                if (codigo.isEmpty() || nombre.isEmpty() || cantidad.isEmpty() || precio.isEmpty() ) {
                    Toast.makeText(AgregarProducto.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if barcode already exists
                checkBarcodeExistence(codigo);
            }
        });
    }

    // Method to check if barcode exists in database
    private void checkBarcodeExistence(String codigo) {
        dbRef.child(codigo).get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                // Error fetching data
                Toast.makeText(AgregarProducto.this, "Error al verificar el código de barras", Toast.LENGTH_SHORT).show();
                return;
            }

            if (task.getResult().exists()) {
                // Obtener el nombre del producto del resultado
                String nombreProducto = task.getResult().child("name").getValue(String.class);

                // Si el nombre del producto está disponible, lo mostramos en el Toast
                if (nombreProducto != null) {
                    Toast.makeText(AgregarProducto.this, "El código de barras ya existe. Producto: " + nombreProducto, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AgregarProducto.this, "El código de barras ya existe.", Toast.LENGTH_SHORT).show();
                }

                eTxtProducto.requestFocus(); // Set focus on barcode field
            } else {
                // El código de barras no existe, proceder con guardar
                saveProduct(codigo, nombre, cantidad, precio);
            }

        });
    }

    // Method to save product (assuming you have a saveProduct method)
    private void saveProduct(String codigo, String nombre, String cantidad, String precio) {
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
