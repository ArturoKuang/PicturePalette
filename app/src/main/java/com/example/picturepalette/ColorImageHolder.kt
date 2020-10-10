package com.example.picturepalette

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class ColorImageHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    val button: Button = itemView.findViewById(R.id.colorImageButton)
}