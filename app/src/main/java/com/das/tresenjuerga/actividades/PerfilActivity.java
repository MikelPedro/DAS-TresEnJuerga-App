package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.das.tresenjuerga.R;

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

        this.miPerfil = super.obtenerDeIntent("userAVisualizar").contentEquals(super.obtenerDeIntent("user"));

        // TODO: Descargar foto here

        View fragmento = super.obtenerFragmentoOrientacion();
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
                    // TODO: Pushear a BD la solicitud de jugar la partida si es posible


                    break;
                case 2:
                    PerfilActivity.super.quitarDeIntent("userAVisualizar");

                    // Dependiendo de si estoy viendo mi perfil o no, deducir de donde vinimos para volver allí
                    if (PerfilActivity.this.miPerfil) {
                        PerfilActivity.super.redirigirAActividad(UsuarioLoggeadoActivity.class);

                    } else {
                        PerfilActivity.super.redirigirAActividad(AmigosActivity.class);

                    }


            }
        }
    }
}