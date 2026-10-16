package com.example.bloc_notes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private static final int LONGUEUR = 30;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private List<Note> listeNotes;

    public NoteAdapter(List<Note> listeNotes) {
        this.listeNotes = listeNotes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note, parent, false);

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteViewHolder holder, int position) {
        Note note = listeNotes.get(position);
        holder.titre.setText(note.getNom());
        holder.details.setText(note.getDetails());
        holder.date.setText(note.getDate().format(FORMAT_DATE));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Editeur.class);
            intent.putExtra("nom", note.getNom());
            intent.putExtra("details", note.getDetails());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listeNotes.size();
    }

    public String creerApercu(String details) {
        String premiereLigne = details.split("\n", 2)[0];
        if (premiereLigne.length() > LONGUEUR) {
            return premiereLigne.substring(0, LONGUEUR) + "...";
        }
        return premiereLigne;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titre;
        TextView date;
        TextView details;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            titre = itemView.findViewById(R.id.titre_note);
            details = itemView.findViewById(R.id.details_note);
            date = itemView.findViewById(R.id.date_note);
        }
    }
}
