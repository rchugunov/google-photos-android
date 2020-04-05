package photos.rchugunov.com.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.photos.types.proto.Album
import kotlinx.android.synthetic.main.activity_main.*
import photos.rchugunov.com.BaseActivity
import photos.rchugunov.com.R
import photos.rchugunov.com.album.AlbumActivity
import photos.rchugunov.com.auth.AuthActivity


class MainActivity : BaseActivity() {

    private val adapter = AlbumsAdapter(::openAlbumActivity)

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            app.viewModelsFactory
        ).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        recyclerView.adapter = adapter
        viewModel.loadAlbums()
        viewModel.observeAlbums()
            .observe(this, Observer { albums: List<Album> -> adapter.albumsList = albums })
        viewModel.observeLogout().observe(this, Observer {
            client.revokeAccess().continueWithTask {
                client.signOut()
            }.addOnCompleteListener {
                finish()
                startActivity(Intent(applicationContext, AuthActivity::class.java))
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun openAlbumActivity(albumId: String) {
        AlbumActivity.startActivity(this, albumId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnLogout -> {
                viewModel.logout()
            }
        }
        return true
    }
}
