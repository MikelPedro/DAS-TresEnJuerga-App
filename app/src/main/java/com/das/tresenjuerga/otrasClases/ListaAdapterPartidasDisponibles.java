package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.JugarActivity;
import com.das.tresenjuerga.actividades.MainActivity;

public class ListaAdapterPartidasDisponibles extends ListaAdapterBase {

    private boolean tuTurno;
    private String oponente;

    public ListaAdapterPartidasDisponibles(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        long idEstado = super.getLong(pos, 1);
        boolean solicitud = idEstado == 0;
        this.tuTurno = idEstado == 2;
        this.oponente = super.getString(pos, 0);


        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Llenar los campos de contra quien juega y a quien le toca
        ((TextView)view.findViewById(R.id.instanciaPartidaT_Oponente)).setText(this.oponente);

        String textoTurno = "";

        if (idEstado == 0) {
            textoTurno = ActividadPadre.getActividadActual().getString(R.string.peticion);
        } else if (idEstado == 1) {
            textoTurno = ActividadPadre.getActividadActual().getString(R.string.turnoDelOtro);

        } else {
            textoTurno = ActividadPadre.getActividadActual().getString(R.string.tuTurno);

        }




        ((TextView)view.findViewById(R.id.instanciaPartidaT_Turno)).setText(textoTurno);

        // Dar los listeners correspondientes a los botones

        Button botonDer = (Button) view.findViewById(R.id.instanciaPartidaB_Entrar);

        if (solicitud) {
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setOnClickListener(new BotonListener(pos,0));
            botonDer.setOnClickListener(new BotonListener(pos, 1));
            botonDer.setText(R.string.aceptar);

        } else {
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setVisibility(View.GONE);
            botonDer.setOnClickListener(new BotonListener(pos, 2));
            botonDer.setText(R.string.entrarAPartida);

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



            switch (this.id) {
                case 0:
                    // Rechazar match
                    String[] datosRechazar = {ActividadPadre.obtenerDeIntent("user"), ListaAdapterPartidasDisponibles.this.oponente, "0"};
                    ActividadPadre.peticionAServidor("partidas", 5, datosRechazar, new ObservadorDeProcesarPeticion());

                    break;

                case 1:
                    String[] datosAceptar = {ActividadPadre.obtenerDeIntent("user"), ListaAdapterPartidasDisponibles.this.oponente};
                    // Aceptar match
                    ActividadPadre.peticionAServidor("partidas", 1, datosAceptar, new ObservadorDeProcesarPeticion());

                    break;

                case 2:

                    ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(ListaAdapterPartidasDisponibles.this.tuTurno));
                    ActividadPadre.añadirAIntent("oponente", ListaAdapterPartidasDisponibles.this.oponente);
                    ActividadPadre.redirigirAActividad(JugarActivity.class);


            }



        }

        private class ObservadorDeProcesarPeticion extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {
                ActividadPadre.recargarActividad();



            }
        }

    }
}
