package com.das.tresenjuerga.actividades;


import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

import java.util.ArrayList;
import java.util.HashMap;
public class AnadirAmigoActivity extends ActividadPadre {


    /*
        Esta actividad permite introducir el nombre de alguien para poder mandarle una solicitud de amistad.


        Según se escribe en el EditText, se enseña la gente disponible que encaja con lo tecleado

        La solicitud de amistad falla si:
          - La persona no existe
          - La persona ya es amigo
          - La persona tiene una solicitud tuya pendiente
          - La persona te ha mandado una solicitud pendiente
          - La persona eres tu mismo


        Cada caso tiene su toast de error personalizado, así como el mensaje de success



     */

    private Arbol arbolNombres;
    private ListView laListaDeDisponibles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_anadir_amigo);

    }



    // Métodos que actualizan los listview para poner solo los posibles en la lista
    private void actualizarListaPosiblesVisualmente(ArrayList<String> posibles) {

        ArrayAdapter<String> eladaptador =
                new ArrayAdapter<String>(AnadirAmigoActivity.this, android.R.layout.simple_list_item_1, posibles);
        this.laListaDeDisponibles.setAdapter(eladaptador);

    }

    private void actualizarListaPosiblesVisualmente(String[] posibles) {

        ArrayAdapter eladaptador =
                new ArrayAdapter<String>(AnadirAmigoActivity.this, android.R.layout.simple_list_item_1, posibles);
        ListView lalista = (ListView) findViewById(R.id.anadirAmigoL_Disponibles);
        this.laListaDeDisponibles.setAdapter(eladaptador);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Obtener el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listener a los botones
        fragmento.findViewById(R.id.añadirAmigoB_Añadir).setOnClickListener(new BotonListener(0));
        fragmento.findViewById(R.id.añadirAmigoB_Volver).setOnClickListener(new BotonListener(1));


        // Encontrar el listview y darle un fondo blanco (para que se vea en cualquier estilo)

        this.laListaDeDisponibles = (ListView) findViewById(R.id.anadirAmigoL_Disponibles);
        this.laListaDeDisponibles.setBackgroundColor(Color.WHITE);

        // Pedir a server la gente disponible que tenemos para añadir para mostrarlos en el listview despues

        String[] datos = {ActividadPadre.obtenerDeIntent("user")};
        ActividadPadre.peticionAServidor("amistades", 0, datos, new ObservadorDeAmigosFactibles());


        // Buscar el editText y darle el listener para ir actualizando lo que se va tecleando

        EditText et = fragmento.findViewById(R.id.añadirAmigoE_User);
        et.addTextChangedListener(new ObservadorDeTexto());



    }




    private class ObservadorDeTexto implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // NOP
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            // Se ha escrito algo nuevo, buscar en el árbol de gente disponible quienes
            // matchean con la string introducida hasta ahora

            ArrayList<String> disponibles = null;
            if (AnadirAmigoActivity.this.arbolNombres != null) {
                disponibles = AnadirAmigoActivity.this.arbolNombres.buscarPalabras(charSequence.toString());



            } else {

                // Fallback: Si no se ha recibido la respuesta del servidor todavía con los nombres
                // el arbol no existe, por ahora no mostrar ningún nombre en la lista
                disponibles = new ArrayList<>();
            }


            AnadirAmigoActivity.this.actualizarListaPosiblesVisualmente(disponibles);
        }

        @Override
        public void afterTextChanged(Editable s) {
            // NOP
        }
    }
    private class ObservadorDeAmigosFactibles extends ObservadorDePeticion {

        @Override
        protected void ejecutarTrasPeticion() {

            // Tras recibir la respuesta del servidor con los nombres, darselos al árbol para
            // que los ordene de manera que se puedan filtrar más fácilmente después
            // y proceder a mostrar todos esos nombres en la lista de sugeridos


            AnadirAmigoActivity.this.arbolNombres = new Arbol(super.getStringArray("nombres"));

            AnadirAmigoActivity.this.actualizarListaPosiblesVisualmente(super.getStringArray("nombres"));

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


                    // Se mandó la solicitud, refrescar la actividad para quitarlo de la lista de sugeridos
                    ActividadPadre.recargarActividad();

                } else {
                    // Si status code != 0, algun tipo de error, los mensajes de error están listados en orden en strings.xml por lo que se accede al mensaje correspondiente en forma de array
                    ActividadPadre.mostrarToast(ActividadPadre.getActividadActual().getResources().getIdentifier("errorAmigo"+respuesta, "string", ActividadPadre.getActividadActual().getPackageName()));

                }
            }
        }
    }

    private class Arbol {

        // Estructura de arbol para navegar los nombres, solo está pensado para añadir elementos
        // al arbol y buscarlos, no quitarlos

        private Nodo root;


        public Arbol(String[] palabras) {

            // Inicializar el árbol con las palabras del array como válidas

            this.root = new Nodo("");

            for (String palabra : palabras) {
                this.añadirString(palabra);
            }

        }




        private void añadirString(String palabra) {

            // Añadir la palabra al árbol

            this.root.añadirString(palabra, 0);
        }

        public ArrayList<String> buscarPalabras(String substring) {

            // Buscar todas las palabras que empiezan por substring y devolverlas


            // Primero, encontrar el nodo que encaja con substring, para buscar a partir de ahí
            // y no iterar el arbol entero

            Nodo inicio = this.root.irA(substring, 0);

            ArrayList<String> palabrasValidas = new ArrayList<>();

            if (inicio != null) {
                // Si dicho nodo existe, buscar en todos sus hijos las palabras que existen
                inicio.buscarTodasLasPalabrasHijas(palabrasValidas);
            }



            return palabrasValidas;
        }

        private class Nodo {

            // Cada nodo del árbol, almacena el valor de la string hasta el momento,
            // así como los pointers a sus sucesores y la letra extra que añaden a la string

            public Nodo(String valor) {
                this.valor = valor;
                this.hijos = new ArrayList<>();
                this.nextLetra = new HashMap<>();
                this.esPalabra = false;
            }

            private ArrayList<Nodo> hijos;
            private HashMap<Character, Integer> nextLetra;
            private boolean esPalabra;
            private String valor;


            private void añadirString(String palabra, int posChar) {


                // Añadir la palabra al árbol

                if (posChar == palabra.length()) {
                    // Si el nodo encaja con el valor de la palabra, marcarlo como palabra
                    this.esPalabra = true;

                } else {

                    // Si no lo es, buscar la ruta a la siguiente letra
                    char letra = palabra.charAt(posChar);

                    if (!this.nextLetra.containsKey(letra)) {
                        // Si dicha ruta no existe, crear el nodo para crear dicha ruta
                        this.nextLetra.put(letra, this.hijos.size());
                        this.hijos.add(new Nodo(palabra.substring(0, posChar+1)));
                    }

                    // Viajar al nodo de la siguiente letra
                    this.hijos.get(this.nextLetra.get(letra)).añadirString(palabra, posChar+1);

                }


            }


            private Nodo irA(String target, int posChar) {

                // Buscar el nodo que contiene la string "target"

                if (posChar == target.length()) {
                    // Si es este nodo, devolverlo
                    return this;
                } else if (this.nextLetra.containsKey(target.charAt(posChar))) {
                    // Si no lo es pero contiene pointer a la siguiente letra, pedir al siguiente nodo recursivamente la respuesta
                    return this.hijos.get(this.nextLetra.get(target.charAt(posChar))).irA(target, posChar + 1);
                } else {
                    // Si no hay letra, no hay ruta a la palabra, return null
                    return null;
                }


            }


            private void buscarTodasLasPalabrasHijas(ArrayList<String> palabras) {

                // Almacenar en el array todas aquellas palabras que existen en el árbol a partir
                // de este nodo a sus hijos

                if (this.esPalabra) {
                    // Si este nodo contiene una palabra válida, añadir su palabra al árbol
                    palabras.add(this.valor);
                }

                for (Nodo hijo : this.hijos) {
                    // Pedir a todos sus hijos que busquen palabras recursivamente
                    hijo.buscarTodasLasPalabrasHijas(palabras);
                }

            }


        }




    }
}