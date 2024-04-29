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
        this.tuTurno = super.getBoolean(pos, 1);
        this.oponente = super.getString(pos, 0);


        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Llenar los campos de contra quien juega y a quien le toca
        ((TextView)view.findViewById(R.id.instanciaPartidaT_Oponente)).setText(this.oponente);

        String textoTurno;
        if (this.tuTurno) {
            textoTurno = ActividadPadre.getActividadActual().getString(R.string.tuTurno);

        } else {
            textoTurno = ActividadPadre.getActividadActual().getString(R.string.turnoDelOtro);

        }

        ((TextView)view.findViewById(R.id.instanciaPartidaT_Turno)).setText(textoTurno);


        // Dar los listeners correspondientes a los botones
        view.findViewById(R.id.instanciaPartidaB_Entrar).setOnClickListener(new BotonListener(pos));






        return view;
    }

    private class BotonListener implements View.OnClickListener {


        private int pos;
        public BotonListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            // TODO: Pasar a la UI del tic tac toe que muestra la partida.

            // tuTurno tiene el valor de si es el turno del player o no, no se requiere consultar esa
            // info a bd de nuevo. oponente contiene el nombre del oponente. El nombre del user actual
            // está en "user" en el intent de la actividad.

        }
    }
}
