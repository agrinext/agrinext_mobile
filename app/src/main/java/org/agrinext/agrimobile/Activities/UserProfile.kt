package org.agrinext.agrimobile.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.agrinext.agrimobile.Activities.FormGeneratorActivity.Companion.DOCTYPE
import org.agrinext.agrimobile.Activities.FormGeneratorActivity.Companion.SHOW_FORM_REQUEST
import org.agrinext.agrimobile.R
import org.jetbrains.anko.sdk25.coroutines.onClick

class UserProfile : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.activity_user_profile, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = activity.findViewById<ImageView>(R.id.ivProfileImage)
        imageView.onClick {
            val intent = Intent(activity.baseContext, FormGeneratorActivity::class.java)
            intent.putExtra(DOCTYPE, "Produce")
            startActivityForResult(intent, SHOW_FORM_REQUEST)
        }
    }
}
