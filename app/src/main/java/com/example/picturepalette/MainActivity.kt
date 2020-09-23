package com.example.picturepalette

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorSpace
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.convertTo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_TAKE_PHOTO = 1

class MainActivity : AppCompatActivity() {
    private lateinit var colorPaletteRecyclerView: RecyclerView
    private lateinit var colorImageRecyclerView: RecyclerView
    private lateinit var cameraButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    private lateinit var colorImageAdapter: ColorImageAdapter
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri
    private var photoFile: File? = null

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

        cameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "PHOTO FILE COULD NOT BE CREATED", Toast.LENGTH_LONG).show()
                    null
                }

                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.picturepalette.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storeDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storeDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap =
                photoFile?.path?.let { getScaledBitmap(it, photo_ImageView.width, photo_ImageView.height) }
            photo_ImageView.setImageBitmap(imageBitmap)
        }
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