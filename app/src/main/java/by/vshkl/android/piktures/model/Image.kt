package by.vshkl.android.piktures.model

import android.os.Parcel
import android.os.Parcelable

data class Image(
        val id: Long,
        var name: String,
        var image: String,
        val mimeType: String
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Image> = object : Parcelable.Creator<Image> {
            override fun createFromParcel(source: Parcel): Image = Image(source)
            override fun newArray(size: Int): Array<Image?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(image)
        dest.writeString(mimeType)
    }
}