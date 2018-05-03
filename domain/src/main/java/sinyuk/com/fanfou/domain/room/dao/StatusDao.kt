package sinyuk.com.fanfou.domain.room.dao

import android.arch.persistence.room.*
import sinyuk.com.fanfou.domain.data.Status

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
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun inserts(statuses: MutableList<Status>): MutableList<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(status: Status): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updates(statuses: MutableList<Status>): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(status: Status): Int


    @Query("SELECT id, statuses.uniqueId, text, source, statuses.location, statuses.createdAt, favorited, players.screenName, players.profileImageUrl, players.birthday, statuses.url, imageurl, thumburl, largeurl FROM statuses INNER JOIN players ON players.uniqueId = statuses.uniqueId WHERE id = :id LIMIT 1")
    fun query(id: String): Status?

}