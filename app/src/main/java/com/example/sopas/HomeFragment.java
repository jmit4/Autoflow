package com.example.sopas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class HomeFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Encuentra el botón en la vista del fragmento
        ImageButton btnConsejos = view.findViewById(R.id.btn_consejos);
        ImageButton btnPreguntas = view.findViewById(R.id.btn_questions);

        // Asigna el OnClickListener para manejar el clic en el botón
        btnConsejos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la nueva actividad
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwi06d2v14aJAxWQBUQIHZUKO3MQFnoECBYQAw&url=https%3A%2F%2Fcorposuite.com%2F2019%2F08%2F20%2F10-tips-para-mejorar-la-administracion-de-tu-empresa%2F&usg=AOvVaw3PiR9RyJsgyKiSE-zIA-1e&opi=89978449"));
                startActivity(browserIntent);
            }
        });

        btnPreguntas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el Intent para abrir la nueva actividad
                Intent intent = new Intent(getActivity(), Questions.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

        return view;
    }
}