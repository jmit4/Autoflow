package com.example.sopas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // Botón de Registrar
        Button btnRegistrar = rootView.findViewById(R.id.Registrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AgregarProducto.class);
                startActivity(intent);
            }
        });

        // Botón de Estadísticas
        Button Estadisticas = rootView.findViewById(R.id.Estadisticas);
        Estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IAMainActivity.class);
                startActivity(intent);
            }
        });

        // Botón de Imprimir
        Button Imprimir = rootView.findViewById(R.id.imprimir);
        Imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Consultar todos los productos en Firebase
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Product> sinCategoria = new ArrayList<>();
                        List<Product> farmacia = new ArrayList<>();
                        List<Product> ferreteria = new ArrayList<>();
                        List<Product> tienda = new ArrayList<>();

                        List<Product> todosLosProductos = new ArrayList<>();  // Lista para JSON

                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            // Obtener datos de cada producto
                            String barcode = productSnapshot.child("barcode").getValue(String.class);
                            String name = productSnapshot.child("name").getValue(String.class);
                            String quantity = productSnapshot.child("quantity").getValue().toString();
                            String price = productSnapshot.child("price").getValue().toString();
                            String category = productSnapshot.child("category").getValue(String.class); // Obtener categoría

                            // Crear un objeto Product
                            Product product = new Product(barcode, name, quantity, price, category);

                            // Agregar a la lista general para JSON
                            todosLosProductos.add(product);

                            // Clasificar productos por categoría
                            if (category == null || category.isEmpty()) {
                                sinCategoria.add(product);
                            } else if (category.equalsIgnoreCase("Farmacia")) {
                                farmacia.add(product);
                            } else if (category.equalsIgnoreCase("Ferretería")) {
                                ferreteria.add(product);
                            } else if (category.equalsIgnoreCase("Tienda")) {
                                tienda.add(product);
                            }
                        }

                        if (todosLosProductos.isEmpty()) {
                            Toast.makeText(getActivity(), "No hay productos en la base de datos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Calcular el total de los productos
                        int total = calcularTotal(sinCategoria, farmacia, ferreteria, tienda);

                        // Generar el contenido del ticket para impresión
                        String ticketContent = generatePrintContent(sinCategoria, farmacia, ferreteria, tienda, total);

                        // Convertir los productos a JSON para el correo
                        Gson gson = new Gson();
                        String ticketJson = gson.toJson(todosLosProductos);

                        // Enviar el ticket en formato JSON por correo
                        enviarTicketPorCorreo(ticketJson);

                        // Imprimir el ticket en formato de texto
                        imprimirTicket(ticketContent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("RealtimeDatabase", "Error getting data: " + databaseError.getMessage());
                    }
                });
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
    private void imprimirTicket(String ticketContent) {
        BluetoothPrint bluetoothPrint = new BluetoothPrint(getActivity());

        // Abrir la conexión Bluetooth
        bluetoothPrint.openBluetoothPrinterWithPermissionCheck();

        // Imprimir el contenido del ticket con tamaño de fuente pequeño
        bluetoothPrint.printData(ticketContent, true);
    }




    private void enviarTicketPorCorreo(String ticketJson) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:diegoaragonia@gmail.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ticket de Compra (Formato JSON)");
        intent.putExtra(Intent.EXTRA_TEXT, "Aquí tienes tu ticket en formato JSON:\n\n" + ticketJson);

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show();
        }
    }





    // Método para generar el contenido del ticket de impresión
    private String generatePrintContent(List<Product> sinCategoria, List<Product> farmacia, List<Product> ferreteria, List<Product> tienda, int total) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String horaActual = dateFormat.format(new Date());

        StringBuilder contentBuilder = new StringBuilder();
        String espacioCentrado = "       "; // Para centrar el contenido

        // Encabezado del ticket
        contentBuilder.append(espacioCentrado).append("AutoFlow").append("\n\n");
        contentBuilder.append("Fecha: ").append(horaActual).append("\n");
        contentBuilder.append("==============================\n");
        contentBuilder.append("Productos en inventario:").append("\n\n");

        // Productos Sin Categoría
        if (!sinCategoria.isEmpty()) {
            contentBuilder.append("Sin Categoría:\n");
            for (Product producto : sinCategoria) {
                contentBuilder.append(producto.getName()).append("  x").append(producto.getQuantity())
                        .append("   $").append(producto.getPrice()).append("\n");
            }
            contentBuilder.append("\n");
        }

        // Productos de Farmacia
        if (!farmacia.isEmpty()) {
            contentBuilder.append("Farmacia:\n");
            for (Product producto : farmacia) {
                contentBuilder.append(producto.getName()).append("  x").append(producto.getQuantity())
                        .append("   $").append(producto.getPrice()).append("\n");
            }
            contentBuilder.append("\n");
        }

        // Productos de Ferretería
        if (!ferreteria.isEmpty()) {
            contentBuilder.append("Ferretería:\n");
            for (Product producto : ferreteria) {
                contentBuilder.append(producto.getName()).append("  x").append(producto.getQuantity())
                        .append("   $").append(producto.getPrice()).append("\n");
            }
            contentBuilder.append("\n");
        }

        // Productos de Tienda
        if (!tienda.isEmpty()) {
            contentBuilder.append("Tienda:\n");
            for (Product producto : tienda) {
                contentBuilder.append(producto.getName()).append("  x").append(producto.getQuantity())
                        .append("   $").append(producto.getPrice()).append("\n");
            }
            contentBuilder.append("\n");
        }

        // Pie de página del ticket con el total
        contentBuilder.append("==============================\n");
        contentBuilder.append("Total: $").append(total).append("\n");
        contentBuilder.append("==============================\n");

        // Añadir la recomendación aleatoria de IA en el pie del ticket
        contentBuilder.append(generateRandomRecommendation());

        return contentBuilder.toString();
    }


    private String generateRandomRecommendation() {
        String[] mensajes = {
                "Hola, soy S.O.P.A.S., el Sistema Optimizado de Productos y Almacenamiento Sopas.",
                "S.O.P.A.S. al servicio: veo que has comprado mucha cinta adhesiva la semana pasada. ¡Podrías pedir menos para evitar productos estancados!",
                "Recuerda, S.O.P.A.S. recomienda hacer una revisión de tu inventario cada semana para evitar exceso de stock.",
                "Parece que tienes muchos productos repetidos, ¿qué tal si pruebas a diversificar más tu inventario?",
                "¡S.O.P.A.S. te recomienda mantener en orden tu inventario para maximizar tus ganancias!",
                "¿Necesitas optimizar tu stock? ¡S.O.P.A.S. está aquí para ayudarte a evitar que se te acumulen productos!",
                "Hola, soy S.O.P.A.S., el sistema que optimiza tus pedidos. ¡No olvides revisar tus productos antes de hacer el siguiente pedido!",
                "¡S.O.P.A.S. te recuerda que mantener un equilibrio en tus existencias es clave para evitar desperdicios y maximizar ganancias!",
                "¡Parece que te estás quedando sin espacio! Considera revisar los productos que llevan más tiempo en tu inventario.",
                "¿Sabías que los productos con rotación rápida pueden aumentar tus ingresos? ¡Deja que S.O.P.A.S. te ayude a gestionarlos mejor!",
                "¡Veo que has comprado varios desodorantes en barra! Recuerda que mantener tu inventario de productos de higiene personal al día es clave para que tus clientes siempre encuentren lo que necesitan.",
                "¡S.O.P.A.S. te sugiere estar atento al inventario de gel antibacterial! Con la alta demanda de productos de higiene, mantener suficientes existencias puede evitarte faltantes.",
                "Parece que tienes mucho stock de productos como desodorantes y gel antibacterial. Tal vez podrías ajustar tus pedidos para evitar que se acumulen demasiado."
        };

        Random random = new Random();
        int index = random.nextInt(mensajes.length);
        return mensajes[index] + "\n";
    }
    private int calcularTotal(List<Product> sinCategoria, List<Product> farmacia, List<Product> ferreteria, List<Product> tienda) {
        int total = 0;

        // Sumar precios de cada categoría
        for (Product producto : sinCategoria) {
            total += Integer.parseInt(producto.getPrice()) * Integer.parseInt(producto.getQuantity());
        }

        for (Product producto : farmacia) {
            total += Integer.parseInt(producto.getPrice()) * Integer.parseInt(producto.getQuantity());
        }

        for (Product producto : ferreteria) {
            total += Integer.parseInt(producto.getPrice()) * Integer.parseInt(producto.getQuantity());
        }

        for (Product producto : tienda) {
            total += Integer.parseInt(producto.getPrice()) * Integer.parseInt(producto.getQuantity());
        }

        return total;
    }


}
