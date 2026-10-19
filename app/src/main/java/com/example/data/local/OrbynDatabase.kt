package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PostEntity::class,
        CommentEntity::class,
        MessageEntity::class,
        UserEntity::class,
        SecurityEntity::class,
        DraftEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OrbynDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun securityDao(): SecurityDao
    abstract fun draftDao(): DraftDao

    companion object {
        @Volatile
        private var INSTANCE: OrbynDatabase? = null

        fun getInstance(context: Context): OrbynDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrbynDatabase::class.java,
                    "orbyn_internal.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default data on background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(db: OrbynDatabase) {
            // Seed Current User
            val currentUser = UserEntity(
                id = "u_me",
                name = "Alex Vance",
                username = "alexvance",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                bio = "film cameras & late night tea ☕️ living between coffee shops",
                statusText = "🎧 listening to boygenius - Not Strong Enough",
                isCloseFriend = true,
                isOnline = true
            )
            db.userDao().insertUser(currentUser)

            // Seed Friends
            val sampleFriends = listOf(
                UserEntity("u_1", "Maya Lin", "mayalin", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80", "capturing small moments", "🥐 baking sourdough", isCloseFriend = true, isOnline = true),
                UserEntity("u_2", "Leo Chen", "leochen", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80", "brooklyn based // sound designer", "🎛️ in the studio", isCloseFriend = true, isOnline = true),
                UserEntity("u_3", "Elena Rostova", "elenar", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80", "architecture & espresso", "reading Calvino", isCloseFriend = true, isOnline = false),
                UserEntity("u_4", "Sam Rivera", "samriv", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80", "climbing rocks & writing poetry", "🧗 bouldering", isCloseFriend = false, isOnline = true),
                UserEntity("u_5", "Kai Tanaka", "kaitanaka", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80", "tokyo / berlin", "✈️ transit", isCloseFriend = true, isOnline = false)
            )
            db.userDao().insertUsers(sampleFriends)

            // Seed Posts
            val samplePosts = listOf(
                PostEntity(
                    id = "p_1",
                    authorId = "u_1",
                    authorName = "Maya Lin",
                    authorUsername = "mayalin",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
                    type = "PHOTO",
                    mediaUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=900&auto=format&fit=crop&q=80",
                    thoughtText = null,
                    thoughtGradientIndex = 0,
                    filterName = "Golden",
                    caption = "sunday morning stillness. coffee tastes better when no one is in a rush.",
                    location = "Café Kitsuné, West Village",
                    taggedFriendsRaw = "alexvance,leochen",
                    isCloseFriendsOnly = true,
                    timestamp = "24m ago",
                    heartCount = 4,
                    laughCount = 0,
                    fireCount = 2,
                    userReaction = "HEART",
                    isSaved = false,
                    createdAt = System.currentTimeMillis() - 24 * 60 * 1000
                ),
                PostEntity(
                    id = "p_2",
                    authorId = "u_2",
                    authorName = "Leo Chen",
                    authorUsername = "leochen",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80",
                    type = "VIDEO",
                    mediaUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=900&auto=format&fit=crop&q=80",
                    thoughtText = null,
                    thoughtGradientIndex = 0,
                    filterName = "Bloom",
                    caption = "sampled the rain hitting my fire escape and turned it into an ambient loop. 48 seconds of quiet.",
                    location = "Studio B, Greenpoint",
                    taggedFriendsRaw = "",
                    isCloseFriendsOnly = false,
                    timestamp = "2h ago",
                    heartCount = 3,
                    laughCount = 1,
                    fireCount = 6,
                    userReaction = "FIRE",
                    isSaved = false,
                    createdAt = System.currentTimeMillis() - 2 * 3600 * 1000
                ),
                PostEntity(
                    id = "p_3",
                    authorId = "u_3",
                    authorName = "Elena Rostova",
                    authorUsername = "elenar",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                    type = "THOUGHT",
                    mediaUrl = null,
                    thoughtText = "the most comforting feeling is sitting in silence with people you don't feel pressured to entertain. that's what orbyn is for.",
                    thoughtGradientIndex = 2,
                    filterName = "Original",
                    caption = "",
                    location = "Home Library",
                    taggedFriendsRaw = "",
                    isCloseFriendsOnly = true,
                    timestamp = "4h ago",
                    heartCount = 5,
                    laughCount = 0,
                    fireCount = 1,
                    userReaction = null,
                    isSaved = true,
                    createdAt = System.currentTimeMillis() - 4 * 3600 * 1000
                )
            )
            db.postDao().insertPosts(samplePosts)

            // Seed Comments
            val sampleComments = listOf(
                CommentEntity("c_1", "p_1", "u_2", "Leo Chen", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=400&auto=format&fit=crop&q=80", "that sunlight angle is pure cinema", "18m ago"),
                CommentEntity("c_2", "p_1", "u_me", "Alex Vance", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80", "save a croissant for me next time!", "12m ago")
            )
            db.commentDao().insertComments(sampleComments)

            // Seed Messages
            val sampleMessages = listOf(
                MessageEntity("m_1", "conv_1", "u_1", "hey! look at this film scan I just developed", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80", false, 0, "10:14 AM", true),
                MessageEntity("m_2", "conv_1", "u_me", "whoa the colors came out incredible! portra 400?", null, false, 0, "10:18 AM", true),
                MessageEntity("m_3", "conv_1", "u_1", null, null, true, 14, "10:22 AM", true),
                MessageEntity("m_4", "conv_1", "u_1", "are we still meeting for matcha at 4?", null, false, 0, "10:45 AM", false),
                MessageEntity("m_5", "conv_2", "u_2", "check out the draft track when you get home", null, false, 0, "9:00 AM", true),
                MessageEntity("m_6", "conv_2", "u_me", "listening right now! the bassline is crazy clean", null, false, 0, "9:15 AM", true)
            )
            db.messageDao().insertMessages(sampleMessages)

            // Seed Security Settings
            db.securityDao().insertSecuritySettings(SecurityEntity())
        }
    }
}
