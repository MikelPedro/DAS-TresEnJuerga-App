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
        View fragmento = super.obtenerFragmentoOrientacion();
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
                    break;
                case 1:
                    break;
                case 2:
                    UsuarioLoggeadoActivity.super.añadirAIntent("userAVisualizar", UsuarioLoggeadoActivity.super.obtenerDeIntent("user"));
                    UsuarioLoggeadoActivity.super.redirigirAActividad(PerfilActivity.class);
                    break;
                case 3:
                    // Log Off, volver a Main Activity
                    UsuarioLoggeadoActivity.super.quitarDeIntent("user");
                    UsuarioLoggeadoActivity.super.redirigirAActividad(MainActivity.class);


            }
        }
    }
}