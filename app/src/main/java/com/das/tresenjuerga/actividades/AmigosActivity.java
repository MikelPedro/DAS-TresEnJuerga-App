package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterMisAmigos;

public class AmigosActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_amigos);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.amigosL_Amigos);
        fragmento.findViewById(R.id.amigosB_Solicitudes).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.amigosB_AñadirAmigo).setOnClickListener(new BotonListener(1));
        fragmento.findViewById(R.id.amigosB_Volver).setOnClickListener(new BotonListener(2));


        // Obtener orientación cardview

        int cardview;
        if (super.enLandscape()) {
            cardview = R.layout.cardview_amigo_landscape;
        } else {
            cardview = R.layout.cardview_amigo_portrait;
        }

        Object[] listaValores = {"A"}; // Valor placeholder, TODO: pedir a BD esta data

        // Montar el listview
        ListaAdapterMisAmigos adapter = new ListaAdapterMisAmigos(listaValores, cardview);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

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
                    ActividadPadre.redirigirAActividad(AmigoSolicitudesActivity.class);
                    break;

                case 1:
                    ActividadPadre.redirigirAActividad(AnadirAmigoActivity.class);
                    break;

                case 2:
                    ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

            }




        }
    }
}