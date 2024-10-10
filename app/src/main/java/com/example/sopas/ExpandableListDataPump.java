package com.example.sopas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData(){
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();
        List<String> pregunta1 = new ArrayList<>(); // Cómo hacer una tarea
        pregunta1.add("1 Abre el menú lateral");
        pregunta1.add("2 Selecciona 'Tareas'");
        pregunta1.add("3 Agrega un título, un asignado y una fecha de finalzación");

        List<String> pregunta2 = new ArrayList<>(); // Como añadir un producto a inventario
        pregunta2.add("1 Abre el menú lateral");
        pregunta2.add("2 Selecciona 'Inventario'");
        pregunta2.add("3 Escanea el código de barras");
        pregunta2.add("4 Llena la información solicitada");

        List<String> pregunta3 = new ArrayList<>(); // Como añadir a un empleado

        pregunta3.add("1 Abre el menú lateral");
        pregunta3.add("2 Selecciona 'Empleados'");
        pregunta3.add("3 Llena su información");

        expandableListDetail.put("¿Cómo creo una nueva tarea?", pregunta1);
        expandableListDetail.put("¿Cómo añado un producto a mi inventario?", pregunta2);
        expandableListDetail.put("¿Cómo agrego un empleado a mi plantilla?", pregunta3);

        return expandableListDetail;

    }
}
