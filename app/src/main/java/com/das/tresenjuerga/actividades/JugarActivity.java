package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private int estiloX;
    private int estiloO;

    private View fragmento;

    private void esperar(int ms) {
        // esperar por X millis
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_jugar);





    }

    @Override
    protected void onStart() {

        super.onStart();

        ActividadPadre.lockBotones(true);


        // Obtener el fragmento de la actividad
        this.fragmento = ActividadPadre.obtenerFragmentoOrientacion();


        // Checkear flags para ver si se requiere hacer redirect porque no tiene sentido mostrar la partida

        if (ActividadPadre.obtenerDeIntent("expulsadoPorNoAmigo").contentEquals("true")) {

            // Si el oponente te quita como amigo cuando estás viendo la partida, esta se borra.
            // No tiene sentido visualizarla, por lo que se redirige una pantalla atrás

            ActividadPadre.quitarDeIntent("expulsadoPorNoAmigo");
            ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);


        // Checkear redirects por fin de partida

        } else if (!ActividadPadre.obtenerDeIntent("oponenteAcaboLaPartida").contentEquals("")) {

            // Si el oponente borró la partida porque se acabó, ir a la screen de resultados

            ActividadPadre.añadirAIntent("resultado", ActividadPadre.obtenerDeIntent("oponenteAcaboLaPartida"));
            ActividadPadre.quitarDeIntent("oponenteAcaboLaPartida");

            this.acabarPartida();

        } else if (!ActividadPadre.obtenerDeIntent("resultado").contentEquals("")) {

            // Si se rotó el móvil antes de mandar la instrucicón de borrar la partida durante el sleep de 3s

            ((TextView)this.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(R.string.finDePartida);
            String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), ActividadPadre.obtenerDeIntent("resultado")};

            // Informar al servidor de que quite la partida
            ActividadPadre.peticionAServidor("partidas",5, datos, new ObservadorDeQuitarPartida()); // quitar la partida de BD

        } else {
            ActividadPadre.lockBotones(false);
        }

        // Dar listeners a los botones
        this.fragmento.findViewById(R.id.partidaB_Volver).setOnClickListener(new BotonListener());

        // Pedir al servidor el estado de la partida
        String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
        ActividadPadre.peticionAServidor("partidas", 3, datos, new ObservadorDeTablero());


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estiloTablero","0");

        int estiloFondo =  super.getResources().getIdentifier("fondo_"+estilo,"drawable",super.getPackageName());
        ((ImageView)this.fragmento.findViewById(R.id.partidaI_Fondo)).setImageResource(estiloFondo);
        this.estiloX = super.getResources().getIdentifier("x_"+estilo,"drawable",super.getPackageName());
        this.estiloO = super.getResources().getIdentifier("o_"+estilo,"drawable",super.getPackageName());


        /*
            Source imagenes:

            0:
               fondo: https://www.istockphoto.com/es/foto/pizarra-ticktacktoe-fondo-gm946399874-258448731?searchscope=image%2Cfilm
               figuras: https://www.istockphoto.com/es/foto/tick-tack-toe-en-la-pizarra-gm1134456281-301461381?searchscope=image%2Cfilm
            1:

                fondo: https://static.vecteezy.com/system/resources/previews/001/918/357/original/tic-tac-toe-game-linear-outline-icon-neon-style-light-decoration-icon-vector.jpg
                figuras: https://play-lh.googleusercontent.com/KZTO1L6r8CzWlg2InJoU_ndRAuvYiaS-35MyYdDPeeVCPNVfM9SBY2qRGjvvADmDIR8

            2:
                Hecho por Iván Mata, no source link

                fondo:  https://www.istockphoto.com/es/foto/pizarra-ticktacktoe-fondo-gm946399874-258448731?searchscope=image%2Cfilm (editado por Mikel Pedro)
                figuras: https://www.flaticon.es/icono-gratis/cerveza_931949?term=cerveza&page=1&position=1&origin=search&related_id=931949
                         https://www.flaticon.es/icono-gratis/uva_1412542?term=uva&page=1&position=2&origin=search&related_id=1412542

            3:





         */

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


        ImageView casilla = (ImageView) this.fragmento.findViewById(super.getResources().getIdentifier("partidaI_"+pos, "id", super.getPackageName()));
        char figura = this.tablero.charAt(pos);

        switch (figura) {
            case 'X': // X
                casilla.setImageResource(this.estiloX);
                break;
            case 'O': // 0
                casilla.setImageResource(this.estiloO);
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

            // Poner el dibujo de la ficha como la que juegas

            ImageView figuraMia = (ImageView)actividad.fragmento.findViewById(R.id.partidaI_TuFigura);

            if (actividad.figura == 'X') {
                figuraMia.setImageResource(actividad.estiloX);
            } else {
                figuraMia.setImageResource(actividad.estiloO);

            }


            for (int i = 0; i != 9; i++) {
                // Dar un listener a cada imagen del tablero, para que puedan responder cuando se pinchan en ellas
                actividad.actualizarCasillaEnUI(i).setOnClickListener(new ImagenListener(i));
            }


            // Settear titulo ("Partida vs [BLANK]")
            ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Titulo)).setText(actividad.getResources().getString(R.string.tituloPartida) + " " + ActividadPadre.obtenerDeIntent("oponente"));


            if (super.getBoolean("finalizado")) {

                // Fin del match

                ActividadPadre.lockBotones(true);
                ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(R.string.finDePartida);

            } else if (ActividadPadre.obtenerDeIntent("tuTurno").contentEquals(Boolean.toString(true))) {

                // Tu turno

                ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(actividad.getResources().getString(R.string.descPartidaTuTurno));

            } else {

                // Turno del otro

                ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(actividad.getResources().getString( R.string.descPartidaTurnoDelOtro));

            }






        }
    }
    private void acabarPartida() {

        // Pre: 0 (loss), 1 (draw), 2 (Win) desde este POV en "resultado"

        // Guardar la info relevante y pasar a la interfaz de revancha
        ActividadPadre.añadirAIntent("estadoRevancha", "0"); // por defecto, el estado de revancha es 0 (ver interfaz de revancha para más info)
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

                ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(R.string.procesandoJugada);

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

            private class ObservadorDeFinDeJuego extends ObservadorDePeticion {

                private int estadoFinal;

                public ObservadorDeFinDeJuego(int fin) {this.estadoFinal = fin;}

                @Override
                protected void ejecutarTrasPeticion() {
                    // Si se gana, esperar 3s para mostrar la matriz un poco antes de forzar redirect a la pantalla de revancha
                    JugarActivity.this.esperar(3000);

                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), Integer.toString(this.estadoFinal)};

                    // Informar al servidor de que quite la partida porque se ha acabado en victoria
                    ActividadPadre.peticionAServidor("partidas",5, datos, new ObservadorDeQuitarPartida()); // quitar la partida de BD
                }
            }

            @Override
            protected void ejecutarTrasPeticion() {

                // El servidor nos responde que ha updateado la partida


                // Comprobar si la partida acaba

                JugarActivity actividad = JugarActivity.this;
                String tablero = actividad.tablero;
                char ficha = actividad.figura;

                if (this.gana(tablero, ficha, this.jugada)) {
                    ActividadPadre.añadirAIntent("resultado", "2"); // 0, 1 or 2.
                    ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(R.string.finDePartida);
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "2"};

                    // Notificar que la partida va a acabar en victoria
                    ActividadPadre.peticionAServidor("partidas", 8, datos, new ObservadorDeFinDeJuego(2));



                } else if (!tablero.contains("-")) {
                    ActividadPadre.añadirAIntent("resultado", "1");
                    ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(R.string.finDePartida);
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "1"};

                    // Notificar que la partida va a acabar en empate
                    ActividadPadre.peticionAServidor("partidas", 8, datos, new ObservadorDeFinDeJuego(1));


                } else {
                    // Cambiar la descripción de la partida para indicar que no es tu turno
                    ((TextView)actividad.fragmento.findViewById(R.id.partidaT_Descripcion)).setText(actividad.getResources().getString(R.string.descPartidaTurnoDelOtro));

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
    private class ObservadorDeQuitarPartida extends ObservadorDePeticion {



        @Override
        protected void ejecutarTrasPeticion() {
            // El servidor nos responde con que ha quitado ya la partida

            JugarActivity.this.acabarPartida();



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

