package sinyuk.com.fanfou

import timber.log.Timber
import timber.log.Timber.DebugTree


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
object TimberDelegate : ITimber {
    override fun plantTree() {
        Timber.plant(DebugTree())
    }

    override fun d(message: String, vararg args: Any) {
        Timber.d(message, args)
    }

    override fun e(message: String, vararg args: Any) {
        Timber.e(message, args)
    }

    override fun i(message: String, vararg args: Any) {
        Timber.i(message, args)
    }

    override fun d(message: String) {
        Timber.d(message)
    }

    override fun e(message: String) {
        Timber.e(message)
    }

    override fun i(message: String) {
        Timber.i(message)
    }

    override fun tag(tag: String) = Timber.tag(tag)
}