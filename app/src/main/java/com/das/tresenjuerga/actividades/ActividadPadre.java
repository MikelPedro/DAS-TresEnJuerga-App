package com.das.tresenjuerga.actividades;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.das.tresenjuerga.R;

public class ActividadPadre extends AppCompatActivity {

    // Toda actividad que pueda ser ejecutada hereda de esta clase


    private static ActividadPadre actividadEnEjecucion; // La actividad que el user está visualizando




    public static ActividadPadre getActividadActual() {return ActividadPadre.actividadEnEjecucion;} // Getter de la actividad


    private static int fragmento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Al hacer onCreate se sobrescribe la actividad anterior en ejecución con esta que se está creando
        ActividadPadre.actividadEnEjecucion = this;
    }
    @Override
    public void setContentView(int layout) {
        super.setContentView(layout);
        // Al hacer setContentView se carga el layout de fragment correspondiente automáticamente
        this.setLayout();

    }



    protected void setLayout() {

        /*
            Post: Pone a su contenedor correspondiente el layout fragment adecuado según
                  la rotación del dispositivo automáticamente.

                  Para esto, asume que el nombre de la actividad linka al resto de recursos
                  necesarios, usando la siguiente lógica:

                  - El nombre del contenedor es el nombre de la actividad en formato
                    dash-case omitiendo la palabra "Activity"

                  - El nombre del fragmento es el nombre de la actividad en formato
                    dash-case, incluyendo "portrait" o "landscape" al final.

                  Para que esto funcione dichos ficheros layout deben existir y el layout
                  base de la actividad debe tener el contenedor respectivo.

                  Ejemplos:

                  Actividad:  UnoDosTresActivity
                  Contenedor: contenedor_uno_dos_tres
                  Fragment:   activity_uno_dos_tres_portrait / activity_uno_dos_tres_landscape

                  Actividad:  UnoActivity
                  Contenedor: contenedor_uno
                  Fragment:   activity_uno_portrait / activity_uno_landscape



         Source mapeo name a id -> https://stackoverflow.com/questions/64158273/what-does-getresources-getidentifier-do-in-android

         Source getClassName(): https://stackoverflow.com/questions/6271417/java-get-the-current-class-name


         */

        // Obtener el nombre de la clase
        String nombreClase = this.getClass().getSimpleName();

        // Crear string builder (solo por razones de mejor rendimiento, funciona
        // también concatenar strings a secas)
        StringBuilder constructorNombreActividad = new StringBuilder();


        // Settear la lectura del nombre de la clase a que justo omita la palabra
        // "Activity" del final
        int charTarget = nombreClase.length() - 8; // No iteramos por la palabra "Activity"


        // Iterar por caracteres que no forme la palabra "Activity" del final
        for (int idx = 0; idx < charTarget; idx++ ) {

            // Obtener caracter actual
            char c = nombreClase.charAt(idx);


            if (Character.isUpperCase(c)) {
                // Si es mayuscula la letra, poner "_" delante de ella y pasarla a minus
                constructorNombreActividad.append("_");
                c = Character.toLowerCase(c);

            }

            // Concatenar dicho caracter al final de la string que se está construyendo
            constructorNombreActividad.append(c);
        }

        // Calcular el nombre del contenedor y fragment usando la lógica explicada arriba.

        String nombreActividad = constructorNombreActividad.toString();
        String nombreContenedor = "contenedor" + nombreActividad;
        String nombreLayout = "";

        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            nombreLayout = "activity" + nombreActividad + "_landscape";
        } else {
            nombreLayout = "activity" + nombreActividad + "_portrait";
        }


        // Obtener los ids en int de los recursos respectivos para poder usarlos

        int idContenedor = super.getResources().getIdentifier(nombreContenedor, "id", super.getPackageName());
        int idLayout = super.getResources().getIdentifier(nombreLayout, "layout", super.getPackageName());


        // Montar el layout "idLayout" en "idContenedor"

        fragmento = idLayout; // Esta es la única forma de pasarle una variable a la clase
                              // La constructora no se puede tocar y los setters no funcionan
                              // para onCreateView
        super.getSupportFragmentManager().beginTransaction()
                .replace(idContenedor, new MiFragmento())
                .commit();


    }

    public static class MiFragmento extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragmento, container, false);
        }
    }


}
