package photos.rchugunov.com.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import okhttp3.*
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import photos.rchugunov.com.BuildConfig
import photos.rchugunov.com.TokenProvider

class AuthViewModel : ViewModel() {

    private lateinit var apiClient: GoogleApiClient
    private lateinit var client: GoogleSignInClient
    private lateinit var tokenProvider: TokenProvider

    private val signInResultData = MutableLiveData<Unit>()

    fun initialize(activity: AppCompatActivity) {
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

        tokenProvider =
            TokenProvider(
                activity.getSharedPreferences(
                    TokenProvider.AUTH_PREFS,
                    Context.MODE_PRIVATE
                )
            )
    }

    fun observeSignInResult(): LiveData<Unit> = signInResultData

    fun signInClient(): Intent = client.signInIntent

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account.takeIf { it?.serverAuthCode != null })
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode())
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        account?.serverAuthCode?.run {
            getAccessToken(
                authCode = this,
                clientId = BuildConfig.GOOGLE_CLIENT_ID,
                clientSecret = BuildConfig.GOOGLE_CLIENT_SECRET
            ) { token ->
                tokenProvider.saveToken(token)
                signInResultData.postValue(Unit)
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getAccessToken(
        authCode: String,
        clientId: String,
        clientSecret: String,
        result: (String) -> Unit
    ) {
        val client = OkHttpClient()
        val requestBody: RequestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("code", authCode)
            .build()
        val request: Request = Request.Builder()
            .url("https://www.googleapis.com/oauth2/v4/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body ?: return
                    val jsonObject = JSONObject(body.string())
                    result(jsonObject.get("access_token").toString())
//                    mTokenType = jsonObject.get("token_type").toString()
//                    mRefreshToken = jsonObject.get("refresh_token").toString()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun checkSignIn() {
        if (tokenProvider.getToken() != null) {
            signInResultData.value = Unit
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}