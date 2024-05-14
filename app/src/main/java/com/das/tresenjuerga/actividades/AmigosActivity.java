package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterMisAmigos;
import com.das.tresenjuerga.otrasClases.ListaAdapterSolicitudes;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class AmigosActivity extends ActividadPadre {


    /*
        Esta actividad muestra la lista de amigos de un usuario loggeado.

        Por cada amigo se da la opción de retarlo, ver su perfil o eliminarlo de la lista de amigos.
        Además, se tienen botones extra para ver las peticiones de amistad o añadir un amigo nuevo



     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_amigos);


    }

    @Override
    protected void onStart() {


        super.onStart();

        // Cargar el fragmento
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Encontrar el listview de los amigos
        ListView listView = super.findViewById(R.id.amigosL_Amigos);

        // Dar listeners a los botones
        fragmento.findViewById(R.id.amigosB_Solicitudes).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.amigosB_AñadirAmigo).setOnClickListener(new BotonListener(1));
        fragmento.findViewById(R.id.amigosB_Volver).setOnClickListener(new BotonListener(2));

        // Preguntar a server por los nombres de nuestros amigos actuales
        String[] datos =  {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("amistades",5,datos,new ObservadorDeSolicitudes(listView));




    }
    private class ObservadorDeSolicitudes extends ObservadorDePeticion {

        private ListView listView;

        public ObservadorDeSolicitudes (ListView listView) {this.listView = listView;}
        @Override
        protected void ejecutarTrasPeticion() {

            // El servidor nos responde con los nombres de nuestros amigos

            // Obtener orientación cardview a cargar en el ListView

            int cardview;
            if (ActividadPadre.enLandscape()) {
                cardview = R.layout.cardview_amigo_landscape;
            } else {
                cardview = R.layout.cardview_amigo_portrait;
            }

            // Recoger la variable que nos da los nombres de nuestros amigos
            Object[] listaValores = super.getStringArray("nombres");

            // Montar el listview, cargando el cardview correspondiente
            ListaAdapterMisAmigos adapter = new ListaAdapterMisAmigos(listaValores, cardview);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);


        }
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
                    // Este botón redirige a la pestaña de solicitudes de amigos
                    ActividadPadre.redirigirAActividad(AmigoSolicitudesActivity.class);
                    break;

                case 1:
                    // Este botón carga la interfaz de añadir un nuevo amigo
                    ActividadPadre.redirigirAActividad(AnadirAmigoActivity.class);
                    break;

                case 2:
                    // Este botón vuelve una pantalla atrás, al menú principal de usuario loggeado
                    ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

            }




        }
    }
}