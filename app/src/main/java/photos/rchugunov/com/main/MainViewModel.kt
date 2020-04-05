package photos.rchugunov.com.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.Album
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import photos.rchugunov.com.BuildConfig
import photos.rchugunov.com.OAuth2Repository
import photos.rchugunov.com.TokenProvider

class MainViewModel(
    private val tokenProvider: TokenProvider,
    private val oauthRepository: OAuth2Repository
) : ViewModel() {

    private val disposableContainer = CompositeDisposable()

    private val albumsLiveData: MutableLiveData<List<Album>> = MutableLiveData()
    private val logoutLiveData: MutableLiveData<Unit> = MutableLiveData()

    @Volatile
    private var client: PhotosLibraryClient

    init {
        client = PhotosLibraryClient.initialize(
            settings(
                tokenProvider.getToken() ?: throw IllegalStateException()
            )
        )
    }

    // Set up the Photos Library Client that interacts with the API
    private fun settings(authToken: String) = PhotosLibrarySettings.newBuilder()
        .setCredentialsProvider(
            FixedCredentialsProvider.create(
                UserCredentials.newBuilder()
                    .setClientId(BuildConfig.GOOGLE_CLIENT_ID)
                    .setClientSecret(BuildConfig.GOOGLE_CLIENT_SECRET)
                    .setAccessToken(
                        AccessToken(authToken, null)
                    )
                    .build()
            )
        )
        .build()

    fun loadAlbums() {
        disposableContainer.add(
            Observable.fromCallable {
                val albumsList = mutableListOf<Album>()
                client.listAlbums().iterateAll().forEach { albumsList.add(it) }
                albumsList
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { albums -> albumsLiveData.value = albums },
                    { error -> reloadToken(error) { loadAlbums() } }
                )
        )
    }

    private fun reloadToken(error: Throwable, result: () -> Unit) {
        val disposable =
            oauthRepository.refreshToken(
                BuildConfig.GOOGLE_CLIENT_ID,
                BuildConfig.GOOGLE_CLIENT_SECRET
            )
                .subscribeOn(Schedulers.io())
                .subscribe(result, { err -> Log.e(TAG, err.message, err) })
        disposableContainer.add(disposable)
    }

    override fun onCleared() {
        disposableContainer.clear()
    }

    fun observeAlbums(): LiveData<List<Album>> = albumsLiveData

    fun observeLogout(): LiveData<Unit> = logoutLiveData

    fun logout() {
        tokenProvider.clear()
        logoutLiveData.value = Unit
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}