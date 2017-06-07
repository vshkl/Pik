package by.vshkl.android.pik.local

import by.vshkl.android.pik.model.Album
import io.paperdb.Paper
import io.reactivex.Observable

object Cache {

    private val BOOK_ALBUMS = "BOOK_ALBUMS"
    private val KEY_ALBUMS = "KEY_ALBUMS"

    fun getAlbums(): Observable<List<Album>> = Observable.create<List<Album>> {
        it.onNext(Paper.book(BOOK_ALBUMS).read(KEY_ALBUMS, emptyList()))
    }


    fun putAlbums(albums: List<Album>): Observable<Boolean> = Observable.create {
        it.onNext(Paper.book(BOOK_ALBUMS).write(KEY_ALBUMS, albums) != null)
    }
}