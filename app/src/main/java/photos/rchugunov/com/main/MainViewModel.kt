package photos.rchugunov.com.main

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.ApiException
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import com.google.photos.types.proto.Album
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import photos.rchugunov.com.BuildConfig
import photos.rchugunov.com.TokenProvider

class MainViewModel : ViewModel() {

    private lateinit var apiClient: GoogleApiClient
    private lateinit var client: GoogleSignInClient
    private lateinit var tokenProvider: TokenProvider

    private val disposableContainer = CompositeDisposable()

    private val albumsLiveData: MutableLiveData<List<Album>> = MutableLiveData()
    private val logoutLiveData: MutableLiveData<Unit> = MutableLiveData()

    fun initialize(activity: AppCompatActivity) {

        tokenProvider =
            TokenProvider(
                activity.getSharedPreferences(
                    TokenProvider.AUTH_PREFS,
                    Context.MODE_PRIVATE
                )
            )

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))
            .requestEmail()
            .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        client = GoogleSignIn.getClient(activity, gso);

        apiClient = GoogleApiClient.Builder(activity)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

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
            PhotosLibraryClient.initialize(settings).use { photosLibraryClient ->
                // Create a new Album  with at title
                loadAlbums(photosLibraryClient)
            }
        } catch (e: ApiException) {
            Log.e(TAG, e.message, e)
        }
    }

    private fun loadAlbums(photosLibraryClient: PhotosLibraryClient) {
        disposableContainer.add(
            Observable.fromCallable {
                    val albumsList = mutableListOf<Album>()
                    photosLibraryClient.listAlbums().iterateAll().forEach { albumsList.add(it) }
                    albumsList
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { albums -> albumsLiveData.value = albums },
                    { error -> Log.e(TAG, error.message, error) }
                )
        )
    }

    override fun onCleared() {
        disposableContainer.clear()
    }

    fun observeAlbums(): LiveData<List<Album>> = albumsLiveData

    fun observeLogout(): LiveData<Unit> = logoutLiveData

    fun logout() = client.signOut().addOnCompleteListener {
        tokenProvider.clear()
        logoutLiveData.value = Unit
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}