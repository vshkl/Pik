package by.vshkl.android.piktures.local

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.ImageColumns.*
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import io.reactivex.Observable
import java.io.File
import java.lang.ref.WeakReference
import java.util.Collections.emptyList

object Repository {

    private val projectionAlbum = arrayOf(_ID, DATA, DISPLAY_NAME, MIME_TYPE)
    private val projectionAlbumThumbnail = arrayOf(BUCKET_ID, DATA)
    private val projectionAlbumCount = arrayOf(BUCKET_ID)
    private val projectionAlbums = arrayOf(BUCKET_ID, BUCKET_DISPLAY_NAME)
    private val projectionImageDelete = DATA + " = ?"

    private val selectByBucketId = BUCKET_ID + " = ?"

    private val sortOrderDateDesc = DATE_MODIFIED + " DESC"
    private val sortOrderDateDescOne = DATE_MODIFIED + " DESC LIMIT 1"

    fun getAlbums(contextRef: WeakReference<Context>): Observable<MutableList<Album>> = Observable.create({ emitter ->
        val albums = getAlbums(contextRef.get()!!, INTERNAL_CONTENT_URI, projectionAlbums, sortOrderDateDesc)
        albums.addAll(getAlbums(contextRef.get()!!, EXTERNAL_CONTENT_URI, projectionAlbums, sortOrderDateDesc))
        contextRef.clear()
        emitter.onNext(albums)
    })

    fun getImages(contextRef: WeakReference<Context>, albumId: String): Observable<MutableList<Image>>
            = Observable.create({ emitter ->
        val images = getImages(contextRef.get(), INTERNAL_CONTENT_URI, projectionAlbum, albumId, sortOrderDateDesc)
        images.addAll(getImages(contextRef.get(), EXTERNAL_CONTENT_URI, projectionAlbum, albumId, sortOrderDateDesc))
        contextRef.clear()
        emitter.onNext(images)
    })

    fun deleteImages(contextRef: WeakReference<Context>, imagePaths: List<String>?): Observable<Int?>
            = Observable.create({ emitter ->
        val deletedImagePaths = deleteImagesFromDisk(imagePaths)
        val deletedRows = deleteImagesFromMediaStore(contextRef.get(), EXTERNAL_CONTENT_URI, projectionImageDelete, deletedImagePaths)
        emitter.onNext(deletedRows)
    })

    //------------------------------------------------------------------------------------------------------------------

    private fun getAlbums(context: Context, storageUri: Uri, projection: Array<String>, sortOrder: String)
            : MutableList<Album> {
        val cursor = context.contentResolver.query(storageUri, projection, null, null, sortOrder)

        val albums: MutableList<Album> = emptyList<Album>().toMutableList()

        if (cursor == null) {
            return albums
        }
        while (cursor.moveToNext()) {
            val album = Album(
                    cursor.getString(cursor.getColumnIndex(BUCKET_ID)),
                    cursor.getString(cursor.getColumnIndex(BUCKET_DISPLAY_NAME))
            )
            if (!albums.contains(album)) {
                albums.add(album)
            }
        }

        albums.forEach { album ->
            album.count = getAlbumEntryCount(context, storageUri, album.id)
            album.thumbnail = getAlbumThumbnail(context, storageUri, album.id)
        }

        cursor.close()
        return albums
    }

    private fun getAlbumEntryCount(context: Context, storageUri: Uri, albumId: String): Int {
        val cursor = context.contentResolver.query(storageUri, projectionAlbumCount,
                selectByBucketId, arrayOf(albumId), sortOrderDateDesc)
        val albumEntryCount = cursor.count
        cursor.close()
        return albumEntryCount
    }

    private fun getAlbumThumbnail(context: Context, storageUri: Uri, albumId: String): String {
        val cursor = context.contentResolver.query(storageUri, projectionAlbumThumbnail,
                selectByBucketId, arrayOf(albumId), sortOrderDateDescOne)
        cursor.moveToFirst()
        val albumThumbnail = cursor.getString(cursor.getColumnIndex(DATA))
        cursor.close()
        return albumThumbnail
    }

    private fun getImages(context: Context?, storageUri: Uri, projection: Array<String>, albumId: String,
                          sortOrder: String): MutableList<Image> {
        val cursor = context?.contentResolver?.query(storageUri, projection, selectByBucketId, arrayOf(albumId), sortOrder)

        val images: MutableList<Image> = emptyList<Image>().toMutableList()

        if (cursor == null) {
            return images
        }
        while (cursor.moveToNext()) {
            val image: Image = Image(
                    cursor.getLong(cursor.getColumnIndex(_ID)),
                    cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)) ?: "",
                    cursor.getString(cursor.getColumnIndex(DATA)),
                    cursor.getString(cursor.getColumnIndex(MIME_TYPE))
            )
            images.add(image)
        }

        cursor.close()
        return images
    }

    private fun deleteImagesFromDisk(imagePaths: List<String>?): List<String>? {
        val deletedImagePaths: MutableList<String> = mutableListOf()
        imagePaths?.forEach {
            val file = File(it)
            if (file.exists()) {
                if (file.delete()) {
                    deletedImagePaths.add(it)
                }
            }
        }
        return deletedImagePaths
    }

    private fun deleteImagesFromMediaStore(context: Context?, storageUri: Uri, projection: String,
                                           imagePaths: List<String>?): Int? {
        return context?.contentResolver?.delete(storageUri, projection, imagePaths?.toTypedArray())
    }
}