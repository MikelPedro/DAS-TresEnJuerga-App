package com.das.tresenjuerga.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.das.tresenjuerga.R;

public class RegistroActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_registro);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View fragmento = super.obtenerFragmentoOrientacion();
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
                    String user = ((EditText)RegistroActivity.super.findViewById(R.id.registroE_Nombre)).getText().toString();
                    String pass = ((EditText)RegistroActivity.super.findViewById(R.id.registroE_Nombre)).getText().toString();

                    if (user.length() < 51 && pass.length() < 51 && user.length() > 0 && pass.length() > 0) {
                        // TODO: Handlear dada de alta.

                    } else {
                        // TODO: Mostrar msg de campos entre 1-50 chars
                    }


                    break;
                case 1:
                    // Volver al menú principal
                    RegistroActivity.super.redirigirAActividad(MainActivity.class);


            }
        }
    }
}