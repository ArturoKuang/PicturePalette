package com.example.picturepalette

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


const val REQUEST_TAKE_PHOTO = 1
const val REQUEST_PERMISSION = 2
const val RESULT_LOAD_IMAGE = 3

class MainActivity : AppCompatActivity() {
    private lateinit var colorPaletteRecyclerView: RecyclerView
    private lateinit var colorImageRecyclerView: RecyclerView
    private lateinit var cameraButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var refreshButton: ImageButton
    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    private lateinit var colorImageAdapter: ColorImageAdapter
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri
    private val random = Random()
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
        refreshButton = findViewById(R.id.refresh_button)
        colorPaletteAdapter = ColorPaletteAdapter(colorPaletteListViewModel.colorList)
        colorImageAdapter = ColorImageAdapter(colorImageListViewModel.colorList)

        colorPaletteRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        colorImageRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        colorPaletteRecyclerView.adapter = colorPaletteAdapter
        colorImageRecyclerView.adapter = colorImageAdapter

        cameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        galleryButton.setOnClickListener() {
            val intent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        }

        refreshButton.setOnClickListener() {
            createColorPaletteFromImageView()
        }

        requestPermissions()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                photoFile = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "PHOTO FILE COULD NOT BE CREATED", Toast.LENGTH_LONG)
                        .show()
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createColorBucket(bitmap: Bitmap): List<Color> {
        var colorBucket = mutableMapOf<Int, Int>()
        var colorList = mutableListOf<FloatArray>()

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val color = bitmap.getPixel(x, y)
                colorBucket.merge(color, 1, Int::plus)

                var hsvColor = FloatArray(3)
                Color.colorToHSV(color, hsvColor)
                colorList.add(hsvColor)
            }
        }

        val colorExtractor = ColorExtractor(colorList, 10, 5)

        return colorExtractor.extract()
        //return colorBucket.toList().sortedBy { (_, value) -> value }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap =
                photoFile?.path?.let {
                    getScaledBitmap(
                        it,
                        photo_ImageView.width,
                        photo_ImageView.height
                    )
                }
            photo_ImageView.setImageBitmap(imageBitmap)
            if (imageBitmap != null) {
                saveImage(imageBitmap)
                getColorsFromImage(imageBitmap)
                updateUI()

            }
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImageUri = data.data
            photo_ImageView.setImageURI(selectedImageUri)
            createColorPaletteFromImageView()
        }
    }

    private fun createColorPaletteFromImageView() {
        val bitmap = photo_ImageView.drawable.toBitmap()
        getColorsFromImage(bitmap)
        updateUI()
    }
    
    private fun getColorsFromImage(bitmap: Bitmap) {
        val colorBucket = createColorBucket(bitmap)
        for (i in colorBucket.indices) {
            //colorImageListViewModel.colorList[i] = Color.valueOf(colorBucket[i].first)
            colorImageListViewModel.colorList[i] = colorBucket[i]
        }

        val sample = Palette(
            intArrayOf(
                colorBucket[0].toArgb(),
                colorBucket[1].toArgb(),
                colorBucket[2].toArgb(),
                colorBucket[3].toArgb(),
                colorBucket[4].toArgb()
            )
        )

        for (i in 0..4) {
            colorPaletteListViewModel.colorList[i] = sample.generateColor()
        }
    }

    private fun saveImage(bitmap: Bitmap) {
        val relativeLocation =
            Environment.DIRECTORY_PICTURES + File.pathSeparator + "PicturePalette"
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val resolver = this.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)

        try {
            uri?.let { uri ->
                val stream = resolver.openOutputStream(uri)
                stream?.let { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)) {
                        throw IOException("failed to saved bitmap")
                    }
                } ?: throw IOException("failed to get output stream")
            } ?: throw IOException("failed to create new MediaStore record")
        } catch (e: IOException) {
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            throw IOException(e)
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValue.put(MediaStore.MediaColumns.IS_PENDING, 0)
            }
        }
    }

    private fun updateUI() {
        val imageColors = colorImageListViewModel.colorList
        colorImageAdapter = ColorImageAdapter(imageColors)
        colorImageRecyclerView.adapter = colorImageAdapter

        val paletteColors = colorPaletteListViewModel.colorList
        colorPaletteAdapter = ColorPaletteAdapter(paletteColors)
        colorPaletteRecyclerView.adapter = colorPaletteAdapter
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION)
    }

    private inner class ColorPaletteAdapter(var colors: List<Color>) :
        RecyclerView.Adapter<ColorPaletteHolder>() {

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
                val colorValue = Color.rgb(color.red(), color.green(), color.blue())
                val colorString = "#" + Integer.toHexString(colorValue)
                button.text = colorString
            }
        }
    }

    private inner class ColorPaletteHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val button: Button = itemView.findViewById(R.id.colorPaletteButton)
    }


    private inner class ColorImageAdapter(var colors: List<Color>) :
        RecyclerView.Adapter<ColorImageHolder>() {

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
                val colorValue = Color.rgb(color.red(), color.green(), color.blue())
                val colorString = "#" + Integer.toHexString(colorValue)
                button.text = colorString
            }
        }
    }

    private inner class ColorImageHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val button: Button = itemView.findViewById(R.id.colorImageButton)
    }
}