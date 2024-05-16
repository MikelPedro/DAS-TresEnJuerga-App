package com.das.tresenjuerga.actividades;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.das.tresenjuerga.R;

public class PreferenciasActivity extends ActividadPadre {


    /*
        Esta pantalla muestra las preferencias del usuario, que se guardan incluso cuando se cierra la app
        Los personalizables son:

        * Idioma:
          - Español
          - Inglés

        * Estilo general
          - Día
          - Neón

        * Tipo de tablero

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_preferencias);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        fragmento.findViewById(R.id.preferenciasB_Volver).setOnClickListener(new BotonListener());


    }

    @Override
    protected void setEstilo(View fragmento) {

        // Overridear setEstilo de ActividadPadre, para poder añadir al estilo que el fondo del contenedor
        // de las preferencias sea blanco (para que pueda ser siempre visible)

        super.setEstilo(fragmento);
        ViewGroup viewGroup = (ViewGroup) fragmento;

        viewGroup.getChildAt(0).setBackgroundColor(Color.CYAN);
    }


    private class BotonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Si se pincha en el botón, volver a la actividad desde la que se llamó

            String actividadQueLlama = ActividadPadre.obtenerDeIntent("actividadQueLlama");
            ActividadPadre.quitarDeIntent("actividadQueLlama");
            try {
                // Source: https://stackoverflow.com/questions/5401467/convert-string-into-a-class-object
                ActividadPadre.redirigirAActividad(Class.forName("com.das.tresenjuerga.actividades."+actividadQueLlama));
            } catch (ClassNotFoundException e) {
                System.out.println("Actividad para returnear no encontrada");
                throw new RuntimeException(e);
            }

        }
    }
}