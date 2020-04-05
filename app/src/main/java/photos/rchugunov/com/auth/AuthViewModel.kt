package photos.rchugunov.com.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import photos.rchugunov.com.BuildConfig
import photos.rchugunov.com.OAuth2Repository
import photos.rchugunov.com.TokenProvider

class AuthViewModel(
    private val oauthRepository: OAuth2Repository,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val signInResultData = MutableLiveData<Unit>()
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun observeSignInResult(): LiveData<Unit> = signInResultData

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            exchangeWithAuthToken(account?.serverAuthCode ?: throw IllegalStateException())
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun exchangeWithAuthToken(authCode: String) {
        val disposable = oauthRepository.getAccessToken(
            authCode = authCode,
            clientId = BuildConfig.GOOGLE_CLIENT_ID,
            clientSecret = BuildConfig.GOOGLE_CLIENT_SECRET
        ).subscribeOn(Schedulers.io())
            .subscribe(
                { signInResultData.postValue(Unit) },
                { err -> Log.e(TAG, err.message, err) })

        disposables.add(disposable)
    }

    fun checkSignIn() {
        if (tokenProvider.getToken() != null) {
            signInResultData.value = Unit
        }
    }

    override fun onCleared() {
        disposables.clear()
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}