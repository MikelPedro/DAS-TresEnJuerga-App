package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class InicioSesionActivity extends ActividadPadre {

    /*
        Esta interfaz muestra la pantalla de login.
        Tiene campos para username y password así como un botón para enviar el formulario


     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_inicio_sesion);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Recoger el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listener a los botones
        fragmento.findViewById(R.id.inicioSesionB_Confirmar).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.inicioSesionB_Salir).setOnClickListener(new BotonListener(1));


    }


    private class BotonListener implements View.OnClickListener {


        private int id;
        public BotonListener(int id) {
            this.id = id;
        }
        @Override
        public void onClick(View v) {
            switch (this.id) {
                case 0:
                    // Este botón envia el formulario con nuestros datos. El servidor nos respnderá si la autentificación
                    // es correcta
                    String user = ((EditText)InicioSesionActivity.super.findViewById(R.id.inicioSesionE_Nombre)).getText().toString();
                    String pass = ((EditText)InicioSesionActivity.super.findViewById(R.id.inicioSesionE_Contrasena)).getText().toString();

                    String[] datos = {user, pass};
                    ActividadPadre.peticionAServidor("usuarios", 1, datos, new ObservadorDeAutentificacion(user));


                    break;
                case 1:
                    // Este botón va una interfaz atrás, a Main Activity (menú principal)
                    ActividadPadre.redirigirAActividad(MainActivity.class);


            }
        }

        private class ObservadorDeAutentificacion extends ObservadorDePeticion {

            private String user;

            public ObservadorDeAutentificacion(String user) {this.user = user;}

            @Override
            protected void ejecutarTrasPeticion() {

                // El servidor nos responde de manera booleana si la autentificación es correcta

                if (super.getBoolean("respuesta")) {
                    // Si lo es, vincular la cuenta al token del móvil y pasar a la pantalla de loggeado
                    ActividadPadre.pushearTokenABDYLoggear(user);

                } else {
                    // Este otro caso es error en autentificación
                    ActividadPadre.mostrarToast(R.string.errorDeAutentificacion);
                }
            }
        }
    }
}