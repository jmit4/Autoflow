package com.example.sopas;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class TareasFragment extends Fragment {
    Button agregarTareaButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas, container, false);

        // Find the button by its ID
        agregarTareaButton = view.findViewById(R.id.button);

        // Set an onClickListener for the button
        agregarTareaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the Agregartarea activity
                Intent intent = new Intent(getActivity(), agregartarea.class); // Replace with your activity class name
                startActivity(intent);
            }
        });

        return view;
    }
}