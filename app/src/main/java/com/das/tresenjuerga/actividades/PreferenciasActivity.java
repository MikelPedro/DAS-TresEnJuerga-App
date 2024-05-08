package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.das.tresenjuerga.R;

public class PreferenciasActivity extends ActividadPadre {


    // TODO: Añadir mas preferencias, como el estilo de tic tac toe a usar. Por ahora solo se usa el de pizarra (el de por defecto)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_preferencias);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        fragmento.findViewById(R.id.preferenciasB_Volver).setOnClickListener(new BotonListener());


    }

    @Override
    protected void setEstilo(View fragmento) {
        super.setEstilo(fragmento);
        ViewGroup viewGroup = (ViewGroup) fragmento;

        viewGroup.getChildAt(0).setBackgroundColor(Color.WHITE);
    }


    private class BotonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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