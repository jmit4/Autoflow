package com.example.sopas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Estadisticas extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);


        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        // Obtener referencia a Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("products");

        // Consultar Firebase y generar información para enviar a ChatGPT
        consultaFirebaseYEnviarAChatGPT();
    }

    // Consulta Firebase y envía la información a ChatGPT
    private void consultaFirebaseYEnviarAChatGPT() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Almacenar la información que se va a enviar
                StringBuilder productosInfo = new StringBuilder();

                // Iterar sobre los productos en la base de datos
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String nombre = productSnapshot.child("name").getValue(String.class);
                    int cantidad = productSnapshot.child("quantity").getValue(Integer.class);
                    double precio = productSnapshot.child("price").getValue(Double.class);
                    String fechaRegistro = productSnapshot.child("purchase_date").getValue(String.class);

                    // Añadir la información de cada producto
                    productosInfo.append("Producto: ").append(nombre)
                            .append(", Cantidad: ").append(cantidad)
                            .append(", Precio: $").append(precio)
                            .append(", Fecha de Registro: ").append(fechaRegistro)
                            .append("\n");
                }

                // Llamar a la API con la información de los productos
                callAPI(productosInfo.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                addResponse("Error consultando Firebase: " + error.getMessage());
            }
        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callAPI(String infoProductos) {
        messageList.add(new Message("Obteniendo recomendaciones...", Message.SENT_BY_BOT));

        // Construir la solicitud JSON
        String json = "{ \"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \"" + infoProductos.replace("\"", "\\\"") + "\"}] }";


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        addResponse("Error parsing response: " + e.getMessage());
                    }
                } else {
                    addResponse("Error: " + response.code() + " " + response.message());
                }
            }
        });
    }
}
