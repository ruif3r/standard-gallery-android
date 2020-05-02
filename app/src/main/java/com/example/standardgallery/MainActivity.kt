package com.example.standardgallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.standardgallery.adapter.GalleryPictureAdapter
import com.example.standardgallery.model.GalleryPicture
import com.example.standardgallery.viewmodel.GalleryViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var adapter : GalleryPictureAdapter
    private lateinit var galleryViewModel : GalleryViewModel
    private lateinit var pictures : ArrayList<GalleryPicture>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission(){
        val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat
                .checkSelfPermission(this, readStorage)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(readStorage), 3)
        }else init()
    }

    private fun init(){
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
       // updateToolbar(0)
        val rv : RecyclerView = findViewById(R.id.rv)
        val layoutManager = GridLayoutManager(this, 3)
        rv.layoutManager = layoutManager
       // rv.addItemDecoration(SpaceItemDecoration(8))
        pictures = ArrayList<GalleryPicture>(galleryViewModel.getGallerySize(this))
        adapter = GalleryPictureAdapter(pictures, this)
        rv.adapter = adapter

       // adapter.setAfterSelectionListener { updateToolBar(getSelectedItemsCount()) }

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastVisibleItemPosition() == pictures.lastIndex){
                    loadPictures(25)
                }
            }
        })
        loadPictures(25)

    }

    private fun loadPictures(pageSize : Int){
        galleryViewModel.getImagesFromGallery(this, pageSize){
            if (it.isNotEmpty()){
                pictures.addAll(it)
                adapter.populateGallery(it)
                adapter.notifyItemRangeInserted(pictures.size, it.size)
            }
            Log.i("galleryListSize", "${pictures.size}")
            Log.i("content", "${pictures.first()}")
        }
    }

    private fun showToast(msg : String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init()
        else{
            showToast("Permission to fetch gallery denied")
            super.onBackPressed()
        }
    }

}
