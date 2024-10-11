package com.example.sopas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InventarioFragment extends Fragment {

    private FirebaseFirestore db; // Firebase Firestore instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inventario, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find TableLayout in the layout
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

        // Fetch data from Firestore and populate the table
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get product data
                                String barcode = document.getString("barcode");
                                String name = document.getString("name");
                                String quantity = document.get("quantity").toString();
                                String price = document.get("price").toString();

                                // Create a new TableRow
                                TableRow row = new TableRow(getActivity());

                                // Create TextViews for each column
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

                                // Add TextViews to TableRow
                                row.addView(barcodeTextView);
                                row.addView(nameTextView);
                                row.addView(quantityTextView);
                                row.addView(priceTextView);

                                // Add TableRow to TableLayout
                                tableLayout.addView(row);
                            }
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return rootView;
    }
}
