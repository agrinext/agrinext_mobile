package org.agrinext.agrimobile.Activities

import android.os.Bundle
import org.agrinext.agrimobile.Android.BaseCompactActivity
import org.agrinext.agrimobile.R

class UserProfile : BaseCompactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
    }
}
