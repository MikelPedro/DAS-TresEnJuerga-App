package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;
import com.das.tresenjuerga.otrasClases.WorkerTimeoutConexion;

import java.util.concurrent.TimeUnit;

public class PantallaFinActivity extends ActividadPadre {


 /*
         Esta actividad muestra la pantalla de revancha tras acabar una partida
         Funciona por estados para manejar revanchas en vivo

                Estados:

                0 -> Se puede pedir revancha, todavia no se ha pedido
                1 -> Oponente pidió rematch, todavía no se ha aceptado
                2 -> Has pedido rematch, todavía no se ha aceptado
                3 -> Rematch aceptado, enviar ping al otro
                4- > Esperando al ping del otro
                5 -> Recibir el ping del otro y cargar data para partida
                6 -> Error de conexion (fallo de ping)
                7 -> Rematch cancelado, el otro player se fue de la interfaz


         Ciclo de vida de los estados entre players A y B (Nota, los estados se guardan por intent y se conservan entre rotaciones)

         1. A y B acaban match y se redirigen a esta actividad.

         *** Estados: A: 0, B: 0 ***

         2. A envia rematch a B, para ello hace una petición a server para añadir la partida a crear (como si
            se hiciera desde la pestaña de amigos, la llamada a BD es la misma).
         3. Tras crear la partida, A se informa por listener y B es notificado por Firebase mediante la BD de que
            deben refrescar UI (no se settea externamente el estado)

         4. Este refresco no ha updateado el valor estado, por lo que ambos móviles buscan en BD si hay una
            partida creada. La encuentran y miran quien reta a quien para actualizar estados.

         *** Estados: A: 2, B: 1 ***

         5. B acepta el rematch, haciendo una llamada al server (como si se aceptara desde la pestaña de partidas
            se hace la misma llamada en BD). Tras aceptar match, B es informado por listener y A por Firebase via BD
            de que deben refrescar sus interfaces (no se settea externamente el estado)

         6. Este refresco no ha updateado el valor estado, por lo que ambos móviles buscan en BD el match y encuentran
            una partida creada y aceptada. Esto updateean los estados de ambos clientes a 3

          *** Estados: A: 3, B: 3 ***

         7. Cada dispositivo hace una llamada directa a Firebase para mandar un ping al otro móvil (asegurarse de que
            sí están en el otro lado). Tiene un timeout de 20s hasta que se pasa al estado de error de conexión.
            Tras hacer este ping, pasan automáticamente al estado 4 para estar a la espera.

          *** Estados: A: 4, B: 4 ***

         8. Cada dispositivo recibe por su clase Firebase un ping. La clase Firebase comprueba que están en la interfaz adecuada
            y se tienen ambos como oponentes. En este caso correcto, la clase firebase actualiza externamente al estado 5 y fuerza
            refresco.

          *** Estados: A: 5, B: 5 ***

         9. Cada dispositivo ha llegado al estado 5, por lo que cargan la partida para jugar. Antes de esto, hacen una llamada a BD
            (cada uno) para ver quien tiene el primer turno


         X: Si pasan 20 segundos sin ping, el Worker programado fuerza a pasar al estado 6 y refresca la interfaz, mostrando el
            mensaje de error

         Y: Si el otro usuario se sale de la interfaz, se manda un mensaje por Firebase al otro dispositivo. Si el otro dispositivo
            está en esta interfaz, la clase Firebase settea su estado al 7 y fuerza el refresco de la actividad


         Nota: El oponente solo puede ser redirigido aquí también si tenía la misma partida abierta cuando la acabaste.
               Si el oponente nunca es redirigido aquí no te podrá aceptar la revancha directamente, aunque se puede
               pedir la revancha para que le salga en su lista de partidas como otra petición de partida para aceptar.

               Esta pantalla de revancha solo redirige de nuevo a la pantalla de jugar si se acepta la revancha desde
               esta misma interfaz y NO desde la inbox de partidas disponibles


         */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pantalla_fin);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Ver el estado en el que estamos
        int estadoRev = Integer.parseInt(ActividadPadre.obtenerDeIntent("estadoRevancha"));
        System.out.println("Estado prev a check: "+estadoRev);

        if (estadoRev < 4) {
            // Los estados previos al 4 requieren comprobar el estado de la solicitud de revancha en servidor
            String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
            ActividadPadre.peticionAServidor("partidas", 6, datos, new ObservadorDeEstadoRevancha());


        } else {
            // El resto de estados se saltan este paso y crean la pantalla inmediatamente
            PantallaFinActivity.this.crearInterfaz(Integer.toString(estadoRev));

        }







    }

    private void crearInterfaz(String estado) {

        System.out.println("Estado: "+estado);

        ActividadPadre.añadirAIntent("estadoRevancha", estado);


        // Recoger el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();


        // Mostrar el título adecuado, según si se ganó, empató o perdió desde esta POV
        String titulo = "";
        switch (ActividadPadre.obtenerDeIntent("resultado")) {
            case "0":
                titulo = super.getResources().getString(R.string.derrota);
                break;
            case "1":
                titulo = super.getResources().getString(R.string.empate);
                break;
            case "2":
                titulo = super.getResources().getString(R.string.victoria);


        }

        ((TextView)fragmento.findViewById(R.id.pantallaFinT_Titulo)).setText(titulo);


        // Dar listeners a los botones

        Button revancha = fragmento.findViewById(R.id.pantallaFinB_Revancha);
        Button volver = fragmento.findViewById(R.id.pantallaFinB_Volver);
        TextView descripcion = fragmento.findViewById(R.id.pantallaFinT_PedidoRevancha);

        String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};

        // Procesar el estado en el que estamos
        switch (estado) {
            case "0":
                // Se puede pedir revancha, no se ha pedido revancha por ninguna party
                descripcion.setText(super.getResources().getString(R.string.puedesPedirRevancha));
                revancha.setOnClickListener(new BotonListener(0)); // el primer botón sirve para pedir revancha
                revancha.setText(super.getResources().getString(R.string.revancha));

                break;

            case "1":
                // Han pedido revancha, esperando tu respuesta...
                descripcion.setText(super.getResources().getString(R.string.hanPedidoRevancha));
                revancha.setOnClickListener(new BotonListener(1)); // el primer botón sirve para aceptar revancha
                revancha.setText(super.getResources().getString(R.string.aceptar));

                break;

            case "2":
                // Has pedido revancha, esperando respuesta...
                descripcion.setText(super.getResources().getString(R.string.revanchaPedida));
                revancha.setVisibility(View.GONE);

                break;



            case "3":
                // Revancha aceptada por ambas parties, enviar ping


                descripcion.setText(super.getResources().getString(R.string.revanchaAceptado));
                revancha.setVisibility(View.GONE);

                volver.setVisibility(View.GONE);
                volver.setEnabled(false);

                ActividadPadre.peticionAServidor("firebase", 1, datos, null);
                ActividadPadre.añadirAIntent("estadoRevancha", "4");

                // Programar temporizador para timeout de conexion
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerTimeoutConexion.class).setInitialDelay(10, TimeUnit.SECONDS).build();
                WorkManager.getInstance(this).enqueue(otwr);
                ActividadPadre.recargarActividad();
                break;

            case "4":

                // Esperando ping...
                descripcion.setText(super.getResources().getString(R.string.revanchaAceptado));
                revancha.setVisibility(View.GONE);

                volver.setVisibility(View.GONE);
                volver.setEnabled(false);

                break;

            case "5":
                // Aceptar ping del otro y conectar





                // Conexion establecida, ver quien empieza y empezar a jugar!

                descripcion.setText(super.getResources().getString(R.string.revanchaReady));
                revancha.setVisibility(View.GONE);

                volver.setVisibility(View.GONE);
                volver.setEnabled(false);

                ActividadPadre.peticionAServidor("partidas", 7, datos, new ObservadorDeInicioRevancha());

                break;




            case "6":
                // Error de conexion (si pasan 10s sin que el otro móvil envie el ping)
                descripcion.setText(super.getResources().getString(R.string.errorConexion));
                revancha.setVisibility(View.GONE);

                break;

            case "7":

                // Se rechazó la revancha (el otro player se fue de la interfaz)
                descripcion.setText(super.getResources().getString(R.string.revanchaRechazada));
                revancha.setVisibility(View.GONE);




        }

        volver.setOnClickListener(new BotonListener(2));
    }



    private class ObservadorDeEstadoRevancha extends ObservadorDePeticion {
        @Override
        protected void ejecutarTrasPeticion() {
            // Tras recibir el estado de la petición del servidor, crear la interfaz
            PantallaFinActivity.this.crearInterfaz(Long.toString(super.getLong("respuesta")));



        }
    }

    private class ObservadorDePeticionRevancha extends ObservadorDePeticion {

        // Tras haber procesado correctamente la petición de pedir revancha, updatear la interfaz
        // para mostrar los cambios
        @Override
        protected void ejecutarTrasPeticion() {
            ActividadPadre.recargarActividad();


        }
    }
    private class ObservadorDeInicioRevancha extends ObservadorDePeticion {

        // Tras correctamente haber pedido la revancha, aceptado y recibido los datos necesarios
        // para jugar, cargar la interfaz de juego

        @Override
        protected void ejecutarTrasPeticion() {

            ActividadPadre.quitarDeIntent("estadoRevancha");
            ActividadPadre.quitarDeIntent("resultado");
            System.out.println(super.getBoolean("respuesta"));
            ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(super.getBoolean("respuesta")));
            ActividadPadre.redirigirAActividad(JugarActivity.class);



        }
    }
    private class BotonListener implements View.OnClickListener {

        private int id;

        public BotonListener (int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {

            String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
            switch (this.id) {

                case 0:
                    // El primer botón en estado 0 pide revancha
                    ActividadPadre.peticionAServidor("partidas", 0, datos, new ObservadorDePeticionRevancha());
                    break;

                case 1:
                    // El primer botón en estado 1 acepta revancha
                    ActividadPadre.peticionAServidor("partidas", 1, datos, new ObservadorDePeticionRevancha());

                    break;



                case 2:
                    // El otro botón va una pantalla atrás, a la de partidas disponibles

                    // Notificar a firebase de que se cancela la revancha
                    ActividadPadre.peticionAServidor("firebase", 0, datos, null);
                    ActividadPadre.quitarDeIntent("estadoRevancha");
                    ActividadPadre.quitarDeIntent("resultado");
                    ActividadPadre.quitarDeIntent("oponente");

                    // Volver
                    ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

            }
        }
    }
}