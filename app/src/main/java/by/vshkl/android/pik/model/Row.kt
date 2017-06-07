package by.vshkl.android.pik.model

data class Row(
    val id: Int,
    val data: String,
    val size: Int,
    val displayName: String,
    val mimeType: String,
    val title: String,
    val dateAdded: Int,
    val dateModified: Int,
    val description: String?,
    val picasaId: String?,
    val isPrivate: Int?,
    val latitude: Double?,
    val longitude: Double?,
    val dateTaken: Int,
    val orientation: Int?,
    val miniThumbnailMagic: Int?,
    val bucketId: String,
    val bucketDisplayName: String,
    val width: Int,
    val height: Int
)