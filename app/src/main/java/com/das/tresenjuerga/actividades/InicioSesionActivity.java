package com.das.tresenjuerga.actividades;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.das.tresenjuerga.R;

public class InicioSesionActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_inicio_sesion);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = super.obtenerFragmentoOrientacion();
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
                    // TODO: Handlear comprobar credenciales
                    String user = ((EditText)InicioSesionActivity.super.findViewById(R.id.inicioSesionE_Nombre)).getText().toString();
                    String pass = ((EditText)InicioSesionActivity.super.findViewById(R.id.inicioSesionE_Contrasena)).getText().toString();

                    // Por ahora, admitir al user que pase.

                    InicioSesionActivity.super.añadirAIntent("user", user);

                    break;
                case 1:
                    // Volver al menú principal
                    InicioSesionActivity.super.redirigirAActividad(MainActivity.class);


            }
        }
    }
}