package Game

import android.app.Application


class GameApp : Application() {
    companion object {
        private lateinit var instance: GameApp //= null

        fun applicationContext(): Application {
            return this
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}
