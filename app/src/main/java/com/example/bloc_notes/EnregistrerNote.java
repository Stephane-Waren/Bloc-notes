package com.example.bloc_notes;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnregistrerNote {
    public static final String fichier = "bloc-Notes";
    public static final String cle = "notes";
    public final SharedPreferences preferences;

    public EnregistrerNote(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public List<Note> charger(){
        List<Note> notes = new ArrayList<>();
        String json = preferences.getString(cle,"[]");
        try{
            JSONArray tab = new JSONArray(json);
            for(int i = 0; i < tab.length(); i++){
                JSONObject o = tab.getJSONObject(i);
                notes.add( new Note (o.getString("nom"),o.getString("details"),LocalDateTime.parse(o.getString("date"))));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return notes;
    }
    public Note rechercher(String nom) {
        List<Note> notes = charger();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            if (note.getNom().equals(nom)) {
                return note;
            }
        }
        return null;
    }
    public void sauvegarder(Note note) {
        List<Note> notes = charger();
        boolean trouvee = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getNom().equals(note.getNom())) {
                notes.set(i, note);
                trouvee = true;
                break;
            }
        }
        if (!trouvee) {
            notes.add(note);
        }
        ecrire(notes);
    }
    private void ecrire(List<Note> notes) {
        JSONArray tableau = new JSONArray();
        try {
            for (Note note : notes) {
                JSONObject o = new JSONObject();
                o.put("nom", note.getNom());
                o.put("details", note.getDetails());
                o.put("date", note.getDate().toString());
                tableau.put(o);
            }
        } catch (JSONException e) {
            return;
        }
        preferences.edit().putString(cle, tableau.toString()).apply();
    }
}
