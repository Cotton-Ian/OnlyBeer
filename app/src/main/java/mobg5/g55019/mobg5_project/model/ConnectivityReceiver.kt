package mobg5.g55019.mobg5_project.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                // Connecté à Internet
                Toast.makeText(context, "CO", Toast.LENGTH_SHORT).show()
            } else {
                // Pas connecté à Internet
                Toast.makeText(context, "Pas de co", Toast.LENGTH_SHORT).show()
            }
        }
    }
}