package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterMisAmigos;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class PerfilActivity extends ActividadPadre {

    private boolean miPerfil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_perfil);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.miPerfil = ActividadPadre.obtenerDeIntent("userAVisualizar").contentEquals(ActividadPadre.obtenerDeIntent("user"));

        // TODO: Descargar foto here

        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        fragmento.findViewById(R.id.perfilB_Volver).setOnClickListener(new BotonListener(2));


        Button botonUtilidad = fragmento.findViewById(R.id.perfilB_CambiarFoto);


        // Esta interfaz se usa para ver los datos del perfil de ti y de tus amigos.
        // Si estás viendo el perfil de un amigo se le permite retar a una partida
        // Si estás viendo tu perfil, se te permite cambiar la foto

        if (this.miPerfil) {
            botonUtilidad.setText(super.getString(R.string.cambiarFoto));
            botonUtilidad.setOnClickListener(new BotonListener(0));

        } else {
            botonUtilidad.setText(super.getString(R.string.retar));
            botonUtilidad.setOnClickListener(new BotonListener(1));

        }
    }


    private class BotonListener implements View.OnClickListener {


        private int id;
        public BotonListener(int id) {
            this.id = id;
        }
        @Override
        public void onClick(View v) {
            switch (this.id) {
                case 0:
                    // TODO: Cambiar foto here
                    break;
                case 1:
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("userAVisualizar")};
                    ActividadPadre.peticionAServidor("partidas", 0, datos, new ObservadorDeRetarPartida());

                    break;
                case 2:
                    ActividadPadre.quitarDeIntent("userAVisualizar");

                    // Dependiendo de si estoy viendo mi perfil o no, deducir de donde vinimos para volver allí
                    if (PerfilActivity.this.miPerfil) {
                        ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

                    } else {
                        ActividadPadre.redirigirAActividad(AmigosActivity.class);

                    }


            }
        }

        private class ObservadorDeRetarPartida extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                if (super.getBoolean("respuesta")) {
                    ActividadPadre.mostrarToast(R.string.retarCorrecto);

                } else {
                    ActividadPadre.mostrarToast(R.string.retarError);

                }
            }
        }
    }
}