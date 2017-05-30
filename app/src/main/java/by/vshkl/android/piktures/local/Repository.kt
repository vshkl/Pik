package by.vshkl.android.piktures.local

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.ImageColumns.*
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import by.vshkl.android.piktures.model.Row
import by.vshkl.android.piktures.util.StorageUtils
import io.reactivex.Observable
import java.io.File
import java.lang.ref.WeakReference
import java.util.Collections.emptyList

object Repository {

    private val projectionAlbum = arrayOf(_ID, DATA, DISPLAY_NAME, MIME_TYPE)
    private val projectionAlbumThumbnail = arrayOf(BUCKET_ID, DATA)
    private val projectionAlbumRename = arrayOf(BUCKET_ID, DATA, BUCKET_DISPLAY_NAME)
    private val projectionAlbumCount = arrayOf(BUCKET_ID)
    private val projectionAlbums = arrayOf(BUCKET_ID, BUCKET_DISPLAY_NAME)
    private val projectionImageDelete = DATA + " = ?"
    private val projectionAll = arrayOf(_ID, DATA, SIZE, DISPLAY_NAME, MIME_TYPE, TITLE, DATE_ADDED, DATE_MODIFIED,
            DESCRIPTION, PICASA_ID, IS_PRIVATE, LATITUDE, LONGITUDE, DATE_TAKEN, ORIENTATION, MINI_THUMB_MAGIC,
            BUCKET_ID, BUCKET_DISPLAY_NAME, WIDTH, HEIGHT)

    private val selectByBucketId = BUCKET_ID + " = ?"

    private val sortOrderDateDesc = DATE_MODIFIED + " DESC"
    private val sortOrderDateDescOne = DATE_MODIFIED + " DESC LIMIT 1"

    fun getAlbums(contextRef: WeakReference<Context>): Observable<MutableList<Album>> = Observable.create({ emitter ->
        val albums = getAlbums(contextRef.get()!!, INTERNAL_CONTENT_URI, projectionAlbums, sortOrderDateDesc)
        albums.addAll(getAlbums(contextRef.get()!!, EXTERNAL_CONTENT_URI, projectionAlbums, sortOrderDateDesc))
        contextRef.clear()
        emitter.onNext(albums)
    })

    fun getImages(contextRef: WeakReference<Context>, album: Album?): Observable<MutableList<Image>>
            = Observable.create({ emitter ->
        when (album) {
            null -> emitter.onNext(emptyList())
            else -> {
                val images = getImages(contextRef.get(), album.storageUri, projectionAlbum, album.id, sortOrderDateDesc)
                contextRef.clear()
                emitter.onNext(images)
            }
        }
    })

    fun deleteImages(contextRef: WeakReference<Context>, imagePaths: List<String>?): Observable<Int?>
            = Observable.create({ emitter ->
        val storageUri = StorageUtils.getStorageUriByPath(imagePaths?.get(0))
        val deletedImagePaths = deleteImagesFromDisk(imagePaths)
        var deletedRows = 0
        deletedImagePaths?.forEach {
            deletedRows += deleteImagesFromMediaStore(contextRef.get(), storageUri, projectionImageDelete, it) ?: 0
        }
        contextRef.clear()
        emitter.onNext(deletedRows)
    })

    fun deleteAlbums(contextRef: WeakReference<Context>, albums: List<Album>?): Observable<Int?>
            = Observable.create({ emitter ->
        var deletedRows = 0
        albums?.forEach {
            deletedRows += deleteAlbumFromDisk(contextRef.get(), it.storageUri, projectionAlbumThumbnail, it.id)
        }
        contextRef.clear()
        emitter.onNext(deletedRows)
    })

    fun renameAlbum(contextRef: WeakReference<Context>, album: Album?, newName: String): Observable<Int> =
            Observable.create({ emitter ->
                val renamedRows = renameAlbum(contextRef.get(), album?.storageUri!!, projectionAll, album,
                        newName)
                contextRef.clear()
                emitter.onNext(renamedRows)
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
                    storageUri,
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
            if (file.exists() && file.isFile) {
                if (file.delete()) {
                    deletedImagePaths.add(it)
                }
            }
        }
        return deletedImagePaths
    }

    private fun deleteImagesFromMediaStore(context: Context?, storageUri: Uri, projection: String,
                                           imagePath: String?): Int? {
        return context?.contentResolver?.delete(storageUri, projection, arrayOf(imagePath))
    }

