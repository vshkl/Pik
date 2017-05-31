package by.vshkl.android.pik.local

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Images.ImageColumns.*
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.model.Row
import by.vshkl.android.pik.util.StorageUtils
import io.reactivex.Observable
import java.io.File
import java.lang.ref.WeakReference
import java.util.Collections.emptyList

object Repository {

    private val projectionAlbumContent = arrayOf(_ID, DATA, DISPLAY_NAME, MIME_TYPE)
    private val projectionAlbumImage = arrayOf(BUCKET_ID, DATA)
    private val projectionAlbumId = arrayOf(BUCKET_ID)
    private val projectionAlbums = arrayOf(BUCKET_ID, BUCKET_DISPLAY_NAME)
    private val projectionImageDelete = DATA + " = ?"
    private val projectionAll = arrayOf(_ID, DATA, SIZE, DISPLAY_NAME, MIME_TYPE, TITLE, DATE_ADDED, DATE_MODIFIED,
            DESCRIPTION, PICASA_ID, IS_PRIVATE, LATITUDE, LONGITUDE, DATE_TAKEN, ORIENTATION, MINI_THUMB_MAGIC,
            BUCKET_ID, BUCKET_DISPLAY_NAME, WIDTH, HEIGHT)

    private val selectByBucketId = BUCKET_ID + " = ?"
    private val selectByImagePath = DATA + " = ?"
    private val selectByBucketDisplayName = BUCKET_DISPLAY_NAME + " = ?"

    private val sortOrderDateDesc = DATE_MODIFIED + " DESC"
    private val sortOrderDateDescOne = DATE_MODIFIED + " DESC LIMIT 1"

    fun getAlbums(contextRef: WeakReference<Context>): Observable<MutableList<Album>>
            = Observable.create({ emitter ->
        val albums: MutableList<Album> = mutableListOf()
        when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> albums.addAll(getAlbums(contextRef.get()!!, EXTERNAL_CONTENT_URI))
            else -> albums.addAll(getAlbums(contextRef.get()!!, INTERNAL_CONTENT_URI))
        }
        contextRef.clear()
        emitter.onNext(albums)
    })

    fun getImages(contextRef: WeakReference<Context>, album: Album?): Observable<MutableList<Image>>
            = Observable.create({ emitter ->
        when (album) {
            null -> emitter.onNext(emptyList())
            else -> {
                val images = getImagesByAlbumId(contextRef.get(), album.storageUri, album.id)
                contextRef.clear()
                emitter.onNext(images)
            }
        }
    })

    fun getImages(contextRef: WeakReference<Context>, imagePath: String): Observable<MutableList<Image>>
            = Observable.create({ emitter ->
        val images: MutableList<Image> = mutableListOf()
        when (Environment.getExternalStorageState()) {
            Environment.MEDIA_MOUNTED -> images.addAll(getImagesByImagePath(contextRef.get()!!, EXTERNAL_CONTENT_URI, imagePath))
            else -> images.addAll(getImagesByImagePath(contextRef.get()!!, EXTERNAL_CONTENT_URI, imagePath))
        }
        contextRef.clear()
        emitter.onNext(images)
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
            deletedRows += deleteAlbumFromDisk(contextRef.get(), it.storageUri, it.id)
        }
        contextRef.clear()
        emitter.onNext(deletedRows)
    })

    fun renameAlbum(contextRef: WeakReference<Context>, album: Album?, newName: String): Observable<String>
            = Observable.create({ emitter ->
        val renamedRows = renameAlbum(contextRef.get(), album?.storageUri!!, album, newName)
        contextRef.clear()
        emitter.onNext(renamedRows)
    })

    //------------------------------------------------------------------------------------------------------------------

    private fun getAlbums(context: Context, storageUri: Uri)
            : MutableList<Album> {
        val cursor = context.contentResolver.query(storageUri, projectionAlbums, null, null, sortOrderDateDesc)

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
        val cursor = context.contentResolver.query(storageUri, projectionAlbumId,
                selectByBucketId, arrayOf(albumId), sortOrderDateDesc)
        val albumEntryCount = cursor.count
        cursor.close()
        return albumEntryCount
    }

    private fun getAlbumThumbnail(context: Context, storageUri: Uri, albumId: String): String {
        val cursor = context.contentResolver.query(storageUri, projectionAlbumImage,
                selectByBucketId, arrayOf(albumId), sortOrderDateDescOne)
        cursor.moveToFirst()
        val albumThumbnail = cursor.getString(cursor.getColumnIndex(DATA))
        cursor.close()
        return albumThumbnail
    }

    private fun getImagesByAlbumId(context: Context?, storageUri: Uri, albumId: String): MutableList<Image> {
        val cursor = context?.contentResolver?.query(storageUri, projectionAlbumContent, selectByBucketId, arrayOf(albumId),
                sortOrderDateDesc)

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

    private fun getImagesByImagePath(context: Context, storageUri: Uri, imagePath: String): MutableList<Image> {
        var cursor = context.contentResolver.query(storageUri, projectionAlbumImage, selectByImagePath,
                arrayOf(imagePath), sortOrderDateDescOne)

        if (cursor == null) {
            return emptyList()
        }
        cursor.moveToFirst()
        val albumId = cursor.getString(cursor.getColumnIndex(BUCKET_ID))
        cursor.close()

        cursor = context.contentResolver.query(storageUri, projectionAlbumContent, selectByBucketId,
                arrayOf(albumId), sortOrderDateDesc)

        if (cursor == null) {
            return emptyList()
        }
        val images: MutableList<Image> = mutableListOf()
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

    private fun deleteAlbumFromDisk(context: Context?, storageUri: Uri, albumIds: String): Int {
        val imagePathsToDelete: MutableList<String> = mutableListOf()
        val albumFileToDelete: File

        val cursor = context?.contentResolver?.query(storageUri, projectionAlbumImage, selectByBucketId,
                arrayOf(albumIds), sortOrderDateDesc)

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

    private fun renameAlbum(context: Context?, storageUri: Uri, album: Album?, newName: String): String {
        val cursor = context?.contentResolver?.query(storageUri, projectionAll, selectByBucketId, arrayOf(album?.id),
                sortOrderDateDesc)

        val rows: MutableList<Row> = emptyList<Row>().toMutableList()

        if (cursor == null) {
            return album?.id!!
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

        val albumFile = File(rows[0].data).parentFile
        albumFile.takeIf { it.isDirectory }?.renameTo(File(albumFile.parent + File.separator + newName))

        rows.forEach { deleteImagesFromMediaStore(context, storageUri, projectionImageDelete, it.data) }

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
        }

        return getAlbumId(context, storageUri, newName)
    }

    private fun getAlbumId(context: Context, storageUri: Uri, albumName: String): String {
        val cursor = context.contentResolver.query(storageUri, projectionAlbums, selectByBucketDisplayName,
                arrayOf(albumName), sortOrderDateDescOne)
        cursor.moveToFirst()
        val albumId = cursor.getString(cursor.getColumnIndex(BUCKET_ID))
        cursor.close()
        return albumId
    }
}