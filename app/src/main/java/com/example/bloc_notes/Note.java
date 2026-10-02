package com.example.bloc_notes;

import java.time.LocalDateTime;

public class Note {
    private String nom;
    private String details;
    private LocalDateTime date;

    public Note(String nom, String details, LocalDateTime date) {
        this.nom = nom;
        this.details = details;
        this.date = date;
    }

    public String getNom(){
        return nom;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
