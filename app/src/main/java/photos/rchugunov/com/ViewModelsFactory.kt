package photos.rchugunov.com

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object ViewModelsFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.newInstance()
    }
}