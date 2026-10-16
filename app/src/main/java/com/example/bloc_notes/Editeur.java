package com.example.bloc_notes;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Editeur extends AppCompatActivity {
    private TextView titre;
    private EditText editDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeur_notes);

        titre = findViewById(R.id.titre_note);
        editDetails = findViewById(R.id.details_note);
        String nom = getIntent().getStringExtra("nom");
        String details = getIntent().getStringExtra("details");
        if (nom != null) {
            titre.setText(nom);
            editDetails.setText(details);
        }

    }
}
