package com.example.notesss.listeners;

import com.example.notesss.entities.Note;

public interface NoteListener {
    void onNoteClick(Note note, int position);

}
