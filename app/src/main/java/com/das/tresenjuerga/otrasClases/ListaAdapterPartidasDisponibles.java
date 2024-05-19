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

    // El ArrayAdapter que muestra la lista de partidas de un usuario

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

        // Llenar los campos de contra quien juega y a quien le toca o si es una peticion
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
            // si es una petición, el botón de la izquierda rechaza la petición
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setOnClickListener(new BotonListener(0, this.oponente, this.tuTurno));
            // si es una peticion, el botón de la derecha hace la acción de aceptar
            botonDer.setOnClickListener(new BotonListener(1, this.oponente, this.tuTurno));
            botonDer.setText(R.string.aceptar);

        } else {
            // si es una partida en curso, no necesitamos el botón de la izquierda
            view.findViewById(R.id.instanciaPartidaB_Rechazar).setVisibility(View.GONE);
            // si es una partida en curso, el botón de la derecha hace la acción de ver la partida
            botonDer.setOnClickListener(new BotonListener( 2, this.oponente, this.tuTurno));
            botonDer.setText(R.string.entrarAPartida);

        }







        return view;
    }

    private class BotonListener implements View.OnClickListener {


        private String oponente;
        private int id;

        private boolean tuTurno;
        public BotonListener(int id, String oponente, boolean tuTurno) {
            this.id = id;
            this.oponente = oponente;
            this.tuTurno = tuTurno;
        }

        @Override
        public void onClick(View v) {



            switch (this.id) {
                case 0:
                    // Rechazar match, pedir a servidor esto
                    String[] datosRechazar = {ActividadPadre.obtenerDeIntent("user"), this.oponente, "0"};
                    ActividadPadre.peticionAServidor("partidas", 5, datosRechazar, new ObservadorDeProcesarPeticion());

                    break;

                case 1:
                    // Aceptar match, pedir a servidor esto
                    String[] datosAceptar = {ActividadPadre.obtenerDeIntent("user"), this.oponente};
                    ActividadPadre.peticionAServidor("partidas", 1, datosAceptar, new ObservadorDeProcesarPeticion());

                    break;

                case 2:

                    // Ver match (redirigir a la interfaz de Jugar9
                    ActividadPadre.añadirAIntent("tuTurno", Boolean.toString(this.tuTurno));
                    ActividadPadre.añadirAIntent("oponente", this.oponente);
                    ActividadPadre.redirigirAActividad(JugarActivity.class);


            }



        }

        private class ObservadorDeProcesarPeticion extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {
                // Tras procesar el acepto o rechazo de petición, recargar la interfaz para mostrar los cambios
                ActividadPadre.recargarActividad();



            }
        }

    }
}