    private fun deleteAlbumFromDisk(context: Context?, storageUri: Uri, projection: Array<String>?,
                                    albumIds: String): Int {
        val imagePathsToDelete: MutableList<String> = mutableListOf()
        val albumFileToDelete: File

        val cursor = context?.contentResolver?.query(storageUri, projection, selectByBucketId, arrayOf(albumIds), sortOrderDateDesc)

        if (cursor == null) {
            return 0
        }
        while (cursor.moveToNext()) {
            val imagePath = cursor.getString(cursor.getColumnIndex(DATA))
            if (!imagePathsToDelete.contains(imagePath)) {
                imagePathsToDelete.add(imagePath)
            }
        }

        cursor.close()

        albumFileToDelete = File(imagePathsToDelete[0]).parentFile

        deleteImagesFromDisk(imagePathsToDelete)?.forEach {
            deleteImagesFromMediaStore(context, storageUri, projectionImageDelete, it)
        }

        albumFileToDelete.takeIf { it.listFiles().isEmpty() }?.delete()

        return 1
    }

    private fun renameAlbum(context: Context?, storageUri: Uri, projection: Array<String>, album: Album?,
                            newName: String): Int {
        val cursor = context?.contentResolver?.query(storageUri, projection, selectByBucketId, arrayOf(album?.id),
                sortOrderDateDesc)

        val rows: MutableList<Row> = emptyList<Row>().toMutableList()

        if (cursor == null) {
            return 0
        }
        while (cursor.moveToNext()) {
            val row = Row(
                    cursor.getInt(cursor.getColumnIndex(_ID)),
                    cursor.getString(cursor.getColumnIndex(DATA)),
                    cursor.getInt(cursor.getColumnIndex(SIZE)),
                    cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MIME_TYPE)),
                    cursor.getString(cursor.getColumnIndex(TITLE)),
                    cursor.getInt(cursor.getColumnIndex(DATE_ADDED)),
                    cursor.getInt(cursor.getColumnIndex(DATE_MODIFIED)),
                    cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(PICASA_ID)),
                    cursor.getInt(cursor.getColumnIndex(IS_PRIVATE)),
                    cursor.getDouble(cursor.getColumnIndex(LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(LONGITUDE)),
                    cursor.getInt(cursor.getColumnIndex(DATE_TAKEN)),
                    cursor.getInt(cursor.getColumnIndex(ORIENTATION)),
                    cursor.getInt(cursor.getColumnIndex(MINI_THUMB_MAGIC)),
                    cursor.getString(cursor.getColumnIndex(BUCKET_ID)),
                    cursor.getString(cursor.getColumnIndex(BUCKET_DISPLAY_NAME)),
                    cursor.getInt(cursor.getColumnIndex(WIDTH)),
                    cursor.getInt(cursor.getColumnIndex(HEIGHT))
            )
            rows.add(row)
        }
        cursor.close()

        rows.forEach { println(it) }

        val albumFile = File(rows[0].data).parentFile
        albumFile.takeIf { it.isDirectory }?.renameTo(File(albumFile.parent + File.separator + newName))

        rows.forEach { deleteImagesFromMediaStore(context, storageUri, projectionImageDelete, it.data) }

        var insertedRows = 0
        val contentValue = ContentValues()
        rows.forEach {
            contentValue.clear()
            contentValue.put(_ID, it.id)
            contentValue.put(DATA, it.data.replace(album?.name!!, newName, false))
            contentValue.put(SIZE, it.size)
            contentValue.put(DISPLAY_NAME, it.displayName)
            contentValue.put(MIME_TYPE, it.mimeType)
            contentValue.put(TITLE, it.title)
            contentValue.put(DATE_ADDED, it.dateAdded)
            contentValue.put(DATE_MODIFIED, it.dateModified)
            contentValue.put(DESCRIPTION, it.description)
            contentValue.put(PICASA_ID, it.picasaId)
            contentValue.put(IS_PRIVATE, it.isPrivate)
            contentValue.put(LATITUDE, it.latitude)
            contentValue.put(LONGITUDE, it.longitude)
            contentValue.put(DATE_TAKEN, it.dateTaken)
            contentValue.put(ORIENTATION, it.orientation)
            contentValue.put(MINI_THUMB_MAGIC, it.miniThumbnailMagic)
            contentValue.put(BUCKET_ID, it.bucketId)
            contentValue.put(BUCKET_DISPLAY_NAME, newName)
            contentValue.put(WIDTH, it.width)
            contentValue.put(HEIGHT, it.height)
            context.contentResolver.insert(storageUri, contentValue)
                    .takeIf { it != null }
                    ?.apply { insertedRows += 1 }
        }

        return insertedRows
    }
}