package com.example.standardgallery.adapter

import android.app.Activity
import android.content.Intent
import android.transition.Explode
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.standardgallery.R
import com.example.standardgallery.UI.FullImageActivity
import com.example.standardgallery.model.GalleryPicture

class GalleryPictureAdapter(
    private val pictures: ArrayList<GalleryPicture>,
    private val activity: Activity
) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item_adapter, parent, false)

        return ViewHolder(viewHolder)
    }

    fun populateGallery(imagesLoaded: List<GalleryPicture>?) {
        if (imagesLoaded != null)
            pictures.addAll(imagesLoaded)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picture = pictures[position]

        Glide.with(holder.photoImageView.context).load(picture.path).into(holder.photoImageView)
        holder.photoImageView.setOnClickListener {
            val intent = Intent(activity, FullImageActivity::class.java)
            intent.putExtra("EXTRA_PATH", picture.path)

            activity.window.exitTransition = Fade()
            it.context.startActivity(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    it,
                    activity.getString(R.string.transitionName)
                ).toBundle()
            )
        }
        holder.photoImageView.setOnLongClickListener {
            if (picture.isSelected){
                holder.itemSelected.visibility = View.GONE
                picture.isSelected = false
            }else{
                holder.itemSelected.visibility = View.VISIBLE
                picture.isSelected = true
            }
            return@setOnLongClickListener true
        }


    }

    override fun getItemCount(): Int = pictures.size

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var photoImageView: ImageView = itemView.findViewById(R.id.imageView_adapter)
    var itemSelected: View = itemView.findViewById(R.id.image_selected)
}
