package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class AnadirAmigoActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_anadir_amigo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = super.obtenerFragmentoOrientacion();
        fragmento.findViewById(R.id.añadirAmigoB_Añadir).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.añadirAmigoB_Volver).setOnClickListener(new BotonListener(1));


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
                    // TODO: Handlear solicitud

                    break;
                case 1:
                    // Volver a amigos
                    AnadirAmigoActivity.super.redirigirAActividad(AmigosActivity.class);


            }
        }
    }
}