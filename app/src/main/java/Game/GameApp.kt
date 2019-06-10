package Game

import android.app.Application

class GameApp: Application() {
    companion object {
        private var instance: GameApp? = null

        fun applicationContext(): GameApp {
            return Companion.instance as GameApp
        }
    }
    init{
       Companion.instance =this
    }

    override fun onCreate() {
        super.onCreate()
    }
}