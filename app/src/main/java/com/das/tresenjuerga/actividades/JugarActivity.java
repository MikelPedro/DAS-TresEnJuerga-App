package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class JugarActivity extends ActividadPadre {

    // Al updatear la partida. Si se hittea wincon / drawcon. Primero updatear el tablero con la jugada.
    // y luego (como a los 5s, usar una tarea programa o thread.sleep [lockear los botones en este ultimo caso])
    // mandar a BD la peticion de fin de partida.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_jugar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

    }


    public boolean esElOponente(String oponente) {
        return ActividadPadre.obtenerDeIntent("oponente").contentEquals(oponente);
    }

    // si se sale de esta interfaz, quitar del intent la key "oponente y tuTurno"

}

