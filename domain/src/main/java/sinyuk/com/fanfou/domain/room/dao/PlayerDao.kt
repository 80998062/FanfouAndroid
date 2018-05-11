package sinyuk.com.fanfou.domain.room.dao

import android.arch.persistence.room.*
import sinyuk.com.fanfou.domain.data.Player


/**
 * Created by sinyuk on 2018/4/24.
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
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(players: MutableList<Player>): MutableList<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(player: Player): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updates(players: MutableList<Player>): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(player: Player): Int

    @Delete
    fun delete(vararg players: Player): Int
}