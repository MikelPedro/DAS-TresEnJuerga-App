package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class UsuarioLoggeadoActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_usuario_loggeado);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
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
                    ActividadPadre.redirigirAActividad(PartidasDisponiblesActivity.class);
                    break;
                case 1:
                    ActividadPadre.añadirAIntent("userAVisualizar", UsuarioLoggeadoActivity.super.obtenerDeIntent("user"));
                    ActividadPadre.redirigirAActividad(PerfilActivity.class);
                    break;
                case 2:
                    ActividadPadre.redirigirAActividad(AmigosActivity.class);

                    break;
                case 3:
                    // Log Off, volver a Main Activity
                    ActividadPadre.quitarDeIntent("user");
                    ActividadPadre.redirigirAActividad(MainActivity.class);


            }
        }
    }
}