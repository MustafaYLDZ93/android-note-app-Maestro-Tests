package com.noteapp.notes.ui.detail;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.noteapp.notes.R;
import com.noteapp.notes.data.model.Note;
import com.noteapp.notes.databinding.ActivityNoteDetailBinding;
import com.noteapp.notes.ui.main.NoteViewModel;
import com.noteapp.notes.util.DateUtils;
import com.noteapp.notes.util.NoteColors;

public class NoteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID   = "extra_note_id";
    public static final String EXTRA_EDIT_MODE = "extra_edit_mode";

    private ActivityNoteDetailBinding binding;
    private NoteViewModel viewModel;

    private Note currentNote  = null;
    private long selectedDate;
    private int  selectedColor = 0;
    private boolean isEditMode;
    private boolean isNewNote;

    private final ImageView[] colorCircles = new ImageView[NoteColors.COLORS.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        int noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        isEditMode = getIntent().getBooleanExtra(EXTRA_EDIT_MODE, false);
        isNewNote  = (noteId == -1);
        selectedDate = System.currentTimeMillis();

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        setupToolbar();
        setupColorPicker();
        setupDatePicker();
        binding.fabSave.setOnClickListener(v -> saveNote());

        if (!isNewNote) {
            loadNote(noteId);
        } else {
            updateDateButton();
            setEditMode(true);
        }
    }

    // ─── Toolbar ───────────────────────────────────────────────────────────────

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (isNewNote) {
            binding.toolbar.setTitle(getString(R.string.new_note_title));
            binding.toolbar.getMenu().clear();
        } else {
            binding.toolbar.setTitle(isEditMode ?
                    getString(R.string.edit_note_title) :
                    getString(R.string.note_detail_title));
        }

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete) { confirmDelete(); return true; }
            if (id == R.id.action_pin)    { togglePin();     return true; }
            return false;
        });
    }

    private void updatePinIcon() {
        if (currentNote == null) return;
        android.view.MenuItem pinItem = binding.toolbar.getMenu().findItem(R.id.action_pin);
        if (pinItem != null) {
            pinItem.setIcon(currentNote.isPinned() ? R.drawable.ic_pin_on : R.drawable.ic_pin_off);
            pinItem.setTitle(currentNote.isPinned() ? R.string.unpin_note : R.string.pin_note);
        }
    }

    private void togglePin() {
        if (currentNote == null) return;
        currentNote.setPinned(!currentNote.isPinned());
        currentNote.setUpdatedAt(System.currentTimeMillis());
        viewModel.update(currentNote);
        updatePinIcon();
        Snackbar.make(binding.getRoot(),
                currentNote.isPinned() ? R.string.note_pinned : R.string.note_unpinned,
                Snackbar.LENGTH_SHORT).show();
    }

    // ─── Color Picker ──────────────────────────────────────────────────────────

    private void setupColorPicker() {
        LinearLayout container = binding.colorPickerRow;
        int dp8  = dpToPx(8);
        int dp32 = dpToPx(32);

        for (int i = 0; i < NoteColors.COLORS.length; i++) {
            final int idx = i;
            ImageView circle = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp32, dp32);
            lp.setMargins(dp8, 0, dp8, 0);
            circle.setLayoutParams(lp);
            drawCircle(circle, NoteColors.COLORS[i], false);
            circle.setOnClickListener(v -> selectColor(idx));
            container.addView(circle);
            colorCircles[i] = circle;
        }
        selectColorIndex(0);
    }

    private void selectColor(int index) {
        selectedColor = NoteColors.COLORS[index];
        for (int i = 0; i < colorCircles.length; i++) {
            drawCircle(colorCircles[i], NoteColors.COLORS[i], i == index);
        }
        binding.scrollContent.setBackgroundColor(NoteColors.resolveCardColor(selectedColor));
    }

    private void selectColorIndex(int index) { selectColor(index); }

    private void selectColorByValue(int color) {
        for (int i = 0; i < NoteColors.COLORS.length; i++) {
            if (NoteColors.COLORS[i] == color) { selectColor(i); return; }
        }
        selectColor(0);
    }

    private void drawCircle(ImageView iv, int noteColor, boolean selected) {
        int fill = (noteColor == 0) ? Color.WHITE : noteColor;
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(fill);
        gd.setStroke(dpToPx(selected ? 3 : 1), selected ? 0xFF5C6BC0 : 0xFFBDBDBD);
        iv.setImageDrawable(gd);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ─── Date Picker ───────────────────────────────────────────────────────────

    private void setupDatePicker() {
        binding.btnSelectDate.setOnClickListener(v -> {
            if (!isEditMode) return;
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(selectedDate)
                    .build();
            picker.addOnPositiveButtonClickListener(sel -> { selectedDate = sel; updateDateButton(); });
            picker.show(getSupportFragmentManager(), "date_picker");
        });
    }

    private void updateDateButton() {
        binding.btnSelectDate.setText(DateUtils.formatDate(selectedDate));
    }

    // ─── Save ──────────────────────────────────────────────────────────────────

    private void saveNote() {
        String title   = getEditText(binding.etTitle);
        String content = getEditText(binding.etContent);

        if (title.isEmpty()) {
            binding.titleLayout.setError(getString(R.string.error_title_required));
            return;
        }
        binding.titleLayout.setError(null);
        long now = System.currentTimeMillis();

        if (isNewNote) {
            Note note = new Note(title, content, selectedDate, now, now);
            note.setColor(selectedColor);
            viewModel.insert(note);
            Snackbar.make(binding.getRoot(), R.string.note_saved, Snackbar.LENGTH_SHORT).show();
            finish();
        } else {
            currentNote.setTitle(title);
            currentNote.setContent(content);
            currentNote.setNoteDate(selectedDate);
            currentNote.setColor(selectedColor);
            currentNote.setUpdatedAt(now);
            viewModel.update(currentNote);
            binding.tvUpdatedAt.setText(getString(R.string.last_updated_at, DateUtils.formatDateTime(now)));
            Snackbar.make(binding.getRoot(), R.string.note_updated, Snackbar.LENGTH_SHORT).show();
            setEditMode(false);
            binding.toolbar.setTitle(getString(R.string.note_detail_title));
        }
    }

    // ─── Load ──────────────────────────────────────────────────────────────────

    private void loadNote(int noteId) {
        viewModel.getNoteById(noteId).observe(this, note -> {
            if (note != null && currentNote == null) {
                currentNote   = note;
                selectedDate  = note.getNoteDate();
                selectedColor = note.getColor();

                binding.etTitle.setText(note.getTitle());
                binding.etContent.setText(note.getContent());
                updateDateButton();
                selectColorByValue(note.getColor());

                binding.metaSection.setVisibility(View.VISIBLE);
                binding.tvCreatedAt.setText(getString(R.string.created_at,
                        DateUtils.formatDateTime(note.getCreatedAt())));
                binding.tvUpdatedAt.setText(getString(R.string.last_updated_at,
                        DateUtils.formatDateTime(note.getUpdatedAt())));

                updatePinIcon();
                setEditMode(isEditMode);
            }
        });
    }

    // ─── Edit mode ─────────────────────────────────────────────────────────────

    private void setEditMode(boolean editable) {
        isEditMode = editable;
        binding.etTitle.setEnabled(editable);
        binding.etContent.setEnabled(editable);
        binding.btnSelectDate.setEnabled(editable);
        binding.colorPickerRow.setVisibility(editable ? View.VISIBLE : View.GONE);
        binding.fabSave.setVisibility(editable ? View.VISIBLE : View.GONE);

        if (!editable) {
            View.OnClickListener trigger = v -> setEditMode(true);
            binding.etTitle.setOnClickListener(trigger);
            binding.etContent.setOnClickListener(trigger);
        } else {
            binding.etTitle.setOnClickListener(null);
            binding.etContent.setOnClickListener(null);
        }
    }

    // ─── Delete ────────────────────────────────────────────────────────────────

    private void confirmDelete() {
        if (currentNote == null) return;
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirm_title)
                .setMessage(getString(R.string.delete_confirm_message, currentNote.getTitle()))
                .setPositiveButton(R.string.delete, (d, w) -> { viewModel.delete(currentNote); finish(); })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (isEditMode && !isNewNote) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.unsaved_changes_title)
                    .setMessage(R.string.unsaved_changes_message)
                    .setPositiveButton(R.string.save, (d, w) -> saveNote())
                    .setNegativeButton(R.string.discard, (d, w) -> super.onBackPressed())
                    .setNeutralButton(R.string.cancel, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private String getEditText(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
