package by.vshkl.android.piktures.model

data class Row(
    var id: Int,
    var data: String,
    var size: Int,
    var displayName: String,
    var mimeType: String,
    var title: String,
    var dateAdded: Int,
    var dateModified: Int,
    var description: String?,
    var picasaId: String?,
    var isPrivate: Int?,
    var latitude: Double?,
    var longitude: Double?,
    var dateTaken: Int,
    var orientation: Int?,
    var miniThumbnailMagic: Int?,
    var bucketId: String,
    var bucketDisplayName: String,
    var width: Int,
    var height: Int
)