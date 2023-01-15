package com.mishalpandey.instacloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mishalpandey.instacloneapp.models.Post
import com.mishalpandey.instacloneapp.models.User
import kotlinx.android.synthetic.main.activity_posts.*

private const val TAG ="PostsActivity"
 const val EXTRA_USERNAME ="EXTRA_USERNAME"

open class PostsActivity : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)


        //Create the layout file which represents one post
        // Create data source - Done
        posts = mutableListOf()
        //Create the adapteer
        adapter = PostAdapter(this, posts)
        //Bind the adapter and layout manager to the RV
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        firestoreDb= FirebaseFirestore.getInstance()

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }
        var postReference = firestoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)
        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){
            supportActionBar?.title = username
            postReference= postReference.whereEqualTo("user.username", username)

        }
        postReference.addSnapshotListener{ snapshot, exception ->
            if(exception != null || snapshot == null){
               Log.e(TAG , "Exception when querying posts", exception )
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for( post in postList){
                Log.i(TAG, "Post ${post}")
            }

            fabCreate.setOnClickListener {
                val intent = Intent(this, CreateActivity::class.java)
                startActivity(intent)
            }
        }

//        auth = FirebaseAuth.getInstance()
//
//        val email = intent.getStringExtra("email")
//        val displayName = intent.getStringExtra("name")

//        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName

//        findViewById<Button>(R.id.signOutBtn).setOnClickListener {
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//
//            val googlesigninclient = GoogleSignIn.getClient(this,gso)
//            googlesigninclient.signOut()
//            startActivity(Intent(this , LoginActivity::class.java))
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}