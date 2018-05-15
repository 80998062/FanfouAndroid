package sinyuk.com.fanfou.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.*
import sinyuk.com.fanfou.data.Status

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
@Dao
interface StatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(statuses: MutableList<Status>): MutableList<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(status: Status): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updates(statuses: MutableList<Status>): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(status: Status): Int

    @Delete
    fun delete(data: Status)

    @Query("SELECT id, statuses.uniqueId, text, source, statuses.location, statuses.createdAt, favorited, players.screenName, players.profileImageUrl, players.birthday, statuses.url, imageurl, thumburl, largeurl FROM statuses INNER JOIN players ON players.uniqueId = statuses.uniqueId WHERE id = :id LIMIT 1")
    fun query(id: String): Status?

    @Query("SELECT id, statuses.uniqueId, text, source, statuses.location, statuses.createdAt, favorited, players.screenName, players.profileImageUrl, players.birthday, statuses.url, imageurl, thumburl, largeurl FROM statuses INNER JOIN players ON players.uniqueId = statuses.uniqueId ORDER BY statuses.createdAt DESC LIMIT :limit")
    fun take(limit: Int): MutableList<Status>?

    @Query("SELECT id, statuses.uniqueId, text, source, statuses.location, statuses.createdAt, favorited, players.screenName, players.profileImageUrl, players.birthday, statuses.url, imageurl, thumburl, largeurl FROM statuses INNER JOIN players ON players.uniqueId = statuses.uniqueId ORDER BY statuses.createdAt DESC")
    fun home(): DataSource.Factory<Int, Status>


}