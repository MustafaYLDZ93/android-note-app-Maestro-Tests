package com.noteapp.notes.util;

import android.graphics.Color;

public class NoteColors {

    // 0 = varsayılan (beyaz), diğerleri pastel renkler
    public static final int[] COLORS = {
            0,            // Default
            0xFFFFF9C4,   // Sarı
            0xFFC8E6C9,   // Yeşil
            0xFFBBDEFB,   // Mavi
            0xFFE1BEE7,   // Mor
            0xFFFCE4EC,   // Pembe
            0xFFFFE0B2,   // Turuncu
    };

    /** Kart arka planı için renk döndürür; 0 ise beyaz */
    public static int resolveCardColor(int noteColor) {
        return noteColor == 0 ? Color.WHITE : noteColor;
    }
}
