package hu.hoekedex.app.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.util.UUID

class ProfileRepository(private val context: Context) {
    private val database = HoekedexDatabase(context.applicationContext)
    private val imageDirectory: File
        get() = File(context.filesDir, IMAGE_DIRECTORY).apply { mkdirs() }

    fun getProfiles(): List<Profile> {
        val db = database.readableDatabase
        val profiles = mutableListOf<Profile>()

        db.query(
            "profiles",
            null,
            null,
            null,
            null,
            null,
            "updated_at DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                profiles += cursor.toProfile()
            }
        }

        if (profiles.isEmpty()) return emptyList()

        val images = getImagesForProfiles(db, profiles.map { it.id })
        return profiles.map { profile ->
            profile.copy(images = images[profile.id].orEmpty())
        }
    }

    fun createEmptyProfile(): Profile {
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("name", "")
            putNull("age")
            put("nationality", "")
            put("rating", 0f)
            put("description", "")
            put("created_at", now)
            put("updated_at", now)
        }

        val id = database.writableDatabase.insertOrThrow("profiles", null, values)
        return Profile(id = id, createdAt = now, updatedAt = now)
    }

    fun updateProfile(profile: Profile) {
        val values = ContentValues().apply {
            put("name", profile.name.trim())
            if (profile.age == null) putNull("age") else put("age", profile.age)
            put("nationality", profile.nationality.trim())
            put("rating", profile.rating.coerceIn(0f, 5f))
            put("description", profile.description.trim())
            put("updated_at", System.currentTimeMillis())
        }

        database.writableDatabase.update(
            "profiles",
            values,
            "id = ?",
            arrayOf(profile.id.toString())
        )
    }

    fun deleteProfile(profile: Profile) {
        profile.images.forEach { deleteFileQuietly(it.path) }
        database.writableDatabase.delete("profiles", "id = ?", arrayOf(profile.id.toString()))
    }

    fun addImages(profileId: Long, uris: List<Uri>) {
        if (uris.isEmpty()) return

        val db = database.writableDatabase
        var order = maxImageOrder(db, profileId) + 1
        val now = System.currentTimeMillis()

        db.beginTransaction()
        try {
            uris.forEach { uri ->
                val copiedPath = copyUriToPrivateStorage(uri)
                val values = ContentValues().apply {
                    put("profile_id", profileId)
                    put("path", copiedPath)
                    put("sort_order", order++)
                    put("created_at", now)
                }
                db.insertOrThrow("profile_images", null, values)
            }
            touchProfile(db, profileId)
            db.setTransactionSuccessful()
        } catch (error: Throwable) {
            throw error
        } finally {
            db.endTransaction()
        }
    }

    fun deleteImage(image: ProfileImage) {
        val db = database.writableDatabase
        db.beginTransaction()
        try {
            db.delete(
                "profile_images",
                "id = ?",
                arrayOf(image.id.toString())
            )
            touchProfile(db, image.profileId)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        deleteFileQuietly(image.path)
    }

    private fun getImagesForProfiles(
        db: SQLiteDatabase,
        profileIds: List<Long>
    ): Map<Long, List<ProfileImage>> {
        val result = mutableMapOf<Long, MutableList<ProfileImage>>()
        val placeholders = profileIds.joinToString(",") { "?" }
        val args = profileIds.map { it.toString() }.toTypedArray()

        db.rawQuery(
            """
            SELECT id, profile_id, path, sort_order
            FROM profile_images
            WHERE profile_id IN ($placeholders)
            ORDER BY profile_id ASC, sort_order ASC, id ASC
            """.trimIndent(),
            args
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val image = ProfileImage(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    profileId = cursor.getLong(cursor.getColumnIndexOrThrow("profile_id")),
                    path = cursor.getString(cursor.getColumnIndexOrThrow("path")),
                    sortOrder = cursor.getInt(cursor.getColumnIndexOrThrow("sort_order"))
                )
                result.getOrPut(image.profileId) { mutableListOf() }.add(image)
            }
        }

        return result
    }

    private fun maxImageOrder(db: SQLiteDatabase, profileId: Long): Int {
        db.rawQuery(
            "SELECT MAX(sort_order) FROM profile_images WHERE profile_id = ?",
            arrayOf(profileId.toString())
        ).use { cursor ->
            if (!cursor.moveToFirst() || cursor.isNull(0)) return -1
            return cursor.getInt(0)
        }
    }

    private fun copyUriToPrivateStorage(uri: Uri): String {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri)
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?.lowercase()
            ?: "jpg"
        val file = File(imageDirectory, "${System.currentTimeMillis()}_${UUID.randomUUID()}.$extension")

        resolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Nem olvasható a kiválasztott kép." }
            file.outputStream().use { output -> input.copyTo(output) }
        }

        return file.absolutePath
    }

    private fun touchProfile(db: SQLiteDatabase, profileId: Long) {
        val values = ContentValues().apply {
            put("updated_at", System.currentTimeMillis())
        }
        db.update("profiles", values, "id = ?", arrayOf(profileId.toString()))
    }

    private fun deleteFileQuietly(path: String) {
        runCatching { File(path).delete() }
    }

    private fun Cursor.toProfile(): Profile {
        val ageIndex = getColumnIndexOrThrow("age")
        return Profile(
            id = getLong(getColumnIndexOrThrow("id")),
            name = getString(getColumnIndexOrThrow("name")),
            age = if (isNull(ageIndex)) null else getInt(ageIndex),
            nationality = getString(getColumnIndexOrThrow("nationality")),
            rating = getFloat(getColumnIndexOrThrow("rating")),
            description = getString(getColumnIndexOrThrow("description")),
            createdAt = getLong(getColumnIndexOrThrow("created_at")),
            updatedAt = getLong(getColumnIndexOrThrow("updated_at"))
        )
    }

    companion object {
        private const val IMAGE_DIRECTORY = "profile_images"
    }
}
