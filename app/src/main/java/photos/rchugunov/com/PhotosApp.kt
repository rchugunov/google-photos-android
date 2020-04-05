package photos.rchugunov.com

import android.app.Application
import android.content.Context

class PhotosApp : Application() {

    val tokenProvider: TokenProvider by lazy {
        TokenProvider(
            getSharedPreferences(
                TokenProvider.AUTH_PREFS,
                Context.MODE_PRIVATE
            )
        )
    }

    val viewModelsFactory: ViewModelsFactory by lazy { ViewModelsFactory(this) }
}