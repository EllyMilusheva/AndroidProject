package com.example.androidproject;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import  android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {



        private final static int ALL_PERMISSIONS_RESULT = 101;

        private ArrayList<String> permissionsToRequest;
        private ArrayList<String> permissionsRejected = new ArrayList<>();
        private ArrayList<String> permissions         = new ArrayList<>();

        private LocationTrack  locationTrack;
        private MaterialButton button;

        private DBHandler database;


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            database = new DBHandler(this);

            ArrayList<Note> notes = database.read();
            List<String> titles   = notes
                    .stream()
                    .map(Note::getTitle)
                    .collect(Collectors.toList());

            String[] raw_titles = new String[titles.size()];
            for(int i = 0; i < raw_titles.length; i++)
                raw_titles[i] = titles.get(i);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.activity_list_view, raw_titles);

            ListView listView = findViewById(R.id.noteListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                    intent.putExtra("title", notes.get(position).getTitle());
                    intent.putExtra("description", notes.get(position).getDescription());
                    intent.putExtra("location", notes.get(position).getLocation());
                    startActivity(intent);
                }
            });

            permissions.add(ACCESS_FINE_LOCATION);
            permissions.add(ACCESS_COARSE_LOCATION);

            permissionsToRequest = findUnAskedPermissions(permissions);
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(
                                new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);

            button = (MaterialButton) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    locationTrack = new LocationTrack(MainActivity.this);

                    if (locationTrack.canGetLocation()) {

                        double longitude = locationTrack.getLongitude();
                        double latitude  = locationTrack.getLatitude();

                        Intent intent = new Intent(MainActivity.this, AddActivity.class);
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("latitude", latitude);
                        startActivity(intent);

                    } else locationTrack.showSettingsAlert();

                }
            });

        }

        @Override
        protected void onResume() {
            super.onResume();

            ArrayList<Note> notes = database.read();
            List<String> titles   = notes
                    .stream()
                    .map(Note::getTitle)
                    .collect(Collectors.toList());

            String[] raw_titles = new String[titles.size()];
            for(int i = 0; i < raw_titles.length; i++)
                raw_titles[i] = titles.get(i);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, R.layout.activity_list_view, raw_titles);

            ListView listView = findViewById(R.id.noteListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                    intent.putExtra("title", notes.get(position).getTitle());
                    intent.putExtra("description", notes.get(position).getDescription());
                    intent.putExtra("location", notes.get(position).getLocation());
                    startActivity(intent);
                }
            });

        }

        private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
            ArrayList<String> result = new ArrayList<>();
            for (String perm : wanted)
                if (!hasPermission(perm))
                    result.add(perm);
            return result;
        }

        private boolean hasPermission(String permission) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == ALL_PERMISSIONS_RESULT) {

                for (String perms : permissionsToRequest)
                    if (!hasPermission(perms))
                        permissionsRejected.add(perms);

                if (permissionsRejected.size() > 0) {

                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        showMessageOKCancel(
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]),
                                                ALL_PERMISSIONS_RESULT);
                                    }
                                });
                    }
                }
            }
        }


        private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("These permissions are mandatory for the application. Please allow access.")
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            locationTrack.stopListener();
        }
}