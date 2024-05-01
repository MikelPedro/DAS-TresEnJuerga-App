package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterSolicitudes;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class AmigoSolicitudesActivity extends ActividadPadre {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_amigo_solicitudes);
    }
    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.amigoSolicitudesL_Solicitudes);
        fragmento.findViewById(R.id.amigoSolicitudesB_Volver).setOnClickListener(new BotonListener());

        String[] datos =  {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("amistades",2,datos,new ObservadorDeSolicitudes(listView));



    }

    private class ObservadorDeSolicitudes extends ObservadorDePeticion {

        private ListView listView;

        public ObservadorDeSolicitudes (ListView listView) {this.listView = listView;}
        @Override
        protected void ejecutarTrasPeticion() {

            // Obtener orientación cardview

            int cardview;
            if (ActividadPadre.enLandscape()) {
                cardview = R.layout.cardview_solicitud_amistad_landscape;
            } else {
                cardview = R.layout.cardview_solicitud_amistad_portrait;
            }

            Object[] listaValores = super.getStringArray("nombres");

            // Montar el listview
            ListaAdapterSolicitudes adapter = new ListaAdapterSolicitudes(listaValores, cardview);
            adapter.notifyDataSetChanged();
            this.listView.setAdapter(adapter);


        }
    }


    private class BotonListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            // Redirigir a la actividad de amigos.
            ActividadPadre.redirigirAActividad(AmigosActivity.class);
        }
    }
}
