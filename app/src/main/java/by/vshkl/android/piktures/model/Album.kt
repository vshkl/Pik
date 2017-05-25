package by.vshkl.android.piktures.model

import android.os.Parcel
import android.os.Parcelable

data class Album(
        var id: String,
        var name: String,
        var thumbnail: String = "",
        var count: Int = 0
) : Parcelable {

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Album> = object : Parcelable.Creator<Album> {
            override fun createFromParcel(source: Parcel): Album = Album(source)
            override fun newArray(size: Int): Array<Album?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(thumbnail)
        dest.writeInt(count)
    }
}