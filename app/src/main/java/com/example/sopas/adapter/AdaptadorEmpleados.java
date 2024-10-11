package com.example.sopas.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sopas.R;
import com.example.sopas.empleado;

import java.util.List;


public class AdaptadorEmpleados extends RecyclerView.Adapter<AdaptadorEmpleados.Myholder>{
    private Context context;
    private List<empleado> empleadoss;

    public Adaptador(Context context, List<empleado> empleados) {
        this.context = context;
        empleadoss= empleados;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //INFLAR EL ADMIN_LAYOUD

        View view = LayoutInflater.from(context).inflate(R.layout.empleado_item,parent,false);
        return new Myholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        empleado empleado = empleadoss.get(position);
        holder.CorreoADMIN.setText(planta.getTIPO());
        holder.ApodoPlantas.setText(planta.getAPODO());

        //OBTENEMOS LOS DATOS DEL MODELO
        String UID = plantass.get(position).getUid();
        String APODO = plantass.get(position).getAPODO();
        String TIPO = plantass.get(position).getTIPO();


        //SETEO DE DATOS

        holder.ApodoPlantas.setText(APODO);
        holder.CorreoADMIN.setText(TIPO);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, detalleAdministrador.class);
                intent.putExtra("UID",UID);
                intent.putExtra("APODO",APODO);
                intent.putExtra("TIPO",TIPO);
                context.startActivity(intent);
            }
        });
//        //AL HACER CLICK EN UN ADMIN
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context, Detalle_Administrador.class);
//                //Pasar datos a la siguiente actividad
//                intent.putExtra("UID",UID);
//                intent.putExtra("NOMBRES", NOMBRES);
//                intent.putExtra("APELLIDOS",APELLIDOS);
//                intent.putExtra("CORREO",CORREO);
//                intent.putExtra("EDAD",EdadString);
//                intent.putExtra("Imagen",Imagen);
//                context.startActivity(intent);
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return empleadoss.size();

    }

    public  class Myholder extends RecyclerView.ViewHolder{


        //DECLARAMOS LAS VISTAS
        TextView nombreEmpleado;


        public Myholder(@NonNull View itemView) {
            super(itemView);

            ApodoPlantas = itemView.findViewById(R.id.ApodoPlantas);
            CorreoADMIN = itemView.findViewById(R.id.CorreoADMIN);
        }
    }
}