package com.example.standardgallery.viewmodel

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.standardgallery.model.GalleryPicture
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class GalleryViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var startingRow = 0
    private var rowsToLoad = 0
    private var allLoaded = false

    fun getImagesFromGallery(
        context: Context,
        pageSize: Int,
        list: (List<GalleryPicture>) -> Unit
    ) {
        compositeDisposable.add(
            Single.fromCallable {
                fetchGalleryImages(context, pageSize)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list(it) }, { it.printStackTrace()
                })
        )
    }

    fun getGallerySize(context: Context) : Int{
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID) //get all files of type images
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val cursor = context.contentResolver
            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, "$orderBy DESC")
        val rows = cursor!!.count
        cursor.close()
        return rows
    }

    fun fetchGalleryImages(context: Context, rowsPerLoad : Int) : List<GalleryPicture> {
        val galleryImageUrls = LinkedList<GalleryPicture>()
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val cursor = context.contentResolver
            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, "$orderBy DESC")
        Log.i("GalleryAllLoaded", "$allLoaded")

        if(cursor != null && !allLoaded) {
            val totalRows = cursor.count
            allLoaded = rowsToLoad == totalRows
            if (rowsToLoad < rowsPerLoad){
                rowsToLoad = rowsPerLoad
            }

            for (i in startingRow until rowsToLoad){
                cursor.moveToPosition(i)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                galleryImageUrls.add(GalleryPicture(cursor.getString(dataColumnIndex)))
            }
            Log.i("TotalGallerySize", "$totalRows")
            Log.i("GalleryStart", "$startingRow")
            Log.i("GalleryEnd", "$rowsToLoad")

            startingRow = rowsToLoad

            if (rowsPerLoad > totalRows || rowsToLoad >= totalRows){
                rowsToLoad = totalRows
            } else {
                if (totalRows - rowsToLoad <= rowsPerLoad)
                    rowsToLoad = totalRows
                else {
                    rowsToLoad += rowsPerLoad
                }
            }
            cursor.close()
            Log.i("PartiallyGallerySize", "${galleryImageUrls.size}")
        }
        return galleryImageUrls
    }
    override fun onCleared(){
        compositeDisposable.clear()
    }
}
