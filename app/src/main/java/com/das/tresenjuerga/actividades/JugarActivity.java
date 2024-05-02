package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class JugarActivity extends ActividadPadre {

    // Al updatear la partida. Si se hittea wincon / drawcon. Primero updatear el tablero con la jugada.
    // y luego (como a los 5s, usar una tarea programa o thread.sleep [lockear los botones en este ultimo caso])
    // mandar a BD la peticion de fin de partida.

    private String tablero;
    private char figura;

    private View fragmento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_jugar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        this.fragmento.findViewById(R.id.partidaB_Volver).setOnClickListener(new BotonListener());
        String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
        ActividadPadre.peticionAServidor("partidas", 3, datos, new ObservadorDeTablero());




    }


    public boolean esElOponente(String oponente) {
        return ActividadPadre.obtenerDeIntent("oponente").contentEquals(oponente);
    }

    private ImageView actualizarCasillaEnUI(int pos) {

        // TODO: Settear la imagen de la casilla aquí.

        ImageView casilla = (ImageView) this.fragmento.findViewById(super.getResources().getIdentifier("partidaI_"+pos, "id", super.getPackageName()));
        char figura = this.tablero.charAt(pos);

        switch (figura) {
            case 'A': // X
               //  casilla.setImageIcon(); / casilla.setImageBitmap();  <- set image here
                break;
            case 'B': // 0

                break;
            case '-': // Casilla vacía

                break;

        }
        return casilla;
    }


    private class ObservadorDeTablero extends ObservadorDePeticion {

        @Override
        protected void ejecutarTrasPeticion() {
            JugarActivity actividad = JugarActivity.this;
            actividad.tablero = super.getString("tablero");
            actividad.figura = super.getString("miFigura").charAt(0);

            for (int i = 0; i != 9; i++) {
                actividad.actualizarCasillaEnUI(i).setOnClickListener(new ImagenListener(i));
            }



        }
    }


    private class ImagenListener implements View.OnClickListener {

        private int id;

        public ImagenListener(int id) {this.id = id;}
        @Override
        public void onClick(View v) {
            JugarActivity actividad = JugarActivity.this;


            if (ActividadPadre.obtenerDeIntent("tuTurno").contentEquals("false")) {
                ActividadPadre.mostrarToast(R.string.noEsTuTurno);


            } else if (actividad.tablero.charAt(this.id) == '-') {
                // Hacer play en client
                actividad.tablero = actividad.tablero.substring(0, this.id) + actividad.figura + actividad.tablero.substring(this.id+1, 9);
                actividad.actualizarCasillaEnUI(this.id);
                ActividadPadre.añadirAIntent("tuTurno", "false");
                String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), actividad.tablero};

                // Comunicar play en server
                ActividadPadre.peticionAServidor("partidas", 4, datos, new ObservadorDePlay());

            } else {
                ActividadPadre.mostrarToast(R.string.noPoderPonerAhi);
            }

        }


        private class ObservadorDePlay extends ObservadorDePeticion {

            @Override
            protected void ejecutarTrasPeticion() {

                ActividadPadre.lockRedirectsYPeticionesAServer(true); // Bloquear al user de que cambie de interfaz hasta que se
                                                                           // se terminen los 5 secs si la partida debería acabar
                // Comprobar si la partida acaba

                JugarActivity actividad = JugarActivity.this;
                String tablero = actividad.tablero;

                if (false /* TODO: Calcular wincon here */) {

                    // WIN
                    this.esperar(5000);
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "2"};
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);
                    ActividadPadre.peticionAServidor("partidas",5, datos, null); // quitar la partida de BD
                    // TODO: Crear pop-up o actividad o lo que sea donde mostrar result screen


                } else if (!tablero.contains("-")) {
                    // DRAW
                    this.esperar(5000);
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente"), "1"};
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);
                    ActividadPadre.peticionAServidor("partidas",5, datos, null); // quitar la partida de BD
                    // TODO: Crear pop-up o actividad o lo que sea donde mostrar result screen

                } else {
                    ActividadPadre.lockRedirectsYPeticionesAServer(false);
                }

            }


            private void esperar(int ms) {
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private class BotonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ActividadPadre.quitarDeIntent("oponente");
            ActividadPadre.quitarDeIntent("tuTurno");

            ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);
        }
    }

}

