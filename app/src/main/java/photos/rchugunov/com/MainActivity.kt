package photos.rchugunov.com

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.ApiException
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Set up the Photos Library Client that interacts with the API
        val settings = PhotosLibrarySettings.newBuilder()
            .setCredentialsProvider(
                FixedCredentialsProvider.create(
                    UserCredentials.newBuilder()
                        .setClientId(BuildConfig.GOOGLE_CLIENT_ID)
                        .setClientSecret(BuildConfig.GOOGLE_CLIENT_SECRET)
                        .setAccessToken(
                            AccessToken((application as PhotosApp).accessToken, null)
                        )
                        .build()
                )
            )
            .build()

        try {
            PhotosLibraryClient.initialize(settings).use { photosLibraryClient ->

                // Create a new Album  with at title
                val album = photosLibraryClient.createAlbum("My Album")

                // Get some properties from the album, such as its ID and product URL
                val id: String = album.getId()
                val url: String = album.getProductUrl()
            }
        } catch (e: ApiException) {
            // Error during album creation
        }
    }
}
