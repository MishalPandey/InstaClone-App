package com.mishalpandey.instacloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

private const val TAG = "ProfileActivity"

class ProfileActivity : PostsActivity(){


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout){
            Log.i(TAG, "User wants to Logout")

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googlesigninclient = GoogleSignIn.getClient(this,gso)
            googlesigninclient.signOut()
            startActivity(Intent(this , LoginActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }
}