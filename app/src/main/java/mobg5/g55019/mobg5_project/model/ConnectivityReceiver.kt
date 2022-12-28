package mobg5.g55019.mobg5_project.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast


/**
 * Broadcast receiver that listens for changes in connectivity.
 *
 * When a change in connectivity is detected, a toast message is displayed indicating whether the device is connected
 * to the Internet or not.
 */
class ConnectivityReceiver : BroadcastReceiver() {

    /**
     * Callback method that is invoked when the broadcast receiver receives an Intent.
     *
     * This method checks for changes in connectivity and displays a toast message indicating the current connectivity
     * status.
     *
     * @param context the context in which the receiver is running
     * @param intent the Intent being received
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                // Connecté à Internet
                Toast.makeText(context, "Connecté à internet", Toast.LENGTH_SHORT).show()
            } else {
                // Pas connecté à Internet
                Toast.makeText(context, "Pas de connexion", Toast.LENGTH_SHORT).show()
            }
        }
    }
}