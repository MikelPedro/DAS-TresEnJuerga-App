package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;

public class ListaAdapterSolicitudes extends ListaAdapterBase {

    private String nombreSolicitante;

    public ListaAdapterSolicitudes(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        this.nombreSolicitante = super.getString(pos, 0);
        ((TextView)view.findViewById(R.id.instanciaSolicitudT_Nombre)).setText(this.nombreSolicitante);


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

            String[] datos = {ActividadPadre.getActividadActual().obtenerDeIntent("user"), ListaAdapterSolicitudes.this.nombreSolicitante};

            switch (this.id) {
                case 0:
                    // Aceptar
                    ActividadPadre.peticionAServidor("amistades", 3, datos, null);

                    break;

                case 1:
                    // Rechazar
                    ActividadPadre.peticionAServidor("amistades", 4, datos, null);

                    break;


            }

        }
    }
}
