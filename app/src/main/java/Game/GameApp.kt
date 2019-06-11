package Game

import android.app.Application

class GameApp: Application() {
    companion object {
        private var instance: GameApp? = null

        fun applicationContext(): GameApp {
            return instance as GameApp
        }
    }
    init{
        instance=this
    }

    override fun onCreate() {
        super.onCreate()
    }
}
