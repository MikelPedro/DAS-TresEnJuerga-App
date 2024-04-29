package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterSolicitudes;

public class AmigoSolicitudesActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_amigo_solicitudes);
    }
    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = super.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.amigoSolicitudesL_Solicitudes);
        fragmento.findViewById(R.id.amigoSolicitudesB_Volver).setOnClickListener(new BotonListener());



        // Obtener orientación cardview

        int cardview;
        if (super.enLandscape()) {
            cardview = R.layout.cardview_solicitud_amistad_landscape;
        } else {
            cardview = R.layout.cardview_solicitud_amistad_portrait;
        }

        Object[] listaValores = {"A, B, C"}; // Valor placeholder, TODO: pedir a BD esta data

        // Montar el listview
        ListaAdapterSolicitudes adapter = new ListaAdapterSolicitudes(listaValores, cardview);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

    }


    private class BotonListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            // Redirigir a la actividad de amigos.
            AmigoSolicitudesActivity.super.redirigirAActividad(AmigosActivity.class);



        }




        }
    }
