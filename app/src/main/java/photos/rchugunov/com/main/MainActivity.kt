package photos.rchugunov.com.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import photos.rchugunov.com.R
import photos.rchugunov.com.ViewModelsFactory


class MainActivity : AppCompatActivity() {

    private val adapter = AlbumsAdapter()
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, ViewModelsFactory).get(MainViewModel::class.java)
        viewModel.initialize(this)
        viewModel.observeAlbums().observe(this, Observer { adapter.albumsList = it })
    }
}
