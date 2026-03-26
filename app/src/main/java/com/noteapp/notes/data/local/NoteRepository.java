package com.noteapp.notes.data.local;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.noteapp.notes.data.model.Note;
import com.noteapp.notes.util.SortOrder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDao noteDao;
    private final ExecutorService executor;

    public NoteRepository(Application application) {
        NoteDatabase db = NoteDatabase.getInstance(application);
        noteDao = db.noteDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Note note) { executor.execute(() -> noteDao.insert(note)); }
    public void update(Note note) { executor.execute(() -> noteDao.update(note)); }
    public void delete(Note note) { executor.execute(() -> noteDao.delete(note)); }

    public LiveData<Note> getNoteById(int id) { return noteDao.getNoteById(id); }

    public LiveData<List<Note>> getAllNotesSorted(SortOrder sort) {
        switch (sort) {
            case DATE_ASC:   return noteDao.getAllByDateAsc();
            case TITLE_ASC:  return noteDao.getAllByTitleAsc();
            case TITLE_DESC: return noteDao.getAllByTitleDesc();
            default:         return noteDao.getAllByDateDesc();
        }
    }

    public LiveData<List<Note>> searchNotesSorted(String query, SortOrder sort) {
        switch (sort) {
            case DATE_ASC:   return noteDao.searchByDateAsc(query);
            case TITLE_ASC:  return noteDao.searchByTitleAsc(query);
            case TITLE_DESC: return noteDao.searchByTitleDesc(query);
            default:         return noteDao.searchByDateDesc(query);
        }
    }
}
