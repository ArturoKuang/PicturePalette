package com.example.picturepalette

import android.graphics.Color
import android.graphics.ColorSpace
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.graphics.convertTo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var colorPaletteRecyclerView: RecyclerView
    private lateinit var colorImageRecyclerView: RecyclerView
    private lateinit var cameraButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    private lateinit var colorImageAdapter: ColorImageAdapter

    private val colorImageListViewModel: ColorImageListViewModel by lazy {
        ViewModelProvider(this).get(ColorImageListViewModel::class.java)
    }

    private val colorPaletteListViewModel: ColorPaletteListViewModel by lazy {
        ViewModelProvider(this).get(ColorPaletteListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorPaletteRecyclerView = findViewById(R.id.colorPalette_recycler_view)
        colorImageRecyclerView = findViewById(R.id.colorImage_recycler_view)
        cameraButton = findViewById(R.id.camera_button)
        galleryButton = findViewById(R.id.select_image_button)
        colorPaletteAdapter = ColorPaletteAdapter(colorPaletteListViewModel.colorList)
        colorImageAdapter = ColorImageAdapter(colorImageListViewModel.colorList)

        colorPaletteRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)

        colorImageRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)

        colorPaletteRecyclerView.adapter = colorPaletteAdapter
        colorImageRecyclerView.adapter = colorImageAdapter
    }


    private inner class ColorPaletteAdapter(var colors: List<Color>)
        : RecyclerView.Adapter<ColorPaletteHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPaletteHolder {
            val view = layoutInflater.inflate(R.layout.color_palette_list_item, parent, false)
            return ColorPaletteHolder(view)
        }

        override fun getItemCount(): Int = colors.size

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ColorPaletteHolder, position: Int) {
            var color = colors[position]
            holder.apply {
                button.setBackgroundColor(color.toArgb())
                val colorValue =  Color.rgb(color.red(), color.green(), color.blue())
                val colorString = "#"+Integer.toHexString(colorValue)
                button.text = colorString
            }
        }
    }

    private inner class ColorPaletteHolder(view: View):
            RecyclerView.ViewHolder(view){

        val button: Button = itemView.findViewById(R.id.colorPaletteButton)
    }



    private inner class ColorImageAdapter(var colors: List<Color>)
        : RecyclerView.Adapter<ColorImageHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorImageHolder {
            val view = layoutInflater.inflate(R.layout.color_image_list_item, parent, false)
            return ColorImageHolder(view)
        }

        override fun getItemCount(): Int = colors.size

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ColorImageHolder, position: Int) {
            var color = colors[position]
            holder.apply {
                button.setBackgroundColor(color.toArgb())
                val colorValue =  Color.rgb(color.red(), color.green(), color.blue())
                val colorString = "#"+Integer.toHexString(colorValue)
                button.text = colorString
            }
        }
    }

    private inner class ColorImageHolder(view: View):
        RecyclerView.ViewHolder(view){

        val button: Button = itemView.findViewById(R.id.colorImageButton)
    }
}