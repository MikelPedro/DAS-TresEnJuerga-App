package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.tresenjuerga.R;

public class ListaAdapterSolicitudes extends ListaAdapterBase {

    public ListaAdapterSolicitudes(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();


        ((TextView)view.findViewById(R.id.instanciaSolicitudT_Nombre)).setText(super.getString(pos, 0));


        // Dar los listeners correspondientes a los botones
        view.findViewById(R.id.instanciaSolicitudB_Aceptar).setOnClickListener(new BotonListener(0));
        view.findViewById(R.id.instanciaSolicitudB_Rechazar).setOnClickListener(new BotonListener(1));






        return view;
    }



    private class BotonListener implements View.OnClickListener {


        private int id;
        public BotonListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {

            // TODO: Solicitudes de amistad
            switch (this.id) {
                case 0:
                    // Aceptar

                    break;
                case 1:

                    // Rechazar


            }

        }
    }
}
