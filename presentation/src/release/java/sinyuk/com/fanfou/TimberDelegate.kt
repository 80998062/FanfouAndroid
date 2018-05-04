package sinyuk.com.fanfou

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
    }

    override fun d(message: String, vararg args: Any) {
    }

    override fun e(message: String, vararg args: Any) {
    }

    override fun i(message: String, vararg args: Any) {
    }

    override fun d(message: String) {
    }

    override fun e(message: String) {
    }

    override fun i(message: String) {
    }

    override fun tag(tag: String) = Timber.tag(tag)
}