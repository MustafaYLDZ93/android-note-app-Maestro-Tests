package com.noteapp.notes.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.noteapp.notes.R;
import com.noteapp.notes.data.model.Note;
import com.noteapp.notes.databinding.ActivityMainBinding;
import com.noteapp.notes.ui.detail.NoteDetailActivity;
import com.noteapp.notes.util.SortOrder;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteViewModel viewModel;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        setupRecyclerView();
        setupViewModel();
        setupSearch();
        binding.fab.setOnClickListener(v -> openNoteDetail(-1, true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_date_desc)  { setSortAndCheck(item, SortOrder.DATE_DESC);  return true; }
        if (id == R.id.sort_date_asc)   { setSortAndCheck(item, SortOrder.DATE_ASC);   return true; }
        if (id == R.id.sort_title_asc)  { setSortAndCheck(item, SortOrder.TITLE_ASC);  return true; }
        if (id == R.id.sort_title_desc) { setSortAndCheck(item, SortOrder.TITLE_DESC); return true; }
        return super.onOptionsItemSelected(item);
    }

    private void setSortAndCheck(MenuItem item, SortOrder order) {
        item.setChecked(true);
        viewModel.setSortOrder(order);
    }

    private void setupRecyclerView() {
        adapter = new NoteAdapter(new NoteAdapter.OnNoteClickListener() {
            @Override public void onNoteClick(Note note)      { openNoteDetail(note.getId(), false); }
            @Override public void onNoteEdit(Note note)       { openNoteDetail(note.getId(), true); }
            @Override public void onNoteDelete(Note note)     { confirmDelete(note); }
            @Override public void onNoteTogglePin(Note note)  {
                viewModel.togglePin(note);
                int msg = note.isPinned() ? R.string.note_pinned : R.string.note_unpinned;
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(null);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        viewModel.notes.observe(this, notes -> {
            adapter.submitList(notes);
            boolean empty = notes == null || notes.isEmpty();
            binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { viewModel.setSearchQuery(s.toString()); }
        });
    }

    private void openNoteDetail(int noteId, boolean editMode) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, noteId);
        intent.putExtra(NoteDetailActivity.EXTRA_EDIT_MODE, editMode);
        startActivity(intent);
    }

    private void confirmDelete(Note note) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, note.getTitle()))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    viewModel.delete(note);
                    Snackbar.make(binding.getRoot(), R.string.note_deleted, Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
