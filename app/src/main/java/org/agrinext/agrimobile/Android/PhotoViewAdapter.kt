package org.agrinext.agrimobile.Android

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.imageURI
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by revant on 28/12/17.
 */

class PhotoViewAdapter(var image_list:JSONArray, imagePreview:SimpleDraweeView): RecyclerView.Adapter<PhotoViewAdapter.ViewHolder>() {
    var imagePreview = imagePreview

    private var mCLickListener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener) {
        mCLickListener = listener
    }

    fun clear() {
        val size = this.image_list.length()
        if (size > 0) {
            for (i in 0 until size) {
                this.image_list.remove(0)
            }
            this.notifyItemRangeRemoved(0, size)

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get list_item (and other fields) from ListItemUI
        val image: SimpleDraweeView = itemView.find(PhotoUIItem.Companion.Ids.listItem)
        // Bind values to image and other fields above
        fun bind(jsonObject: JSONObject?, imagePreview:SimpleDraweeView) {
            val imageUri = Uri.parse(FrappeClient(itemView.context).getServerURL() + jsonObject?.getString("file_url"))
            image.imageURI = imageUri
            image.setOnClickListener {
                imagePreview.imageURI = imageUri
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(PhotoUIItem().createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun getItemCount(): Int {
        // return listing size
        return image_list.length()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var p = position
        while(image_list.length() < p){
            p -= image_list.length()
        }
        val jsonObject = image_list.getJSONObject(p)

        holder!!.bind(jsonObject, imagePreview)

        holder.itemView.setOnClickListener(mCLickListener)
    }
}