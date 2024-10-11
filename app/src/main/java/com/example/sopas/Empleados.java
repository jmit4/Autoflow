package com.example.sopas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Empleados extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empleados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void ObtenerLista() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection("Usuarios").document(user.getUid()).collection("Plantas");

            reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Toast.makeText(getActivity(), "Error al cargar las plantas: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PLANTAS.clear();
                    if (value != null) {
                        int count = 0;
                        for (QueryDocumentSnapshot ds : value) {
                            if (count >= 10) {  // Verifica si se ha alcanzado el límite antes de agregar más plantas
                                mostrarDialogoSuscripcion();  // Muestra el diálogo cuando se alcanzan 10 ítems
                                break;  // Detiene la adición de más ítems
                            }

                            planta planta = ds.toObject(planta.class);
                            if (planta != null) {
                                Log.d("PLANTA", "APODO: " + planta.getAPODO() + ", TIPO: " + planta.getTIPO());
                                PLANTAS.add(planta);
                                count++;  // Incrementa el contador de plantas agregadas
                            }
                        }

                        if (adaptador == null) {
                            adaptador = new Adaptador(getActivity(), PLANTAS);
                            plantas_recyclerview.setAdapter(adaptador);
                        } else {
                            adaptador.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}