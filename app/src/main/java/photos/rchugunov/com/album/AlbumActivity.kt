package photos.rchugunov.com.album

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import io.reactivex.Observable

class AlbumActivity : AppCompatActivity(), AlbumView {

    private val albumId by lazy {
        intent.getStringExtra(ALBUM_ID_KEY)
            ?: throw IllegalArgumentException("AlbumId can't be null")
    }

    private lateinit var presenter: AlbumPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = lastCustomNonConfigurationInstance as? AlbumPresenter ?: AlbumPresenter(object :
            AlbumRepository {
            override fun loadAlbum(albumId: String): Observable<Any> = Observable.just(Any())
        })
    }

    override fun onStart() {
        super.onStart()

        presenter.attachView(this)

        presenter.loadAlbum(albumId)
    }

    override fun onStop() {
        super.onStop()

        presenter.detachView()
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? {
        return presenter
    }

    override fun albumLoaded(album: Any?) {

    }

    companion object {

        private const val ALBUM_ID_KEY = "ALBUM_ID_KEY"

        fun startActivity(context: Activity, albumId: String) {
            val intent = Intent(context, AlbumActivity::class.java)
            val bundle: Bundle = bundleOf(ALBUM_ID_KEY to albumId)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}