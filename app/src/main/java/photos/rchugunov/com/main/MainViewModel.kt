package photos.rchugunov.com.main

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.ApiException
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.Album
import photos.rchugunov.com.BuildConfig
import photos.rchugunov.com.TokenProvider

class MainViewModel : ViewModel() {

    private lateinit var tokenProvider: TokenProvider
    private val albumsLiveData: MutableLiveData<List<Album>> = MutableLiveData()

    fun initialize(activity: AppCompatActivity) {

        tokenProvider =
            TokenProvider(
                activity.getSharedPreferences(
                    TokenProvider.AUTH_PREFS,
                    Context.MODE_PRIVATE
                )
            )
        val token = tokenProvider.getToken()

        // Set up the Photos Library Client that interacts with the API
        val settings = PhotosLibrarySettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(
                    UserCredentials.newBuilder()
                        .setClientId(BuildConfig.GOOGLE_CLIENT_ID)
                        .setClientSecret(BuildConfig.GOOGLE_CLIENT_SECRET)
                        .setAccessToken(
                            AccessToken(token, null)
                        )
                        .build()
                )
            )
            .build()

        try {
            val albumsList = mutableListOf<Album>()
            PhotosLibraryClient.initialize(settings).use { photosLibraryClient ->
                // Create a new Album  with at title
                photosLibraryClient.listAlbums().iterateAll().forEach { albumsList.add(it) }
                albumsLiveData.value = albumsList
            }
        } catch (e: ApiException) {
            Log.e(TAG, e.message, e)
        }
    }

    fun observeAlbums(): LiveData<List<Album>> = albumsLiveData

    companion object {
        private const val TAG = "MainViewModel"
    }
}