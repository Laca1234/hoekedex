package hu.hoekedex.app.data

data class Profile(
    val id: Long = 0,
    val name: String = "",
    val age: Int? = null,
    val nationality: String = "",
    val rating: Float = 0f,
    val description: String = "",
    val images: List<ProfileImage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = name.ifBlank { "Új profil" }

    val thumbnailPath: String?
        get() = images.firstOrNull()?.path
}

data class ProfileImage(
    val id: Long,
    val profileId: Long,
    val path: String,
    val sortOrder: Int
)
