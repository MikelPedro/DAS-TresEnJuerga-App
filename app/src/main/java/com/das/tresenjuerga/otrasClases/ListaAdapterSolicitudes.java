package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.MainActivity;

public class ListaAdapterSolicitudes extends ListaAdapterBase {

    // El ArrayAdapter que muestra las solicitudes de amistad

    private String nombreSolicitante;

    public ListaAdapterSolicitudes(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Obtener el nombre de la persona que solicita
        this.nombreSolicitante = super.getString(pos, 0);
        ((TextView)view.findViewById(R.id.instanciaSolicitudT_Nombre)).setText(this.nombreSolicitante);


        // Dar los listeners correspondientes a los botones
        view.findViewById(R.id.instanciaSolicitudB_Aceptar).setOnClickListener(new BotonListener(0, this.nombreSolicitante));
        view.findViewById(R.id.instanciaSolicitudB_Rechazar).setOnClickListener(new BotonListener(1, this.nombreSolicitante));






        return view;
    }



    private class BotonListener implements View.OnClickListener {


        private int id;
        private String nombreSolicitante;
        public BotonListener(int id, String nombreSolicitante) {
            this.id = id;
            this.nombreSolicitante = nombreSolicitante;
        }

        @Override
        public void onClick(View v) {

            String[] datos = {ActividadPadre.getActividadActual().obtenerDeIntent("user"), this.nombreSolicitante};

            switch (this.id) {
                case 0:
                    // Aceptar petición, pedir esto a servidor
                    ActividadPadre.peticionAServidor("amistades", 3, datos, new ObservadorDeProcesamientoDeSolicitud());

                    break;

                case 1:
                    // Rechazar petición, pedir a servidor esto
                    ActividadPadre.peticionAServidor("amistades", 4, datos, new ObservadorDeProcesamientoDeSolicitud());

                    break;


            }

        }
    }

    private class ObservadorDeProcesamientoDeSolicitud extends ObservadorDePeticion {
        @Override
        protected void ejecutarTrasPeticion() {

            // Tras procesar una solicitud, refrescar la UI para quitarla de la lista

            ActividadPadre.recargarActividad();


        }
    }
}
