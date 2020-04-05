package photos.rchugunov.com.album

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class AlbumPresenter(
    private val repository: AlbumRepository
) {
    private var viewReference: WeakReference<AlbumView>? = null
    private val disposableContainer = CompositeDisposable()

    fun attachView(view: AlbumView) {
        viewReference = WeakReference(view)
    }

    fun detachView() {
        viewReference = null
        disposableContainer.clear()
    }

    fun loadAlbum(albumId: String) {
        disposableContainer.add(
            repository.loadAlbum(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { viewReference?.get()?.albumLoaded(it) },
                    { error -> Log.e(TAG, error.message, error) }
                )
        )
    }
}

private const val TAG = "AlbumPresenter"