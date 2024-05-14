package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterSolicitudes;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class AmigoSolicitudesActivity extends ActividadPadre {


    /*
        Esta actividad muestra una lista con todas las solicitudes de amistad.

        Por cada solicitud, se permite aceptarla o rechazarla


     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_amigo_solicitudes);
    }
    @Override
    protected void onStart() {
        super.onStart();


        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Localizar el ListView
        ListView listView = super.findViewById(R.id.amigoSolicitudesL_Solicitudes);

        // Dar listeners a los botones
        fragmento.findViewById(R.id.amigoSolicitudesB_Volver).setOnClickListener(new BotonListener());


        // Preguntar a server que gente nos ha mandado solicitudes
        String[] datos =  {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("amistades",2,datos,new ObservadorDeSolicitudes(listView));



    }

    private class ObservadorDeSolicitudes extends ObservadorDePeticion {

        private ListView listView;

        public ObservadorDeSolicitudes (ListView listView) {this.listView = listView;}
        @Override
        protected void ejecutarTrasPeticion() {

            // El server nos los nombres de los users que han mandado friend request

            // Obtener orientación cardview

            int cardview;
            if (ActividadPadre.enLandscape()) {
                cardview = R.layout.cardview_solicitud_amistad_landscape;
            } else {
                cardview = R.layout.cardview_solicitud_amistad_portrait;
            }

            // Recoger la variable con los nombres de la gente que nos ha mandado solicitud
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
            // El único botón redirige una pantalla atrás, a la actividad de amigos.
            ActividadPadre.redirigirAActividad(AmigosActivity.class);
        }
    }
}
