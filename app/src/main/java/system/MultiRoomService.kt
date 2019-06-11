package system

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import edu.us.ischool.bchong.info448project.NearbyConnection


class MultiRoomService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun onStartService() {
        //your code
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val nearby = NearbyConnection.instance
        Log.d("INFO_448_DEBUG", "Inside custom service")
        if (nearby != null) {
            super.onTaskRemoved(rootIntent)
            //do something you want
            nearby.disconnectEndpointsAndStop()
            //stop service
            this.stopSelf()
        }
    }
}