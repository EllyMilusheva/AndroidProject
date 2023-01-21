package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {

    private EditText titleEdt, descriptionEdt;

    private String location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        DBHandler database = new DBHandler(this);

        Intent intent = getIntent();
        double lon = intent.getDoubleExtra("longitude", 0);
        double lat = intent.getDoubleExtra("latitude", 0);

        Toast toast = Toast.makeText(
                this,
                String.format("Lon: %d & Lat: %d", (int)lon, (int)lat),
                Toast.LENGTH_LONG);
        toast.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://location-to-address.p.rapidapi.com/v1/geocode/reverse?lon=" + lon + "&lat=" + lat + "&limit=1&lang=en")
                            .get()
                            .addHeader("X-RapidAPI-Key", "")//your key, please register to rapidapi
                            .addHeader("X-RapidAPI-Host", "location-to-address.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();
                    String body = response.body().string();

                    String  regex   = "\"name\":\"(.*?)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(body);

                    if(matcher.find()) {
                        location = matcher.group(1);
                    } else {
                        regex   = "\"country\":\"(.*?)\"";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(body);
                        if(matcher.find()) {
                            location = matcher.group(1);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error while calling the API.");
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        MaterialButton button = findViewById(R.id.savebtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    thread.join();

                    titleEdt               = findViewById(R.id.titleinput);
                    descriptionEdt         = findViewById(R.id.descriptioninput);
                    Note note = new Note(
                            titleEdt.getText().toString(),
                            descriptionEdt.getText().toString(),
                            location);

                    database.insert(note);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
