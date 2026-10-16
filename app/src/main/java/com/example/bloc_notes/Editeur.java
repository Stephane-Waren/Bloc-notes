package com.example.bloc_notes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayDeque;
import java.util.Deque;

public class Editeur extends AppCompatActivity {
    private TextView titre;
    private EditText editDetails;
    private Button boutonUndo;
    private Button boutonRedo;
    private final Deque<String> pileUndo = new ArrayDeque<>();
    private final Deque<String> pileRedo = new ArrayDeque<>();
    private String textOrigine = "";
    private boolean modif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editeur_notes);

        titre = findViewById(R.id.titre_note);
        editDetails = findViewById(R.id.details_note);
        boutonUndo = findViewById(R.id.bouton_Undo);
        boutonRedo = findViewById(R.id.bouton_Redo);

        String nom = getIntent().getStringExtra("nom");
        String details = getIntent().getStringExtra("details");
        if (nom != null) {
            titre.setText(nom);
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


}

