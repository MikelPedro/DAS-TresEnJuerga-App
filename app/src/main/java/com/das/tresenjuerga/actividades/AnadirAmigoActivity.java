package com.das.tresenjuerga.actividades;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

import java.util.Arrays;
import java.util.List;

public class AnadirAmigoActivity extends ActividadPadre {


    /*
        Esta actividad permite introducir el nombre de alguien para poder mandarle una solicitud de amistad.

        La solicitud de amistad falla si:
          - La persona no existe
          - La persona ya es amigo
          - La persona tiene una solicitud tuya pendiente
          - La persona te ha mandado una solicitud pendiente
          - La persona eres tu mismo

        Cada caso tiene su toast de error personalizado, así como el mensaje de success



     */


    // TODO: Obtener la lista de gente que pueden ser amigos. (lo de lo ultimo de la teoria) [Mikel]

    /*
        Para obtener la lista de amigos posibles, usar el siguiente codigo:


            private void tuMetodo() {
                String[] datos = {ActividadPadre.obtenerDeIntent("user")};
                ActividadPadre.peticionAServidor("amistades", 0, datos, new ObservadorDeAmigosFactibles());
            }

            private class ObservadorDeAmigosFactibles extends ObservadorDePeticion {

                @Override
                protected void ejecutarTrasPeticion() {
                    String[] genteQueNoSonAmigosTuyos = super.getStringArray("nombres");
                    // Hacer con esa lista de string lo que haga falta
                }
            }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_anadir_amigo);

    }



    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        EditText et = fragmento.findViewById(R.id.añadirAmigoE_User);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("App", "Se va a cambiar: "+s);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("App", "Nuevo texto: "+charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String[] datos = {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("amistades", 0, datos, new ObservadorDeAmigosFactibles());
        Log.d("QUERY", datos[0]);

        // Dar listener a los botones
        fragmento.findViewById(R.id.añadirAmigoB_Añadir).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.añadirAmigoB_Volver).setOnClickListener(new BotonListener(1));
    }

    private class ObservadorDeAmigosFactibles extends ObservadorDePeticion{

        @Override
        protected void ejecutarTrasPeticion() {
            String[] noSonAmigos = super.getStringArray("nombres");
            List<String> l = Arrays.asList(noSonAmigos);
            Observable<String> o = Observable.fromIterable(l);
            // Hacer aquí lo que haga falta con la lista (rxjava)
        }
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
                    // Este botón envia a server la petición de amistad basado en lo que hemos escrito en el EditText.
                    // El server nos responderá con un status code para ver que ha ocurrido
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ((EditText)AnadirAmigoActivity.super.findViewById(R.id.añadirAmigoE_User)).getText().toString()};
                    ActividadPadre.peticionAServidor("amistades", 1, datos, new ObservadorDeAñadirAmigo());
                    break;
                case 1:
                    // Este botón va una interfaz atrás, a la de la lista de amigos
                    ActividadPadre.redirigirAActividad(AmigosActivity.class);

            }
        }

        private class ObservadorDeAñadirAmigo extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                // El servidor nos responde

                long respuesta = super.getLong("respuesta");

                if (respuesta == 0) {
                    // Status code 0 == Success!
                    ActividadPadre.mostrarToast(R.string.solicitudAñadida);

                } else {
                    // Si status code != 0, algun tipo de error, los mensajes de error están listados en orden en strings.xml por lo que se accede al mensaje correspondiente en forma de array
                    ActividadPadre.mostrarToast(ActividadPadre.getActividadActual().getResources().getIdentifier("errorAmigo"+respuesta, "string", ActividadPadre.getActividadActual().getPackageName()));

                }
            }
        }
    }

}