package com.noteapp.notes.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.noteapp.notes.data.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    // --- Tüm notlar (sıralama seçenekleri) ---
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, noteDate DESC, updatedAt DESC")
    LiveData<List<Note>> getAllByDateDesc();

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, noteDate ASC, updatedAt ASC")
    LiveData<List<Note>> getAllByDateAsc();

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, title ASC")
    LiveData<List<Note>> getAllByTitleAsc();

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, title DESC")
    LiveData<List<Note>> getAllByTitleDesc();

    // --- Arama (sıralama seçenekleri) ---
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :q || '%' OR content LIKE '%' || :q || '%' ORDER BY isPinned DESC, noteDate DESC")
    LiveData<List<Note>> searchByDateDesc(String q);

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :q || '%' OR content LIKE '%' || :q || '%' ORDER BY isPinned DESC, noteDate ASC")
    LiveData<List<Note>> searchByDateAsc(String q);

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :q || '%' OR content LIKE '%' || :q || '%' ORDER BY isPinned DESC, title ASC")
    LiveData<List<Note>> searchByTitleAsc(String q);

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :q || '%' OR content LIKE '%' || :q || '%' ORDER BY isPinned DESC, title DESC")
    LiveData<List<Note>> searchByTitleDesc(String q);

    @Query("SELECT * FROM notes WHERE id = :id")
    LiveData<Note> getNoteById(int id);
}
