package com.example.bloc_notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private Button buttonAjouter;
    private List<Note> listeNotes;
    private NoteAdapter adapter;
    private EnregistrerNote enregistrer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerNotes = findViewById(R.id.recyclerNotes);
        buttonAjouter = findViewById(R.id.buttonAjouter);
        SharedPreferences preferences = getSharedPreferences(EnregistrerNote.fichier, MODE_PRIVATE);
        enregistrer = new EnregistrerNote(preferences);
        if (enregistrer.charger().isEmpty()) {
            enregistrer.sauvegarder(new Note("Philo", "Matiere de session 3 , Philosophie et rationnalite,prof tres bon", LocalDateTime.now()));
            enregistrer.sauvegarder(new Note("Native-3", "Matiere de session 3 , Creation d'application native,prof tres bon", LocalDateTime.now()));
            enregistrer.sauvegarder(new Note("Web-3", "Matiere de session 3 , Creation de page web et inter-action avec une API,prof tres bon", LocalDateTime.now()));
        }

        listeNotes = new ArrayList<>();

        adapter = new NoteAdapter(listeNotes);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setAdapter(adapter);
        buttonAjouter.setOnClickListener(v -> startActivity(new Intent(this, Editeur.class)));
        listeNotes.sort((a, b) -> b.getDate().compareTo(a.getDate()));
    }
    @Override
    protected void onResume() {
        super.onResume();
        rafraichirListe();
    }

    private void rafraichirListe() {
        listeNotes.clear();
        listeNotes.addAll(enregistrer.charger());
        listeNotes.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        adapter.notifyDataSetChanged();
    }

}