package com.das.tresenjuerga.actividades;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.DialogoSalir;

public class MainActivity extends ActividadPadre {

    /*
        Esta es la pantalla principal de la app, tiene el botón que redirige al formulario de sign up y otro
        para login


     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!=
                    PackageManager.PERMISSION_GRANTED) {

                //Pedir el permiso para las notificaciones
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        fragmento.findViewById(R.id.mainB_InicioSesion).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.mainB_Registrar).setOnClickListener(new BotonListener(1));
        fragmento.findViewById(R.id.mainB_Salir).setOnClickListener(new BotonListener(2));
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
                    // Este botón va al menú de inicio de sesión
                    ActividadPadre.redirigirAActividad(InicioSesionActivity.class);
                    break;
                case 1:
                    // Este botón va al menú de registro
                    ActividadPadre.redirigirAActividad(RegistroActivity.class);
                    break;
                case 2:
                    // Este botón sale de la app, para ello muestra un dialogo de confirmación de salida
                    DialogoSalir alertaSalir = new DialogoSalir();
                    alertaSalir.show(ActividadPadre.getActividadActual().getSupportFragmentManager(), "etiqueta");
            }
        }
    }
}