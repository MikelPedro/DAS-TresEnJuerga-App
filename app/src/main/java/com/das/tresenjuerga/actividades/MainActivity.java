package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class MainActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

    }


    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = super.obtenerFragmentoOrientacion();
        fragmento.findViewById(R.id.mainB_InicioSesion).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.mainB_Registrar).setOnClickListener(new BotonListener(1));
        fragmento.findViewById(R.id.mainB_Salir).setOnClickListener(new BotonListener(2));

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
                    // Ir a menú de inicio sesión
                    break;
                case 1:
                    // Ir al menú de registro
                    MainActivity.super.redirigirAActividad(RegistroActivity.class);
                    break;
                case 2:
                    // TODO: Implementar dialogo de confirmar cierre de app aquí

            }
        }
    }
}