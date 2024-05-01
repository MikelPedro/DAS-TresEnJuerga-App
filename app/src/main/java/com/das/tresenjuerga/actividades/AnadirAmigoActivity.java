package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class AnadirAmigoActivity extends ActividadPadre {

    // TODO: Obtener la lista de gente que pueden ser amigos.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_anadir_amigo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
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
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ((EditText)AnadirAmigoActivity.super.findViewById(R.id.añadirAmigoE_User)).getText().toString()};
                    ActividadPadre.peticionAServidor("amistades", 1, datos, new ObservadorDeAñadirAmigo());

                    break;
                case 1:
                    // Volver a amigos
                    ActividadPadre.redirigirAActividad(AmigosActivity.class);


            }
        }

        private class ObservadorDeAñadirAmigo extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                long respuesta = super.getLong("respuesta");
                System.out.println(respuesta);

                if (respuesta == 0) {
                    ActividadPadre.mostrarToast(R.string.solicitudAñadida);

                } else {
                    ActividadPadre.mostrarToast(ActividadPadre.getActividadActual().getResources().getIdentifier("errorAmigo"+respuesta, "string", ActividadPadre.getActividadActual().getPackageName()));

                }
            }
        }
    }


}