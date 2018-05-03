package sinyuk.com.fanfou.domain.room.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import sinyuk.com.fanfou.domain.data.Photos
import sinyuk.com.fanfou.domain.data.Player
import sinyuk.com.fanfou.domain.data.Status
import sinyuk.com.fanfou.domain.room.LocalDatabase
import java.util.*

/**
 * Created by sinyuk on 2018/4/23.
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│        _______. __  .__   __. ____    ____  __    __   __  ___   │
│       /       ||  | |  \ |  | \   \  /   / |  |  |  | |  |/  /   │
│      |   (----`|  | |   \|  |  \   \/   /  |  |  |  | |  '  /    │
│       \   \    |  | |  . `  |   \_    _/   |  |  |  | |    <     │
│   .----)   |   |  | |  |\   |     |  |     |  `--'  | |  .  \    │
│   |_______/    |__| |__| \__|     |__|      \______/  |__|\__\   │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
 */
@RunWith(AndroidJUnit4::class)
class JoinedTablesTest {
    lateinit var localDatabase: LocalDatabase
    lateinit var statusDao: StatusDao
    lateinit var playerDao: PlayerDao

    @Before
    fun createRoom() {
        val context = InstrumentationRegistry.getTargetContext()
        localDatabase = Room.databaseBuilder(context, LocalDatabase::class.java, "mock.db")
                .fallbackToDestructiveMigration().build()
        statusDao = localDatabase.statusDao()
        playerDao = localDatabase.playerDao()
    }

    @Test
    fun testAmbiguousColumns() {
        val playerCreatedAt = System.currentTimeMillis()
        val mockPlayer = Player(uniqueId = "player_id",
                screenName = "player",
                location = "Shanghai",
                url = "player_url",
                createdAt = Date(playerCreatedAt))

        val statusCreatedAt = System.currentTimeMillis()
        val mockStatus = Status(id = "status_id",
                location = "Hangzhou",
                photos = Photos(url = "status_url"),
                player = mockPlayer,
                createdAt = Date(statusCreatedAt))

        // related
        mockStatus.uniqueId = mockPlayer.uniqueId

        // ordered
        assert(playerDao.insert(mockPlayer) > 0)
        // foreign key constrain failed
        assert(statusDao.insert(mockStatus) > 0)


        val status = statusDao.query("status_id")!!

        assert(status.id == "status_id")
        assert(status.uniqueId == "player_id")
        assert(status.location == "Hangzhou")
        assert(status.photos?.url == "status_url")
        assert(status.createdAt?.time == statusCreatedAt)

        val player = status.author!!
        assert(player.screenName == "player")
        assert(player.birthday?.time == playerCreatedAt)
    }


    /**
     * 测试更新是否会迭代
     */
    @Test
    fun testOnUpdateCascade() {
        val newPlayer = Player(uniqueId = "player_id", screenName = "sinyuk")
        assert(playerDao.update(newPlayer) == 1)

        val status = statusDao.query("status_id")!!
        assert(status.author?.screenName == "sinyuk")
    }


    /**
     * 删除User时,关联的Status所有的值会变成Null
     */
    @Test
    fun testOnDeleteSetNull() {
        assert(statusDao.query("status_id") != null)

        val player = Player(uniqueId = "player_id")
        assert(playerDao.delete(player) == 1)

        val status = statusDao.query("status_id")
        assert(status?.uniqueId == null)
        assert(status?.id == null)
    }


    @After
    fun closeRoom() {
        localDatabase.close()
    }
}