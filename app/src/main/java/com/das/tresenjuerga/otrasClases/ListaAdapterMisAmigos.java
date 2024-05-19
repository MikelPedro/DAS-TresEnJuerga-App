package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.MainActivity;
import com.das.tresenjuerga.actividades.PerfilActivity;

public class ListaAdapterMisAmigos extends ListaAdapterBase{

    // El ArrayAdapter que crea la lista de los amigos de un usuario


    private String amigo;
    public ListaAdapterMisAmigos(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Llenar los campos de contra quien juega y a quien le toca
        this.amigo = super.getString(pos, 0);
        ((TextView)view.findViewById(R.id.instanciaAmigoT_Nombre)).setText(this.amigo);


        // Dar los listeners correspondientes a los botones
        view.findViewById(R.id.instanciaAmigoB_VerInfo).setOnClickListener(new BotonListener(0, this.amigo));
        view.findViewById(R.id.instanciaAmigoB_Retar).setOnClickListener(new BotonListener(1, this.amigo));
        view.findViewById(R.id.instanciaAmigoB_Eliminar).setOnClickListener(new BotonListener(2, this.amigo));



        System.out.println(this.amigo);

        return view;
    }



    private class BotonListener implements View.OnClickListener {


        private int id;
        private String amigo;
        public BotonListener(int id, String amigo) {
            this.id = id; this.amigo = amigo;
        }

        @Override
        public void onClick(View v) {

            String[] datos = {ActividadPadre.obtenerDeIntent("user"), this.amigo};
            System.out.println(this.amigo);

            switch (this.id) {
                case 0:
                    // Ver perfil
                    ActividadPadre.añadirAIntent("userAVisualizar", this.amigo);
                    ActividadPadre.redirigirAActividad(PerfilActivity.class);
                    break;
                case 1:
                    // Retar, pedir a servidor que mande la petición
                    ActividadPadre.peticionAServidor("partidas", 0, datos, new ObservadorDeRetarPartida());
                    break;
                case 2:
                    // Borrar amigo, pedir a servidor que borre al usuario de su lista de amigos y su partida relacionada con él
                    ActividadPadre.peticionAServidor("amistades", 4, datos, new ObservadorDeBorrarAmigo());

                    String[] data = {ActividadPadre.obtenerDeIntent("user"), this.amigo, "0"};
                    ActividadPadre.peticionAServidor("partidas", 5, data, null);



            }

        }
        private class ObservadorDeBorrarAmigo extends ObservadorDePeticion {

            @Override
            protected void ejecutarTrasPeticion() {
                // Tras borrar al amigo, forzar al otro usuario fuera de la partida que tenían si la estaba mirando
                String[] data = {ActividadPadre.obtenerDeIntent("user"), ListaAdapterMisAmigos.this.amigo};
                ActividadPadre.peticionAServidor("firebase",2, data, null);

                // Tras esto, recargar la actividad para quitar al usuario de la interfaz
                ActividadPadre.recargarActividad();
            }
        }

        private class ObservadorDeRetarPartida extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                // Tras recibir la petición del reto, ver si fue posible o no y mostrar por toast

                if (super.getBoolean("respuesta")) {
                    ActividadPadre.mostrarToast(R.string.retarCorrecto);

                } else {
                    ActividadPadre.mostrarToast(R.string.retarError);

                }
            }
        }
    }
}
