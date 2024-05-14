package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class UsuarioLoggeadoActivity extends ActividadPadre {

    /*
        Esta es la pantalla principal de un usuario loggeado
        Un usuario loggeado puede ser sus partidas, amigos y perfil


     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_usuario_loggeado);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        fragmento.findViewById(R.id.usuarioLoggeadoB_Jugar).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.usuarioLoggeadoB_Perfil).setOnClickListener(new BotonListener(1));
        fragmento.findViewById(R.id.usuarioLoggeadoB_Amigos).setOnClickListener(new BotonListener(2));
        fragmento.findViewById(R.id.usuarioLoggeadoB_Salir).setOnClickListener(new BotonListener(3));


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
                    // Este botón va a la partidas disponibles
                    ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);
                    break;
                case 1:
                    // Este botón abre el perfil del usuario
                    ActividadPadre.añadirAIntent("userAVisualizar", UsuarioLoggeadoActivity.super.obtenerDeIntent("user"));
                    ActividadPadre.redirigirAActividad(PerfilActivity.class);
                    break;
                case 2:
                    // Este botón abre la lista de amigos
                    ActividadPadre.redirigirAActividad(AmigosActivity.class);

                    break;
                case 3:
                    // Este botón cierra sesión y manda a la actividad principal
                    ActividadPadre.quitarDeIntent("user");
                    ActividadPadre.redirigirAActividad(MainActivity.class);


            }
        }
    }
}