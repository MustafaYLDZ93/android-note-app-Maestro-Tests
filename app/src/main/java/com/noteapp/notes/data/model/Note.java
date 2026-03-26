package com.noteapp.notes.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String content;
    private long noteDate;
    private long createdAt;
    private long updatedAt;
    private int color;        // ARGB int, 0 = default white
    private boolean isPinned; // sabitleme

    public Note(String title, String content, long noteDate, long createdAt, long updatedAt) {
        this.title = title;
        this.content = content;
        this.noteDate = noteDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.color = 0;
        this.isPinned = false;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public long getNoteDate() { return noteDate; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public int getColor() { return color; }
    public boolean isPinned() { return isPinned; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setNoteDate(long noteDate) { this.noteDate = noteDate; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public void setColor(int color) { this.color = color; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
}
