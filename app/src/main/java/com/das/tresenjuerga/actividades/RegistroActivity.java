package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;



import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

public class RegistroActivity extends ActividadPadre {


    /*
        Esta interfaz tiene el registro para registrar un nuevo usuario.

        Por cada usuario nuevo se le pide el nombre y contraseña

        Las contraseñas se pueden repetir con otros usuarios, pero no los nombres.
        Ambos campos deben tener entre 1 y 50 chars.


     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_registro);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Recoger el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        fragmento.findViewById(R.id.registroB_Confirmar).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.registroB_Salir).setOnClickListener(new BotonListener(1));


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

                    // Este botón manda la petición de registro
                    // Para ello, recoge los campos correspondientes y los manda a BD
                    String user = ((EditText) RegistroActivity.super.findViewById(R.id.registroE_Nombre)).getText().toString();
                    String pass = ((EditText) RegistroActivity.super.findViewById(R.id.registroE_Contrasena)).getText().toString();

                    if (user.length() < 51 && pass.length() < 51 && user.length() > 0 && pass.length() > 0) {
                        // Comprobado que user y pass entre 1 y 50 chars, pedir al servidor que se registre
                        String[] datos = {user, pass};
                        ActividadPadre.peticionAServidor("usuarios", 0, datos, new ObservadorDeRegistro());


                    } else {
                        // Mostrar toast de error de tamaño de campos
                        ActividadPadre.mostrarToast(R.string.errorFormatoRegistro);
                    }


                    break;
                case 1:
                    // El otro botón manda una pantalla atrás, a Main Activity
                    ActividadPadre.redirigirAActividad(MainActivity.class);


            }
        }
    }
        private class ObservadorDeRegistro extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {
                // El servidor nos responde con si el registro fue correcto

                if (super.getBoolean("respuesta")) {
                    // Registro correcto, redirigir a Main Activity
                    ActividadPadre.redirigirAActividad(MainActivity.class);

                } else {

                    // Error por duplicado
                    ActividadPadre.mostrarToast(R.string.errorNombreCogido);

                }


            }
        }
}

