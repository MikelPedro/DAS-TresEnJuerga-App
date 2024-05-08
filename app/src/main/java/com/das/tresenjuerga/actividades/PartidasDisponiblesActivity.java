package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterPartidasDisponibles;
import com.das.tresenjuerga.otrasClases.ListaAdapterSolicitudes;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

import org.checkerframework.checker.units.qual.A;

public class PartidasDisponiblesActivity extends ActividadPadre {

    // NOTA: EN ESTA INTERFAZ EN LA QUE SE MUESTRAN LAS PARTIDAS DEL JUGADOR EN CURSO TAMBIÉN
    // SE MUESTRAN LAS SOLICITUDES DE JUGAR UNA PARTIDA. PERO SE MUESTRAN AL FONDO DE LA LISTA
    // CON UN ESTADO DISTINTO. SI QUERÉIS SE PUEDE MOVER A OTRA INTERFAZ PERO POR AHORA
    // ESTÁ PROGRAMADO ASÍ PARA TENER ALGO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_partidas_disponibles);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.partidasDisponiblesL_Partidas);
        fragmento.findViewById(R.id.partidasDisponiblesB_Volver).setOnClickListener(new BotonListener());

        String[] datos = {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("partidas", 2, datos, new ObservadorDePartidas(listView));

    }

    private class ObservadorDePartidas extends ObservadorDePeticion {

        private ListView listView;

        public ObservadorDePartidas (ListView listView) {this.listView = listView;}
        @Override
        protected void ejecutarTrasPeticion() {

            // Obtener orientación cardview

            int cardview;
            if (ActividadPadre.enLandscape()) {
                cardview = R.layout.cardview_partida_landscape;
            } else {
                cardview = R.layout.cardview_partida_portrait;
            }

            Object[] listaValores = this.juntarArray(super.getStringArray("oponentes"), super.getLongArray("estados"));
            // Montar el listview
            ListaAdapterPartidasDisponibles adapter = new ListaAdapterPartidasDisponibles(listaValores, cardview);
            adapter.notifyDataSetChanged();
            this.listView.setAdapter(adapter);


        }
        private Object[][] juntarArray(String[] array1, long[] array2) {
            // Proceso:  [[a, b, c] , [d,e,f]]  -> [[a,d],[b,e],[c,f]]


            Object[][] resultado = new Object[array1.length][2];

            for (int i = 0; i != array1.length; i++) {
                resultado[i][0] = array1[i];
                resultado[i][1] = array2[i];
            }

            return resultado;
        }
    }




    private class BotonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // Volver a menú de registrado
            ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);



        }
    }
}