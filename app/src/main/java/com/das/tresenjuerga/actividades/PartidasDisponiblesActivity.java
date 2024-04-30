package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterPartidasDisponibles;

public class PartidasDisponiblesActivity extends ActividadPadre {

    // NOTA: EN ESTA INTERFAZ EN LA QUE SE MUESTRAN LAS PARTIDAS DEL JUGADOR EN CURSO TAMBIÉN
    // SE MUESTRAN LAS SOLICITUDES DE JUGAR UNA PARTIDA. PERO SE MUESTRAN AL FONDO DE LA LISTA
    // CON UN ESTADO DISTINTO. SI QUERÉIS SE PUEDE MOVER A OTRA INTERFAZ PERO POR AHORA
    // ESTÁ PROGRAMADO ASÍ PARA TENER ALGO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_partidas_disponibles);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = super.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.partidasDisponiblesL_Partidas);
        fragmento.findViewById(R.id.partidasDisponiblesB_Volver).setOnClickListener(new BotonListener());

        // Obtener orientación cardview

        int cardview;
        if (super.enLandscape()) {
            cardview = R.layout.cardview_partida_landscape;
        } else {
            cardview = R.layout.cardview_partida_portrait;
        }


        Object[][] listaValores = {{"A", 1, "B", 0}}; // Valor placeholder, TODO: pedir a BD esta data

        // Montar el listview
        ListaAdapterPartidasDisponibles adapter = new ListaAdapterPartidasDisponibles(listaValores, cardview);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

    }


    private class BotonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // Volver a menú de registrado
            PartidasDisponiblesActivity.super.redirigirAActividad(UsuarioLoggeadoActivity.class);



        }
    }
}