package by.vshkl.android.pik

import android.app.Application
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import io.paperdb.Paper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Paper.init(applicationContext)
        BigImageViewer.initialize(FrescoImageLoader.with(applicationContext))
    }
}