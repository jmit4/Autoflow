package com.example.sopas;

import android.content.Intent; // Importar Intent
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Importar Button
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InventarioFragment extends Fragment {

    private DatabaseReference dbRef; // Firebase Realtime Database reference

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_inventario, container, false);

        // Inicializar Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("products");

        // Encontrar el TableLayout en el layout
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

        // Encontrar el botón de Registrar
        Button btnRegistrar = rootView.findViewById(R.id.Registrar);

        // Agregar un OnClickListener al botón de Registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la actividad AgregarProducto
                Intent intent = new Intent(getActivity(), AgregarProducto.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        Button Estadisticas = rootView.findViewById(R.id.Estadisticas);

        // Agregar un OnClickListener al botón de Registrar
        Estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la actividad AgregarProducto
                Intent intent = new Intent(getActivity(), IAMainActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        // Obtener datos de Realtime Database y rellenar la tabla
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Obtener datos del producto
                    String barcode = productSnapshot.child("barcode").getValue(String.class);
                    String name = productSnapshot.child("name").getValue(String.class);
                    String quantity = productSnapshot.child("quantity").getValue().toString();
                    String price = productSnapshot.child("price").getValue().toString();

                    // Crear una nueva fila de tabla
                    TableRow row = new TableRow(getActivity());

                    // Crear TextViews para cada columna
                    TextView barcodeTextView = new TextView(getActivity());
                    barcodeTextView.setText(barcode);
                    barcodeTextView.setPadding(8, 8, 8, 8);

                    TextView nameTextView = new TextView(getActivity());
                    nameTextView.setText(name);
                    nameTextView.setPadding(8, 8, 8, 8);

                    TextView quantityTextView = new TextView(getActivity());
                    quantityTextView.setText(quantity);
                    quantityTextView.setPadding(8, 8, 8, 8);

                    TextView priceTextView = new TextView(getActivity());
                    priceTextView.setText("$" + price);
                    priceTextView.setPadding(8, 8, 8, 8);

                    // Agregar TextViews a la fila
                    row.addView(barcodeTextView);
                    row.addView(nameTextView);
                    row.addView(quantityTextView);
                    row.addView(priceTextView);

                    // Agregar la fila a la TableLayout
                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RealtimeDatabase", "Error getting data: " + databaseError.getMessage());
            }
        });

        return rootView;
    }
}
