package pos.com.br.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

/**
 * Created by wanderson on 07/06/2015.
 */
public class Conexao {

    public static void conectado(Activity activity) {
        final Activity activity2 = activity;
        try {
            ConnectivityManager cm = (ConnectivityManager)
                    activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (!cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {

                AlertDialog.Builder bl = new AlertDialog.Builder(activity);
                bl.setTitle("Status Conexão");
                bl.setMessage("Desconectado: Verifique");
                bl.setNegativeButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conectado(activity2);
                    }
                });
                bl.show();
            }
        } catch (Exception e) {
        }
    }
}
