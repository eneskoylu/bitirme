package com.eneskoylu.bitirme2.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eneskoylu.bitirme2.R
import com.eneskoylu.bitirme2.adapter.FeedRecyclerAdapter
import com.eneskoylu.bitirme2.databinding.ActivityFeedBinding
import com.eneskoylu.bitirme2.model.Adverta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var advertArrayList : ArrayList<Adverta>
    private lateinit var feedAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        advertArrayList = ArrayList<Adverta>()

        getData()

        var layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager


        val adapter = FeedRecyclerAdapter(advertArrayList)
        binding.recyclerView.adapter = adapter

        //binding.recyclerView.layoutManager = LinearLayoutManager(this)
       // feedAdapter = FeedRecyclerAdapter(advertArrayList)
        //binding.recyclerView.adapter = feedAdapter


    }

    private fun getData(){

        db.collection("Advert").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        val documents = snapshot.documents

                        advertArrayList.clear()

                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val address = document.get("address") as String
                            val city = document.get("city") as String
                            val district = document.get("district") as String
                            val iletme = document.get("iletme") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            //val longitude = document.get("longitude") as Double
                            //val latitude = document.get("latitude") as Double

                            val advert = Adverta(userEmail,comment,address,city,district,iletme,downloadUrl)
                            advertArrayList.add(advert)

                           // val location = Location(latitude,longitude)
                            //locationArrayList.add(location)

                        }
                        feedAdapter.notifyDataSetChanged()

                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_advert){
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }else if (item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return super.onOptionsItemSelected(item)
    }

}