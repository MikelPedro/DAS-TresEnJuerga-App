package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class JugarActivity extends ActividadPadre {



    /*
        Esta interfaz muestra el estado de una partida

        Internamente, se guarda el estado de la partida en una string de 9 chars. 'X' representa X,
        'O' representa O y '-' representa espacio vacío

        La string indexada de 0 a 8, cada posición indica el siguiente trozo del tablero:

             ----------------------------
        |     0   |    1     |  2   |
        ----------------------------
        |     3   |    4    |   5   |
        ----------------------------
        |     6   |    7     |  8   |
        ----------------------------

        Matemáticamente, asumiendo 0 based indexing:

        Fila = pos / 3
        Col  = pos % 3


        Por ejemplo, el estado:

        ----------------------------
        |     X   |    O     |      |
        ----------------------------
        |         |     X    |      |
        ----------------------------
        |     O   |          |      |
        ----------------------------

        Se guarda como: "XO--X-O--"

        Esta interfaz también almacena como que pieza juega el jugador que la está viendo ('O' o 'X')

        La 'X' siempre juega primero

     */

    private String tablero;
    private char figura;

    private View fragmento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_jugar);

        // Checkear flags para ver si se requiere hacer redirect porque no tiene sentido mostrar la partida

        if (ActividadPadre.obtenerDeIntent("expulsadoPorNoAmigo").contentEquals("true")) {

            // Si el oponente te quita como amigo cuando estás viendo la partida, esta se borra.
            // No tiene sentido visualizarla, por lo que se redirige una pantalla atrás

            ActividadPadre.quitarDeIntent("expulsadoPorNoAmigo");
            ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);
        } else {


            String estadoFinPartida = ActividadPadre.obtenerDeIntent("oponenteAcaboLaPartida");

            if (!estadoFinPartida.contentEquals("")) {
                // Si el oponente realizó una jugada que acabó la partida, entonces también se debe
                // redirigir.

                ActividadPadre.quitarDeIntent("oponenteAcaboLaPartida");
                this.acabarPartida(Integer.parseInt(estadoFinPartida));
            }
        }


    }

    @Override
    protected void onStart() {

        super.onStart();

        // Obtener el fragmento de la actividad
        this.fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        this.fragmento.findViewById(R.id.partidaB_Volver).setOnClickListener(new BotonListener());

        // Pedir al servidor el estado de la partida
        String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
        ActividadPadre.peticionAServidor("partidas", 3, datos, new ObservadorDeTablero());


        // TODO: Poner distintos fondos segun preferencias, por ahora fondo_0 es default

        /*
            Source imagenes:

            0:
               fondo: https://www.istockphoto.com/es/foto/pizarra-ticktacktoe-fondo-gm946399874-258448731?searchscope=image%2Cfilm
               figuras: https://www.istockphoto.com/es/foto/tick-tack-toe-en-la-pizarra-gm1134456281-301461381?searchscope=image%2Cfilm

         */
        ((ImageView)this.fragmento.findViewById(R.id.partidaI_Fondo)).setImageResource(R.drawable.fondo_0);


    }




    private ImageView actualizarCasillaEnUI(int pos) {

        // Este método actualiza la imagen de una pieza en la matriz de juego. Los ids de posición van así:

        /*
         ----------------------------
        |     0   |    1     |  2   |
        ----------------------------
        |     3   |    4    |   5   |
        ----------------------------
        |     6   |    7     |  8   |
        ----------------------------

        Matemáticamente, asumiendo 0 based indexing:

        Fila = pos / 3
        Col  = pos % 3

         */

        // TODO: Poner distintos X/O segun preferencias, por ahora modo_0 es default

        ImageView casilla = (ImageView) this.fragmento.findViewById(super.getResources().getIdentifier("partidaI_"+pos, "id", super.getPackageName()));
        char figura = this.tablero.charAt(pos);

        switch (figura) {
            case 'X': // X
                casilla.setImageResource(R.drawable.x_0);
                break;
            case 'O': // 0
                casilla.setImageResource(R.drawable.o_0);
         // case '-':
         //     NOP;

        }
        return casilla;
    }


    private class ObservadorDeTablero extends ObservadorDePeticion {

        @Override
        protected void ejecutarTrasPeticion() {

            // El servidor nos responde con el estado de la partida

            JugarActivity actividad = JugarActivity.this;

            // Recoger el tablero, así como la figura con la que jugamos
            actividad.tablero = super.getString("tablero"); // string de 9 chars usando '-', 'X' y 'O'
            actividad.figura = super.getString("miFigura").charAt(0); // X or O

            for (int i = 0; i != 9; i++) {
                // Dar un listener a cada imagen del tablero, para que puedan responder cuando se pinchan en ellas
                actividad.actualizarCasillaEnUI(i).setOnClickListener(new ImagenListener(i));
            }


            // Settear titulo ("Partida vs [BLANK]")
            ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Titulo)).setText(actividad.getResources().getString(R.string.tituloPartida) + " " + ActividadPadre.obtenerDeIntent("oponente"));

            // Settear descripcion ("Es tu turno", "No es tu turno", etc..)

            int idString;
            if (ActividadPadre.obtenerDeIntent("tuTurno").contentEquals(Boolean.toString(true))) {
                idString = R.string.descPartidaTuTurno;
            } else {
                idString = R.string.descPartidaTurnoDelOtro;

            }
            ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(actividad.getResources().getString(idString) + " " + actividad.figura +".");



        }
    }
    private void acabarPartida(int resultado) {

        // Pre: 0 (loss), 1 (draw), 2 (Win) desde este POV

        // Guardar la info relevante y pasar a la interfaz de revancha
        ActividadPadre.añadirAIntent("estadoRevancha", "0"); // por defecto, el estado de revancha es 0 (ver interfaz de revancha para más info)
        ActividadPadre.añadirAIntent("resultado", Integer.toString(resultado)); // 0, 1 or 2.
        ActividadPadre.quitarDeIntent("tuTurno");

        ActividadPadre.redirigirAActividad(PantallaFinActivity.class);

    }

    private class ImagenListener implements View.OnClickListener {

        private int id;

        public ImagenListener(int id) {this.id = id;}
        @Override
        public void onClick(View v) {

            // Procesar cuando se pincha en una casilla

            JugarActivity actividad = JugarActivity.this;



            if (!Boolean.parseBoolean(ActividadPadre.obtenerDeIntent("tuTurno"))) {
                // Si no es mi turno, no puedo jugar, mostrar error
                ActividadPadre.mostrarToast(R.string.noEsTuTurno);



            } else if (actividad.tablero.charAt(this.id) == '-') {
                // Si es mi turno y pincho en una casilla en blanco, puedo jugar

                // Hacer play en client, para ello actualizar la string del tablero de forma local
                actividad.tablero = actividad.tablero.substring(0, this.id) + actividad.figura + actividad.tablero.substring(this.id+1, 9);
                // Actualizar visualmente el cambio
                actividad.actualizarCasillaEnUI(this.id);

                // Cambiar la descripción de la partida para indicar que no es tu turno
                ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(actividad.getResources().getString(R.string.descPartidaTurnoDelOtro) + " " + actividad.figura +".");

                // Settear el booleano que trackea si es tu turno a false
                ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(false));

                // Comunicar play en server para que se actualice la matriz
                String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), actividad.tablero};
                ActividadPadre.peticionAServidor("partidas", 4, datos, new ObservadorDePlay(this.id));

            } else {
                // Si es mi turno pero la casilla no está vacía, no puedo jugar ahí. Mostrar error
                ActividadPadre.mostrarToast(R.string.noPoderPonerAhi);
            }

        }



        private class ObservadorDePlay extends ObservadorDePeticion {

            private int jugada;
            public ObservadorDePlay(int jugada) {
                this.jugada = jugada;

            }


            @Override
            protected void ejecutarTrasPeticion() {

                // El servidor nos responde que ha updateado la partida

                ActividadPadre.lockRedirectsYPeticionesAServer(true); // Bloquear al user de que cambie de interfaz hasta que se


                // Comprobar si la partida acaba
                JugarActivity actividad = JugarActivity.this;
                String tablero = actividad.tablero;
                char ficha = actividad.figura;

                if (this.gana(tablero, ficha, this.jugada)) {

                    // Si se gana, esperar 3s para mostrar la matriz un poco antes de forzar redirect a la pantalla de revancha
                    this.esperar(3000);

                    // Informar al servidor de que quite la partida porque se ha acabado en victoria
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "2"};
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);

                    ActividadPadre.peticionAServidor("partidas",5, datos, null);

                    // Acabar la partida en victoria
                    JugarActivity.this.acabarPartida(2);


                } else if (!tablero.contains("-")) {

                    // Si se empata, esperar 3s para mostrar la matriz un poco antes de forzar redirect a la pantalla de revancha
                    this.esperar(3000);

                    // Informar al servidor de que quite la partida porque se ha acabado en empate
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "1"};
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);

                    ActividadPadre.peticionAServidor("partidas",5, datos, null); // quitar la partida de BD

                    JugarActivity.this.acabarPartida(1);

                } else {
                    // No se hace redirect si la partida no ha acabado
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);
                }

            }


            private void esperar(int ms) {
                // esperar por X millis
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            private boolean gana(String tablero, char ficha, int pos) {

                // Comprobar si se gana, para ello comprobar en la pieza recien colocada si alguna
                // de las lineas en las que forma hay 3 piezas del mismo tipo que la colocada.

                switch (pos) {
                    case 0:
                        return this.tresEnRaya(tablero, ficha, 0, 1, 2) ||
                                this.tresEnRaya(tablero, ficha, 0, 3 ,6) ||
                                this.tresEnRaya(tablero, ficha, 0, 4 ,8);
                    case 1:
                        return this.tresEnRaya(tablero, ficha, 0, 1, 2) ||
                                this.tresEnRaya(tablero, ficha, 1, 4 ,7);
                    case 2:
                        return this.tresEnRaya(tablero, ficha, 0, 1, 2) ||
                                this.tresEnRaya(tablero, ficha, 2, 5 ,8) ||
                                this.tresEnRaya(tablero, ficha, 2, 4 ,6);
                    case 3:
                        return this.tresEnRaya(tablero, ficha, 0, 3, 6) ||
                                this.tresEnRaya(tablero, ficha, 3, 4 ,5);
                    case 4:
                        return this.tresEnRaya(tablero, ficha, 1, 4, 7) ||
                                this.tresEnRaya(tablero, ficha, 3, 4 ,5) ||
                                this.tresEnRaya(tablero, ficha, 0, 4 ,8) ||
                                this.tresEnRaya(tablero, ficha, 2, 4 ,6);

                    case 5:
                        return this.tresEnRaya(tablero, ficha, 2, 5, 8) ||
                                this.tresEnRaya(tablero, ficha, 3, 4 ,5);
                    case 6:
                        return this.tresEnRaya(tablero, ficha, 0, 3, 6) ||
                                this.tresEnRaya(tablero, ficha, 6, 7 ,8) ||
                                this.tresEnRaya(tablero, ficha, 2, 4 ,6);
                    case 7:
                        return this.tresEnRaya(tablero, ficha, 1, 4, 7) ||
                                this.tresEnRaya(tablero, ficha, 6, 7 ,8);
                    case 8:
                        return this.tresEnRaya(tablero, ficha, 0, 4, 8) ||
                                this.tresEnRaya(tablero, ficha, 6, 7 ,8) ||
                                this.tresEnRaya(tablero, ficha, 2, 5 ,8);
                }

                return false;
            }

            private boolean tresEnRaya(String tablero,char ficha, int pos1, int pos2, int pos3) {
                // Pre: 0<= pos1,pos2,pos3 <= 8, ficha = 'X' or 'O', tablero string de 9 chars formado por 'X', 'O' y '-'

                // Comprueba si las 3 posiciones en el tablero tienen la ficha indica
                return tablero.charAt(pos1) == ficha && tablero.charAt(pos2) == ficha && tablero.charAt(pos3) == ficha;
            }
        }
    }


    private class BotonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // El único botón redirige una interfaz atrás, a la de partidas disponibles
            ActividadPadre.quitarDeIntent("oponente");
            ActividadPadre.quitarDeIntent("tuTurno");

            ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);
        }
    }

}

