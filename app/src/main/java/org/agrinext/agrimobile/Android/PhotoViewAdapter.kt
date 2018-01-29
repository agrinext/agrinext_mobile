package org.agrinext.agrimobile.Android

import android.accounts.AccountManager
import android.content.DialogInterface
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.mntechnique.otpmobileauth.auth.AuthReqCallback
import org.agrinext.agrimobile.Activities.ListingActivity
import org.agrinext.agrimobile.Activities.PhotosActivity
import org.agrinext.agrimobile.BuildConfig
import org.agrinext.agrimobile.R
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.support.v4.startActivity
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
                imagePreview.setOnClickListener {
                    setOrDeleteImage(jsonObject?.getString("name"), jsonObject?.getString("file_url"))
                }
            }
        }

        fun setOrDeleteImage(name:String?, fileUrl:String?) {
            val items = arrayOf<CharSequence>(
                    itemView.context.getString(R.string.set_photo),
                    itemView.context.getString(R.string.delete_photo),
                    itemView.context.getString(R.string.cancel)
            )
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle(itemView.context.getString(R.string.select_photo))
            builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
                if (items[item] == itemView.context.getString(R.string.set_photo)) {
                    setPhoto(fileUrl)
                } else if (items[item] == itemView.context.getString(R.string.delete_photo)) {
                    deletePhoto(name)
                } else if (items[item] == itemView.context.getString(R.string.cancel)) {
                    dialog.dismiss()
                }
            })
            builder.show()
        }

        fun setPhoto(fileUrl:String?) {
            val mAccountManager = AccountManager.get(itemView.context)
            val accounts = mAccountManager?.getAccountsByType(BuildConfig.APPLICATION_ID)
            val user = accounts!![0].name
            val request = OAuthRequest(Verb.PUT, FrappeClient(itemView.context)?.getServerURL() + "/api/resource/User/" + user)
            request.addBodyParameter("data","{\"user_image\":\"${fileUrl}\"}")
            val callback = object : AuthReqCallback {
                override fun onSuccessResponse(result: String) {
                    Log.d("SUCCESS!", result)
                    (itemView.context as AppCompatActivity).finish()
                }

                override fun onErrorResponse(error: String) {
                    Log.d("ERROR!", error)
                }

            }
            FrappeClient(itemView.context)?.executeRequest(request, callback)
        }

        fun deletePhoto(name:String?) {
            val request = OAuthRequest(Verb.DELETE, FrappeClient(itemView.context)?.getServerURL() + "/api/resource/File/" + name)
            val callback = object : AuthReqCallback {
                override fun onSuccessResponse(result: String) {
                    Log.d("SUCCESS!", result)
                    (itemView.context as AppCompatActivity).finish()
                }

                override fun onErrorResponse(error: String) {
                    Log.d("ERROR!", error)
                }

            }
            FrappeClient(itemView.context)?.executeRequest(request, callback)
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