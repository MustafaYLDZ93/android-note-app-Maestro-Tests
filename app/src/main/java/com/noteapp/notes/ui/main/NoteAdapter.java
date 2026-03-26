package com.noteapp.notes.ui.main;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.noteapp.notes.R;
import com.noteapp.notes.data.model.Note;
import com.noteapp.notes.util.DateUtils;
import com.noteapp.notes.util.NoteColors;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
        void onNoteEdit(Note note);
        void onNoteDelete(Note note);
        void onNoteTogglePin(Note note);
    }

    private final OnNoteClickListener listener;

    public NoteAdapter(OnNoteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note o, @NonNull Note n) {
            return o.getId() == n.getId();
        }
        @Override
        public boolean areContentsTheSame(@NonNull Note o, @NonNull Note n) {
            return o.getTitle().equals(n.getTitle())
                    && o.getContent().equals(n.getContent())
                    && o.getNoteDate() == n.getNoteDate()
                    && o.getUpdatedAt() == n.getUpdatedAt()
                    && o.getColor() == n.getColor()
                    && o.isPinned() == n.isPinned();
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final View colorStrip;
        private final TextView tvNoteDate;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvUpdatedAt;
        private final ImageView ivPin;
        private final ImageView btnMore;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView    = itemView.findViewById(R.id.cardView);
            colorStrip  = itemView.findViewById(R.id.colorStrip);
            tvNoteDate  = itemView.findViewById(R.id.tvNoteDate);
            tvTitle     = itemView.findViewById(R.id.tvTitle);
            tvContent   = itemView.findViewById(R.id.tvContent);
            tvUpdatedAt = itemView.findViewById(R.id.tvUpdatedAt);
            ivPin       = itemView.findViewById(R.id.ivPin);
            btnMore     = itemView.findViewById(R.id.btnMore);
        }

        void bind(Note note) {
            // Renk
            int cardColor = NoteColors.resolveCardColor(note.getColor());
            cardView.setCardBackgroundColor(cardColor);
            if (note.getColor() != 0) {
                colorStrip.setBackgroundColor(darken(note.getColor(), 0.75f));
                colorStrip.setVisibility(View.VISIBLE);
            } else {
                colorStrip.setVisibility(View.GONE);
            }

            // Sabitleme ikonu
            ivPin.setVisibility(note.isPinned() ? View.VISIBLE : View.GONE);

            tvNoteDate.setText(DateUtils.formatDate(note.getNoteDate()));
            tvTitle.setText(note.getTitle());

            if (note.getContent() != null && !note.getContent().trim().isEmpty()) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(note.getContent());
            } else {
                tvContent.setVisibility(View.GONE);
            }

            tvUpdatedAt.setText(itemView.getContext().getString(
                    R.string.updated_at, DateUtils.formatDateTime(note.getUpdatedAt())));

            itemView.setOnClickListener(v -> listener.onNoteClick(note));

            btnMore.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), btnMore);
                popup.inflate(R.menu.menu_note_item);
                // Pin menü metnini dinamik ayarla
                popup.getMenu().findItem(R.id.action_pin)
                        .setTitle(note.isPinned()
                                ? itemView.getContext().getString(R.string.unpin_note)
                                : itemView.getContext().getString(R.string.pin_note));
                popup.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.action_edit)   { listener.onNoteEdit(note);      return true; }
                    if (id == R.id.action_pin)    { listener.onNoteTogglePin(note); return true; }
                    if (id == R.id.action_delete) { listener.onNoteDelete(note);    return true; }
                    return false;
                });
                popup.show();
            });
        }

        /** Rengi belirtilen oranda koyulaştırır (şerit için) */
        private int darken(int color, float factor) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= factor;
            return Color.HSVToColor(hsv);
        }
    }
}
