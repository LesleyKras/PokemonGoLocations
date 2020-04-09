package com.example.pokemongolocations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class FormActivity extends AppCompatActivity {
    private Button backButton;
    private Button submitButton;
    private TextView textView;
    RequestQueue RequestQueue;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        backButton = (Button) findViewById(R.id.back_button);
        submitButton = (Button) findViewById(R.id.submit_button);

        textView = findViewById(R.id.apitest);
        textView.setMovementMethod(new ScrollingMovementMethod());

        // RequestQueue For Handle Network Request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        setUrl("https://pokeapi.co/api/v2/pokemon?limit=151");

        getApiData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hier komt de actie om data op te slaan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void getApiData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    textView.setText("Response : " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText("Response : " + error.toString());
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void back() {
        this.finish();
    }
}