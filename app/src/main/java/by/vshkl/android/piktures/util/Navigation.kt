package by.vshkl.android.piktures.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import by.vshkl.android.piktures.ui.album.AlbumFragment
import by.vshkl.android.piktures.ui.albums.AlbumsFragment
import by.vshkl.android.piktures.ui.imageinfo.ImageInfoFragment
import by.vshkl.android.piktures.ui.imagepager.ImagePagerFragment
import com.yalantis.ucrop.UCrop
import java.io.File
import android.support.v4.content.ContextCompat



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

    fun showImageInfoDialog(activity: FragmentActivity, imagePath: String?) {
        val fragment = ImageInfoFragment.newInstance(imagePath)
        fragment.show(activity.supportFragmentManager, fragment.tag)
    }

    fun shareImages(context: Context, imagePaths: List<String>?) {
        val intent = Intent()
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        when (imagePaths?.size) {
            1 -> {
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_STREAM,
                        FileProvider.getUriForFile(context, AUTHORITY, File(imagePaths[0])))
            }
            else -> {
                intent.action = Intent.ACTION_SEND_MULTIPLE
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        ArrayList(imagePaths?.map { FileProvider.getUriForFile(context, AUTHORITY, File(it)) }))
            }
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.all_action_share_title)))
    }

    fun editImage(context: Context, fragment: Fragment, requestCode: Int, imagePath: String?) {
        val options = UCrop.Options()
        options.setStatusBarColor(ContextCompat.getColor(context, android.R.color.black))
        options.setToolbarColor(ContextCompat.getColor(context, android.R.color.black))
        options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorAccent))
        options.setCompressionQuality(100)
        UCrop.of(Uri.fromFile(File(imagePath)), Uri.fromFile(File(imagePath)))
                .withOptions(options)
                .start(context, fragment, requestCode)
    }

    fun openInMap(context: Context, locationUri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = locationUri
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
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