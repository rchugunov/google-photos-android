package photos.rchugunov.com

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import photos.rchugunov.com.auth.AuthViewModel
import photos.rchugunov.com.main.MainViewModel

class ViewModelsFactory(private val app: PhotosApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass == AuthViewModel::class.java -> AuthViewModel(
                oauthRepository = OAuth2Repository(app.tokenProvider),
                tokenProvider = app.tokenProvider
            ) as T
            modelClass == MainViewModel::class.java -> MainViewModel(
                oauthRepository = OAuth2Repository(app.tokenProvider),
                tokenProvider = app.tokenProvider
            ) as T
            else -> modelClass.newInstance()
        }
    }
}