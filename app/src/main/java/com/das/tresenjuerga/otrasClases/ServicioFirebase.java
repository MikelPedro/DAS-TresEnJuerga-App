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
import com.das.tresenjuerga.actividades.PantallaFinActivity;
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

            String recibidor = remoteMessage.getData().get("recibidor");
            String enviador = remoteMessage.getData().get("enviador");

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

                // Notificaciones si estás fuera de la actividad correspondiente, redirects / refreshes si en la actividad
                case 0:
                    // Nueva solicitud de amistad recibida
                    recargarInterfaz = actividadActual instanceof AmigoSolicitudesActivity;
                    break;
                case 1:
                    // Solicitud de amistad aceptada por parte del otro
                    recargarInterfaz = actividadActual instanceof AmigosActivity;
                    break;
                case 2:
                    // Solicitud de partida recibida / han pedido revancha

                    recargarInterfaz = actividadActual instanceof PartidasDisponiblesActivity || actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador);

                case 3:
                    // El otro ha aceptado la partida / revancha aceptada

                    recargarInterfaz = actividadActual instanceof PartidasDisponiblesActivity || actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador);

                    break;
                case 4:
                    // Oponente ha jugado
                    recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));
                    if (recargarInterfaz) {
                        // Si estamos en la misma interfaz, pasar el flag del turno a true
                        ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(true));
                    }

                    break;
                case 5:
                    // Partida en empate
                    recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));
                    if (recargarInterfaz) {
                        ActividadPadre.añadirAIntent("oponenteAcaboLaPartida", "1");

                    }
                case 6:
                    // Partida en loss

                    recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));
                    if (recargarInterfaz) {
                        ActividadPadre.añadirAIntent("oponenteAcaboLaPartida", "0");
                    }


                    break;



                // Mensajes firebase que nunca son notificaciones

                case 7:
                    // Rechazar revancha (solo afecta si se está en la pantalla de resultados vs ese oponente en concreto

                    if (actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador)) {
                        recargarInterfaz = true;
                        ActividadPadre.añadirAIntent("estadoRevancha", "7");

                        // Eliminar la revancha creada de BD
                        String[] data = {enviador, recibidor};
                        ActividadPadre.peticionAServidor("partidas",5, data, null);

                    } else {
                        recargarInterfaz = false;
                    }

                case 8:

                    // Recibir ping request del otro lado para enviar ping answer (rematch)

                    if (actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador)) {
                        recargarInterfaz = true;
                        ActividadPadre.añadirAIntent("estadoRevancha", "5");

                    } else {
                        recargarInterfaz = false;

                    }

                case 9:
                    // Ser expulsado de la partida porque te eliminó como amigo

                    ActividadPadre.añadirAIntent("expulsadoPorNoAmigo", "true");
                    recargarInterfaz = true;



                default:
                    recargarInterfaz = false;

            }



            if (recargarInterfaz) {
                // Refrescar la actividad
                ActividadPadre.recargarActividad();

                // Fin del método, liberar el lock para que el usuario pueda cambiar de actividad
                ActividadPadre.lockRedirectsYPeticionesAServer(false);


            } else if (id < 7) {

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
                String body = enviador + ": " + recibidor + " "+ actividadActual.getString(actividadActual.getResources().getIdentifier("notif"+id, "string", actividadActual.getPackageName()));

                elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), com.google.android.material.R.drawable.abc_btn_check_material))
                        .setSmallIcon(android.R.drawable.checkbox_on_background)
                        .setContentTitle(titulo)
                        .setContentText(body)
                        .setAutoCancel(true);




                elManager.notify(1, elBuilder.build());

                // Fin del método, liberar el lock para que el usuario pueda cambiar de actividad
                ActividadPadre.lockRedirectsYPeticionesAServer(false);

            }


        }


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }


}