package com.example.picturepalette

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class ColorImageAdapter(var colors: List<Color>) :
    RecyclerView.Adapter<ColorImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorImageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_image_list_item, parent, false)

        return ColorImageHolder(view)
    }

    override fun getItemCount(): Int = colors.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ColorImageHolder, position: Int) {
        var color = colors[position]
        holder.apply {
            button.setBackgroundColor(color.toArgb())
            val colorString = convertColorToHex(color)
            button.text = colorString
        }
    }

    private fun convertColorToHex(color: Color): String {
        val colorValue = Color.rgb(color.red(), color.green(), color.blue())
        return "#" + Integer.toHexString(colorValue)
    }
}
