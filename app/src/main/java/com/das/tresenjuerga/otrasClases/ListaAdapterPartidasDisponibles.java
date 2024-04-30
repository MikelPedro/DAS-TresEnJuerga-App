package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;

public class ListaAdapterPartidasDisponibles extends ListaAdapterBase {

    private boolean tuTurno;
    private String oponente;

    public ListaAdapterPartidasDisponibles(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        int idEstado = super.getInteger(pos, 1);
        boolean solicitud = idEstado == 0;
        this.tuTurno = idEstado == 2;
        this.oponente = super.getString(pos, 0);


        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Llenar los campos de contra quien juega y a quien le toca
        ((TextView)view.findViewById(R.id.instanciaPartidaT_Oponente)).setText(this.oponente);

        String textoTurno = "";


        switch (idEstado) {
            case 0:
                textoTurno = ActividadPadre.getActividadActual().getString(R.string.peticion);
                break;
            case 1:
                textoTurno = ActividadPadre.getActividadActual().getString(R.string.turnoDelOtro);
                break;
            case 2:
                textoTurno = ActividadPadre.getActividadActual().getString(R.string.tuTurno);

        }


        ((TextView)view.findViewById(R.id.instanciaPartidaT_Turno)).setText(textoTurno);

        // Dar los listeners correspondientes a los botones


        if (solicitud) {
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setOnClickListener(new BotonListener(pos,0));
            view.findViewById(R.id.instanciaPartidaB_Entrar).setOnClickListener(new BotonListener(pos, 1));

        } else {
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setVisibility(View.GONE);
            view.findViewById(R.id.instanciaPartidaB_Entrar).setOnClickListener(new BotonListener(pos, 2));

        }







        return view;
    }

    private class BotonListener implements View.OnClickListener {


        private int pos;
        private int id;
        public BotonListener(int pos, int id) {
            this.pos = pos;
            this.id = id;
        }

        @Override
        public void onClick(View v) {

            String user = ActividadPadre.getActividadActual().obtenerDeIntent("user");


            switch (this.id) {
                case 0:

                    // TODO: Rechazar solicitud de match

                    break;

                case 1:

                    // TODO: Aceptar solicitud de match

                    break;

                case 2:
                    // TODO: Pasar a la UI del tic tac toe que muestra la partida.

                    // tuTurno tiene el valor de si es el turno del player o no, no se requiere consultar esa
                    // info a bd de nuevo. oponente contiene el nombre del oponente. El nombre del user actual
                    // está en "user" en el intent de la actividad.

            }



        }
    }
}
