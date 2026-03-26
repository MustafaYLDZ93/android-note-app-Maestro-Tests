package com.noteapp.notes.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.noteapp.notes.data.local.NoteRepository;
import com.noteapp.notes.data.model.Note;
import com.noteapp.notes.util.SortOrder;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    public static class FilterState {
        public final String query;
        public final SortOrder sortOrder;
        FilterState(String query, SortOrder sortOrder) {
            this.query = query;
            this.sortOrder = sortOrder;
        }
    }

    private final NoteRepository repository;
    private final MutableLiveData<FilterState> filterState =
            new MutableLiveData<>(new FilterState("", SortOrder.DATE_DESC));
    public final LiveData<List<Note>> notes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        notes = Transformations.switchMap(filterState, state -> {
            if (state.query == null || state.query.trim().isEmpty()) {
                return repository.getAllNotesSorted(state.sortOrder);
            }
            return repository.searchNotesSorted(state.query.trim(), state.sortOrder);
        });
    }

    public void insert(Note note) { repository.insert(note); }
    public void update(Note note) { repository.update(note); }
    public void delete(Note note) { repository.delete(note); }

    public LiveData<Note> getNoteById(int id) { return repository.getNoteById(id); }

    public void setSearchQuery(String query) {
        FilterState current = filterState.getValue();
        SortOrder sort = current != null ? current.sortOrder : SortOrder.DATE_DESC;
        filterState.setValue(new FilterState(query, sort));
    }

    public void setSortOrder(SortOrder sortOrder) {
        FilterState current = filterState.getValue();
        String query = current != null ? current.query : "";
        filterState.setValue(new FilterState(query, sortOrder));
    }

    public SortOrder getCurrentSortOrder() {
        FilterState state = filterState.getValue();
        return state != null ? state.sortOrder : SortOrder.DATE_DESC;
    }

    public void togglePin(Note note) {
        note.setPinned(!note.isPinned());
        note.setUpdatedAt(System.currentTimeMillis());
        repository.update(note);
    }
}
