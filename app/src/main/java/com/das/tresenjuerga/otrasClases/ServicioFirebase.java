package com.das.tresenjuerga.otrasClases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.AmigoSolicitudesActivity;
import com.das.tresenjuerga.actividades.AmigosActivity;
import com.das.tresenjuerga.actividades.JugarActivity;
import com.das.tresenjuerga.actividades.PartidasDisponiblesActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    private void esperar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {}
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getNotification() != null) {

            // Estamos en la app al recibir el msg de Firebase, crear la notificación
            // (si no estuviera la app abierta, Firebase haría automáticamente este paso)

            // Esto se ejecuta en otro thread, lockear el cambio de actividad para que no se lie con los checks
            // de la actividad en la que está
            ActividadPadre.lockRedirectsYPeticionesAServer(true);
            this.esperar(10); // Esperar un poco por si justo pillamos el lock en la acción del cambio de actividad
                                  // para que de tiempo a que la nueva cargue y el user se quede atascado en ella

            ActividadPadre actividadActual = ActividadPadre.getActividadActual();
            int id = Integer.parseInt(remoteMessage.getData().get("id"));
            boolean recargarInterfaz;

            switch (id) {
                case 0:
                    recargarInterfaz = actividadActual instanceof AmigoSolicitudesActivity;
                    break;
                case 1:
                    recargarInterfaz = actividadActual instanceof AmigosActivity;
                    break;
                case 2:
                case 3:
                    recargarInterfaz = actividadActual instanceof PartidasDisponiblesActivity;
                    break;
                case 4:
                case 5:
                case 6:
                    if (actividadActual instanceof JugarActivity) {
                        recargarInterfaz = ((JugarActivity)actividadActual).esElOponente(remoteMessage.getData().get("enviador"));

                    } else {
                        recargarInterfaz = false;
                    }

                    break;

                default:
                    recargarInterfaz = false;

            }

            if (recargarInterfaz) {
                actividadActual.recargarActividad();

            } else {

                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(actividadActual.getActividadActual(), "IdCanal");


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
                    //Configuración del canal
                    elCanal.setDescription("Descripción del canal");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.RED);
                    elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    elCanal.enableVibration(true);


                    elManager.createNotificationChannel(elCanal);
                }



                String titulo = actividadActual.getString(actividadActual.getResources().getIdentifier("notifTitulo"+id, "string", actividadActual.getPackageName()));
                String body =remoteMessage.getData().get("recibidor") + ": " +remoteMessage.getData().get("enviador") + " "+ actividadActual.getString(actividadActual.getResources().getIdentifier("notif"+id, "string", actividadActual.getPackageName()));

                elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), com.google.android.material.R.drawable.abc_btn_check_material))
                        .setSmallIcon(android.R.drawable.checkbox_on_background)
                        .setContentTitle(titulo)
                        .setContentText(body)
                        .setAutoCancel(true);




                elManager.notify(1, elBuilder.build());

            }

            // Fin del método, liberar el lock para que el usuario pueda cambiar de actividad
            ActividadPadre.lockRedirectsYPeticionesAServer(false);

        }


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }


}