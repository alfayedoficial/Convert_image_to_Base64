package com.alfayedoficial.convertimagetobase64

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.ByteArrayOutputStream


private const val PIC_PHOTO = 123
class MainActivity : AppCompatActivity() {

    private val imgPicked: ImageView by lazy { findViewById(R.id.imgPicked) }
    private val imgConverted: ImageView by lazy { findViewById(R.id.imgConverted) }
    private val tvBytes: TextView by lazy { findViewById(R.id.tvBytes) }
    private val galleryBtn: MaterialButton by lazy { findViewById(R.id.galleryBtn) }
    private val convertBytesToImageBtn: MaterialButton by lazy { findViewById(R.id.convertBytesToImageBtn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set textview to scrollable
        tvBytes.movementMethod = ScrollingMovementMethod()
        // must take permission READ_EXTERNAL_STORAGE first
        galleryBtn.setOnClickListener {
            // check if permission granted
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // permission granted
                imgConverted.setImageResource(R.drawable.ic_launcher_background)
                imgPicked.setImageResource(R.drawable.ic_launcher_background)
                tvBytes.text = ""
                // open gallery
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PIC_PHOTO)
            } else {
                // permission not granted
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            }
        }

        convertBytesToImageBtn.setOnClickListener {
            val encodedImageString =  findViewById<TextView>(R.id.tvBytes).text.toString()
            val decodedString: ByteArray = Base64.decode(encodedImageString, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imgConverted.setImageBitmap(decodedByte)

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
        * when result is ok
        * initialize uri
        * */
        val uri = data?.data ?: return

        try{
            // initialize byte stream
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver , uri)
            // compress Bitmap
            val stream = ByteArrayOutputStream().apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,this)
            }
            // Initialize byte array
            val bytes = stream.toByteArray()
            // get base64 encoded string
            val stringImages = Base64.encodeToString(bytes,Base64.DEFAULT)
            imgPicked.setImageBitmap(bitmap)
            tvBytes.text = stringImages
        }catch (e:Exception){
            Toast.makeText(this , "pick up image does not finish well" ,Toast.LENGTH_LONG ).show()
        }
    }
}