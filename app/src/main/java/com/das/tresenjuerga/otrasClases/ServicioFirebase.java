package com.das.tresenjuerga.otrasClases;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.AmigoSolicitudesActivity;
import com.das.tresenjuerga.actividades.AmigosActivity;
import com.das.tresenjuerga.actividades.JugarActivity;
import com.das.tresenjuerga.actividades.MainActivity;
import com.das.tresenjuerga.actividades.PantallaFinActivity;
import com.das.tresenjuerga.actividades.PartidasDisponiblesActivity;
import com.das.tresenjuerga.actividades.PerfilActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {



    public void onMessageReceived(RemoteMessage remoteMessage) {



        // Todos los mensajes de firebase tienen un emisor y receptor, recoger dichos campos

        String recibidor = remoteMessage.getData().get("recibidor");
        String enviador = remoteMessage.getData().get("enviador");

        // Estamos en la app al recibir el msg de Firebase, crear la notificación
        // (si no estuviera la app abierta, Firebase haría automáticamente este paso)





        // Obtener la actividad que tiene el user abierta y el id de petición, según estas es posible que se deba
        // hacer una redirección en vez de una notificación
        ActividadPadre actividadActual = ActividadPadre.getActividadActual();
        int id = Integer.parseInt(remoteMessage.getData().get("id"));
        boolean recargarInterfaz;
        boolean forzarDeslogeo = false;


        // ids 0-6 => Notificaciones / Redirecciones
        // ids 7-9 => Redirecciones / NOP

        switch (id) {
            // IDs 0-6:
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
                break;
                
            case 3:
                // El otro ha aceptado la partida / revancha aceptada
                recargarInterfaz = actividadActual instanceof PartidasDisponiblesActivity || actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador);

                break;
            case 4:
                // Oponente ha jugado
                recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));

                if (recargarInterfaz) {
                    // Si estamos en la misma interfaz, pasar el flag del turno a true para que ahora podamos jugar
                    ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(true));
                }
                break;
            case 5:
                // Partida en empate


                recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));

                if (recargarInterfaz) {
                    // Si estamos en la partida adecuada, informarla de que fue un empate
                    ActividadPadre.añadirAIntent("oponenteAcaboLaPartida", "1");

                }

                break;
            case 6:

                // Partida en derrota
                recargarInterfaz = actividadActual instanceof JugarActivity && enviador.contentEquals(ActividadPadre.obtenerDeIntent("oponente"));



                if (recargarInterfaz) {
                    // Si estamos en la partida adecuada, informarla de que fue una derrota
                    ActividadPadre.añadirAIntent("oponenteAcaboLaPartida", "0");

                }

                break;



            // Mensajes firebase que nunca son notificaciones pero pueden ser redirects

            case 7:
                // Rechazar revancha (solo afecta si se está en la pantalla de resultados vs ese oponente en concreto
                System.out.println( ActividadPadre.obtenerDeIntent("oponente") + " " + enviador);

                if (actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador)) {
                    recargarInterfaz = true;
                    ActividadPadre.añadirAIntent("estadoRevancha", "7");

                    // Si están los dos users y uno se va, se quita la revancha de BD y se bloquea al que queda el botón de
                    // pedir de nuevo (cuenta como rechazo directo)
                    // Si solo hubiera un user en esta interfaz y el otro no conectase, no se quitaria la revancha pedida
                    // pues el usuario que se sale es el que pidió la revancha y el otro no tenía la opción de aceptarla, por
                    // lo que se quedaría en el inbox.

                    String[] data = {enviador, recibidor, "0"};
                    ActividadPadre.peticionAServidor("partidas",5, data, null);
                    System.out.println("Check de redirect: true");
                } else {
                    recargarInterfaz = false;
                    System.out.println("Check de redirect: false");

                }

                break;

            case 8:

                // Recibir ping del otro lado para ver que esta activo para rematch

                if (actividadActual instanceof PantallaFinActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador)) {

                    // El ping solo se procesa si se está en la misma interfaz y fuerza un refresco + update de estado de revancha
                    // (ver PantallaFinActivity para más info de los estados)
                    recargarInterfaz = true;
                    ActividadPadre.añadirAIntent("estadoRevancha", "5");

                } else {
                    recargarInterfaz = false;

                }

                break;

            case 9:
                // Forzar recarga de lista de amigos porque un user te borro como amigo

                if (actividadActual instanceof AmigosActivity) {

                    recargarInterfaz = true;


                    // Ser expulsado de la interfaz porque tu amigo te borró
                } else if (actividadActual instanceof PerfilActivity && ActividadPadre.obtenerDeIntent("userAVisualizar").contentEquals(enviador) ||actividadActual instanceof JugarActivity && ActividadPadre.obtenerDeIntent("oponente").contentEquals(enviador)) {

                    // si justo estaba viendo la partida que mi amigo me quito, redirigir fuera de ella, para ello
                    // informar a la interfaz de jugar de esto y refrescarla

                    ActividadPadre.añadirAIntent("expulsadoPorNoAmigo", "true");
                    recargarInterfaz = true;
                } else {
                    recargarInterfaz = false;
                }


                break;

            case 10:
                // Otro móvil se conecta a esta cuenta, quitar este de ella
                forzarDeslogeo = true;
                recargarInterfaz = false;

                break;

            default:
                // Escenario fallback es no refrescar la interfaz, aunque no debería ejecutarse esto
                recargarInterfaz = false;

        }


        // Ver si se pide refrescar la interfaz
        if (recargarInterfaz) {
            // Si es el caso, refrescar la actividad y omitir crear una posible notificación
            ActividadPadre.recargarActividad();



        } else if (id < 7) {

            // Si no se pide refrescar la interfaz pero estamos en IDs 0-6, entonces debemos montar la notificación
            // correspondiente según lo que nos ha informando firebase.(IDs 0-6 incluyen campos extra de las notificaciones en sus cuerpos)

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
            String body = recibidor + ": " + enviador + " "+ actividadActual.getString(actividadActual.getResources().getIdentifier("notif"+id, "string", actividadActual.getPackageName()));

            elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.tres_en_raya))
                    .setSmallIcon(android.R.drawable.checkbox_on_background)
                    .setContentTitle(titulo)
                    .setContentText(body)
                    .setAutoCancel(true);




            elManager.notify(1, elBuilder.build());


        } else if (forzarDeslogeo) {
            ActividadPadre.limpiarIntent();
            ActividadPadre.redirigirAActividad(MainActivity.class);
        }





    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }


}
