package com.example.bloc_notes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

public class Editeur extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 100;
    private EditText titre;
    private EditText editDetails;
    private Button boutonUndo;
    private Button boutonRedo;
    private Button boutonGPS;
    private Button boutonEnregistrer;
    private EnregistrerNote enregistrerNote;
    private boolean nouveau;
    private final Deque<String> pileUndo = new ArrayDeque<>();
    private final Deque<String> pileRedo = new ArrayDeque<>();
    private String textOrigine = "";
    private boolean modif = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeur_notes);

        titre = findViewById(R.id.titre_note);
        editDetails = findViewById(R.id.details_note);
        boutonUndo = findViewById(R.id.bouton_Undo);
        boutonRedo = findViewById(R.id.bouton_Redo);
        boutonGPS = findViewById(R.id.bouton_GPS);
        boutonEnregistrer = findViewById(R.id.bouton_Enregistrer);

        SharedPreferences preferences = getSharedPreferences(EnregistrerNote.fichier, MODE_PRIVATE);
        enregistrerNote = new EnregistrerNote(preferences);


        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        String details = intent.getStringExtra("details");

        if (Intent.ACTION_SEND.equals(intent.getAction())
                && "text/plain".equals(intent.getType())) {
            String texteRecu = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (texteRecu != null) {
                details = texteRecu;
            }
            String sujet = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            if (sujet != null) {
                titre.setText(sujet);
            }
        }

        nouveau = nom == null;
        if (nom != null) {
            titre.setText(nom);
            titre.setEnabled(false);
            editDetails.setText(details);
        } else if (details != null) {
            editDetails.setText(details);
        }


        textOrigine = editDetails.getText().toString();

        editDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (modif) {
                    return;
                }
                pileUndo.push(textOrigine);
                textOrigine = s.toString();
                pileRedo.clear();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        boutonUndo.setOnClickListener(v -> annulerModification());
        boutonRedo.setOnClickListener(v -> restaurerModification());
        boutonGPS.setOnClickListener(v -> demanderLocalisation());
        boutonEnregistrer.setOnClickListener(v -> enregistrerNote());

    }

    private void annulerModification() {
        if (pileUndo.isEmpty()) {
            return;
        }
        pileRedo.push(textOrigine);
        afficherTexte(pileUndo.pop());
    }

    private void restaurerModification() {
        if (pileRedo.isEmpty()) {
            return;
        }
        pileUndo.push(textOrigine);
        afficherTexte(pileRedo.pop());
    }

    private void afficherTexte(String lTexte) {
        modif = true;
        textOrigine = lTexte;
        editDetails.setText(lTexte);
        editDetails.setSelection(lTexte.length());
        modif = false;

    }

    private void demanderLocalisation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION
            );

            return;
        }

        obtenirPosition();
    }

    @SuppressLint("MissingPermission")
    private void obtenirPosition() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toast.makeText(this, "En recherche de la position", Toast.LENGTH_SHORT).show();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String texte = "\n" + "Latitude : " + location.getLatitude() + "\n" + " Longitude: " + location.getLongitude();
                int position = Math.max(editDetails.getSelectionStart(), 0);
                editDetails.getText().insert(position, texte);
                locationManager.removeUpdates(this);
            }
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationListener
        );

    }

    private void enregistrerNote() {
        String nom = titre.getText().toString().trim();
        String details = editDetails.getText().toString();

        if (nom.isEmpty()) {
            Toast.makeText(this, "Il manque un titre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nouveau && enregistrerNote.rechercher(nom) != null) {
            Toast.makeText(this, "Une note existe deja avec ce nom!", Toast.LENGTH_SHORT).show();
            return;
        }

        enregistrerNote.sauvegarder(new Note(nom, details, LocalDateTime.now()));
        Toast.makeText(this, "Note enregistrer", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenirPosition();
            } else {
                Toast.makeText(this, "Permission de localisation refusée",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}