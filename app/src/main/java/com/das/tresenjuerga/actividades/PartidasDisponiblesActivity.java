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

    /*
        Esta interfaz muestra las partidas disponibles para un jugador, en estas se incluyen:

        - En las que son tu turno
        - En las que no son tu turno
        - Las partidas que otra gente ha solicitado contra ti

        Solo puede haber una partida activa entre cada par de jugadores y dicha partida siempre estará
        en uno de esos 3 estados, indicados en la propia instancia de la partida en la lista de partidas disponibles

        Cuando se acaba una partida, se borra de la lista y se puede pedir otra.
        La única otra forma de borrar una partida es quitar al oponente de la lista de amigos

        Las partidas que has solicitado contra alguien pero todavía no se hayan aceptado NO aparecen en esta
        pantalla, pues no tienen interés desde tu POV.



     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_partidas_disponibles);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        ListView listView = super.findViewById(R.id.partidasDisponiblesL_Partidas);

        // Dar listeners a los botones
        fragmento.findViewById(R.id.partidasDisponiblesB_Volver).setOnClickListener(new BotonListener());


        // Pedir al servidor que partidas tienes disponibles
        String[] datos = {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("partidas", 2, datos, new ObservadorDePartidas(listView));

    }

    private class ObservadorDePartidas extends ObservadorDePeticion {

        private ListView listView;

        public ObservadorDePartidas (ListView listView) {this.listView = listView;}
        @Override
        protected void ejecutarTrasPeticion() {

            // El servidor responde con la lista de partidas (oponentes + estados de partida)

            // Obtener orientación cardview

            int cardview;
            if (ActividadPadre.enLandscape()) {
                cardview = R.layout.cardview_partida_landscape;
            } else {
                cardview = R.layout.cardview_partida_portrait;
            }


            // Recoger la info de la BD, la BD da dos listas independientes, una con los nombres y otra con los estados.
            // Este cliente junta las dos listas en una matriz.
            Object[] listaValores = this.juntarArray(super.getStringArray("oponentes"), super.getLongArray("estados"));

            // Montar el listview
            ListaAdapterPartidasDisponibles adapter = new ListaAdapterPartidasDisponibles(listaValores, cardview);
            adapter.notifyDataSetChanged();
            this.listView.setAdapter(adapter);


        }
        private Object[][] juntarArray(String[] array1, long[] array2) {
            // Proceso:  [[a,b,c] , [d,e,f]]  -> [[a,d],[b,e],[c,f]]

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

            // El único botón va una pantalla atrás, a la de usuario loggeado
            ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);



        }
    }
}