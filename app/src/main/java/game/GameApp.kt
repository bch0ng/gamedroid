package game

import android.app.Application

class GameApp: Application() {
    companion object {
        private lateinit var instance: GameApp

        fun applicationContext(): GameApp {
            return instance
        }
    }
    init{
        instance=this
    }

    override fun onCreate() {
        super.onCreate()
    }
}
