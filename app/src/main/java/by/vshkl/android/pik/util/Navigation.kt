package by.vshkl.android.pik.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewCompat
import android.transition.TransitionInflater
import android.view.View
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.ui.album.AlbumFragment
import by.vshkl.android.pik.ui.albums.AlbumsFragment
import by.vshkl.android.pik.ui.imageinfo.ImageInfoFragment
import by.vshkl.android.pik.ui.imagepager.ImagePagerFragment
import com.yalantis.ucrop.UCrop
import java.io.File

object Navigation {

    private val AUTHORITY: String = "by.vshkl.android.piktures.provider"

    fun navigateToAlbums(activity: FragmentActivity) {
        val albumsFragment = AlbumsFragment.newInstance()
        albumsFragment.enterTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        albumsFragment.exitTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        albumsFragment.allowEnterTransitionOverlap = false
        albumsFragment.allowReturnTransitionOverlap = false
        replaceFragment(activity, AlbumsFragment.newInstance(), false, false, null)
    }

    fun navigateToAlbum(activity: FragmentActivity, album: Album?, startSharedView: View?) {
        val albumFragment = AlbumFragment.newInstance(album)
        albumFragment.enterTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        albumFragment.exitTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        albumFragment.allowEnterTransitionOverlap = false
        albumFragment.allowReturnTransitionOverlap = false
        replaceFragment(activity, albumFragment, true, false, startSharedView)
    }

    fun navigateToImagePager(activity: FragmentActivity, images: List<Image>?, startPosition: Int,
                             shouldReplace: Boolean) {
        val imagePagerFragment = ImagePagerFragment.newInstance(ArrayList(images), startPosition)
        imagePagerFragment.enterTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        imagePagerFragment.exitTransition =
                TransitionInflater.from(activity.baseContext).inflateTransition(android.R.transition.fade)
        imagePagerFragment.allowEnterTransitionOverlap = false
        imagePagerFragment.allowReturnTransitionOverlap = false
        replaceFragment(activity, ImagePagerFragment.newInstance(ArrayList(images), startPosition), true,
                shouldReplace, null)
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

    private fun replaceFragment(activity: FragmentActivity, fragment: Fragment, addToBackStack: Boolean,
                                shouldReplace: Boolean, startSharedView: View?) {
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (shouldReplace) {
            fragmentManager.popBackStack()
        }
        fragmentTransaction.replace(R.id.flFragmentContainer, fragment, fragment.tag)
        if (startSharedView != null) {
            fragmentTransaction.addSharedElement(startSharedView, ViewCompat.getTransitionName(startSharedView))
        }
        when {
            addToBackStack -> fragmentTransaction.addToBackStack(fragment.tag)
            else -> (0..fragmentManager.backStackEntryCount - 1).forEach { fragmentManager.popBackStack() }
        }
        fragmentTransaction.commit()
    }
}