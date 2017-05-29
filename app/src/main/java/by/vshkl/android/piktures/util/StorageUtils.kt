package by.vshkl.android.piktures.util

import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

object StorageUtils {

    fun getStorageUriByPath(path: String?): Uri {
        return when (path?.startsWith(Environment.getExternalStorageDirectory().absolutePath)) {
            true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Images.Media.INTERNAL_CONTENT_URI
        }
    }
}