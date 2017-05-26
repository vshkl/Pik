package by.vshkl.android.piktures.util

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import by.vshkl.android.piktures.ui.album.AlbumFragment
import by.vshkl.android.piktures.ui.albums.AlbumsFragment
import by.vshkl.android.piktures.ui.imagepager.ImagePagerFragment
import java.io.File

object Navigation {

    private val AUTHORITY: String = "by.vshkl.android.piktures.provider"

    fun navigateToAlbums(activity: FragmentActivity) {
        replaceFragment(activity, AlbumsFragment.newInstance(), false)
    }

    fun navigateToAlbum(activity: FragmentActivity, album: Album?) {
        replaceFragment(activity, AlbumFragment.newInstance(album), true)
    }

    fun navigateToImagePager(activity: FragmentActivity, images: List<Image>?, startPosition: Int) {
        replaceFragment(activity, ImagePagerFragment.newInstance(ArrayList(images), startPosition), true)
    }

    fun shareImages(context: Context, imagePaths: List<String>?) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                ArrayList(imagePaths?.map { FileProvider.getUriForFile(context, AUTHORITY, File(it)) }))
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.all_action_share_title)))
    }

    private fun replaceFragment(activity: FragmentActivity, fragment: Fragment, addToBackStack: Boolean) {
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragmentContainer, fragment, fragment.tag)
        when {
            addToBackStack -> fragmentTransaction.addToBackStack(fragment.tag)
            else -> (0..fragmentManager.backStackEntryCount - 1).forEach { fragmentManager.popBackStack() }
        }
        fragmentTransaction.commit()
    }
}