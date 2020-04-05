package photos.rchugunov.com

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope

open class BaseActivity : AppCompatActivity() {

    private lateinit var apiClient: GoogleApiClient
    lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        client = GoogleSignIn.getClient(this, gso);

        apiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    fun signInIntent(): Intent = client.signInIntent

    val app: PhotosApp by lazy { application as PhotosApp }
}