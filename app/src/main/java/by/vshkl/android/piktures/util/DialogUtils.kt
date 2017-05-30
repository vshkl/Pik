package by.vshkl.android.piktures.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.ui.albums.AlbumsRenameListener
import com.afollestad.materialdialogs.MaterialDialog

object DialogUtils {

    fun showAlbumRenameDialog(context: Context, album: Album?, listener: AlbumsRenameListener, albumPosition: Int) {
        MaterialDialog.Builder(context)
                .title(R.string.albums_dialog_rename_title)
                .positiveText(R.string.albums_dialog_rename_ok)
                .negativeText(R.string.all_dialog_cancel)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .contentColor(ContextCompat.getColor(context, android.R.color.primary_text_light))
                .input(context.getString(R.string.albums_dialog_rename_hint), album?.name, false, { _, _ -> })
                .onPositive { dialog, _ ->
                    listener.onAlbumRenamed(album, dialog.inputEditText?.text?.toString()!!, albumPosition)
                }
                .show()
    }
}