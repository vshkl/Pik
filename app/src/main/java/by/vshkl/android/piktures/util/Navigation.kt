package by.vshkl.android.piktures.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.ui.albums.AlbumsFragment

class Navigation {

    fun navigateToAlbums(activity: FragmentActivity) {
        replaceFragment(activity, AlbumsFragment.newInstance(), false)
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