package photos.rchugunov.com.album

import io.reactivex.Observable

interface AlbumRepository {
    fun loadAlbum(albumId: String): Observable<Any>
}
