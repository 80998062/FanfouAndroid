package sinyuk.com.fanfou

import timber.log.Timber

/**
 * Created by sinyuk on 2018/5/3.
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
interface ITimber {
    fun plantTree()
    fun d(message: String, vararg args: Any)
    fun e(message: String, vararg args: Any)
    fun i(message: String, vararg args: Any)
    fun d(message: String)
    fun e(message: String)
    fun i(message: String)
    fun tag(tag: String):Timber.Tree
}
