package com.example.picturepalette

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ColorPaletteHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    val button: Button = itemView.findViewById(R.id.colorPaletteButton)
}