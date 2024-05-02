package com.das.tresenjuerga.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ConexionAServer;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class ActividadPadre extends AppCompatActivity {





    // TODO: Están básciamente todas las actividades necesarias creadas.
    //       La única que queda por crear es la que enseña la propia partida del tic tac toe.
    //       Aunque puede que venga bien decorarlas un poco más


    // Toda actividad que pueda ser ejecutada hereda de esta clase


    private static ActividadPadre actividadEnEjecucion; // La actividad que el user está visualizando



    public static ActividadPadre getActividadActual() {return ActividadPadre.actividadEnEjecucion;} // Getter de la actividad


    private static int fragmento;
    private int idContenedor;

    private static int cantidadLocks = 0;

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

    @Override
    protected void onStart() {
        super.onStart();
        this.setEstilo(this.obtenerFragmentoOrientacion());

    }


    // Source de estos dos métodos: https://stackoverflow.com/questions/5123407/losing-data-when-rotate-screen

    // Esto es para que se guarde el bundle entre rotaciones
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        Bundle bundle = super.getIntent().getExtras();

        for (String key : bundle.keySet()) {
            savedInstanceState.putString(key, bundle.getString(key));
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        for (String key: savedInstanceState.keySet()) {
            super.getIntent().putExtra(key, savedInstanceState.getString(key));
        }

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

        this.idContenedor = super.getResources().getIdentifier(nombreContenedor, "id", super.getPackageName());
        int idLayout = super.getResources().getIdentifier(nombreLayout, "layout", super.getPackageName());


        // Montar el layout "idLayout" en "idContenedor"

        fragmento = idLayout; // Esta es la única forma de pasarle una variable a la clase
                              // La constructora no se puede tocar y los setters no funcionan
                              // para onCreateView
        super.getSupportFragmentManager().beginTransaction()
                .replace(this.idContenedor, new MiFragmento())
                .commit();




    }

    private void setEstilo(View fragmento) {

        // Pintar los distintos elementos de la UI según el estilo elegido



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;

        if (estilo.contentEquals("1")) {
            // Estilo día

            fragmento.setBackgroundColor(Color.WHITE);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI

                View elemento = viewGroup.getChildAt(i);

                if (elemento instanceof Button) {
                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.GRAY);
                    boton.setTextColor(Color.BLACK);

                } else if (elemento instanceof EditText) {
                    EditText edit = (EditText) elemento;
                    edit.setBackgroundColor(Color.GRAY);
                    edit.setTextColor(Color.BLACK);


                } else if (elemento instanceof TextView) {


                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.rgb(0,0,0));


                }



            }



        } else {
            // Estilo neón

            fragmento.setBackgroundColor(Color.BLACK);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI
                View elemento = viewGroup.getChildAt(i);

                if (elemento instanceof  Button) {
                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.rgb(0, 100, 0));
                    boton.setTextColor(Color.WHITE);

                } else if (elemento instanceof EditText) {
                    EditText edit = (EditText) elemento;
                    edit.setBackgroundColor(Color.rgb(0, 100, 0));
                    edit.setTextColor(Color.WHITE);


                } else if (elemento instanceof TextView) {

                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.WHITE);
                }
            }


        }
    }

    protected static boolean enLandscape() {
        return actividadEnEjecucion.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static void añadirAIntent(String key, String value) {
        actividadEnEjecucion.getIntent().putExtra(key, value);
    }

    protected static void quitarDeIntent(String key) {
        actividadEnEjecucion.getIntent().removeExtra(key);
    }

    public static String obtenerDeIntent(String key) {return actividadEnEjecucion.getIntent().getExtras().getString(key);}

    protected static View obtenerFragmentoOrientacion() {
        return actividadEnEjecucion.getSupportFragmentManager().findFragmentById(actividadEnEjecucion.idContenedor).getView();
    }

    public static void redirigirAActividad(Class ActividadTarget) {

        // Cierra la actividad actual y abre ActividadTarget.
        // Todos los datos del intent de la actividad actual se pasan a la siguiente actividad

        // Solo se permite el cambio de actividad si sePermiteCambiar es true o se redirige a la
        // misma actividad

        System.out.println("EN REDIRECT");

        if (ActividadPadre.comprobarUnlock() || ActividadTarget.isInstance(actividadEnEjecucion)){
            Intent intent = new Intent(ActividadPadre.actividadEnEjecucion, ActividadTarget);

            Bundle bundle = actividadEnEjecucion.getIntent().getExtras();

            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    intent.putExtra(key, bundle.getString(key));
                }
            }



            ActividadPadre.actividadEnEjecucion.finish();
            ActividadPadre.actividadEnEjecucion.startActivity(intent);
        }


    }

    public static void lockRedirectsYPeticionesAServer(boolean flag) {
        if (!flag) {
            ActividadPadre.cantidadLocks--;
        } else {
            ActividadPadre.cantidadLocks++;
        }
    }

    private static boolean comprobarUnlock() {
        return ActividadPadre.cantidadLocks <= 0;

    }

    public static void recargarActividad() {
        ActividadPadre.redirigirAActividad(actividadEnEjecucion.getClass());
    }


    public static void cerrarApp() {

        ActividadPadre.actividadEnEjecucion.finish();
        System.exit(0);
    }

    public static void mostrarToast(int idMsg) {
        Toast.makeText(ActividadPadre.actividadEnEjecucion, idMsg, Toast.LENGTH_SHORT);
    }


    public static void peticionAServidor(String recurso, int idPeticion, String[] parametros, ObservadorDePeticion observador) {


        if (ActividadPadre.comprobarUnlock()) {

            // Lockear los botones durante el thread para que el user no pueda cambiar de actividad

            ActividadPadre.lockRedirectsYPeticionesAServer(true);




            Data datos = new Data.Builder()
                    .putString("recurso", recurso)
                    .putInt("idPeticion", idPeticion)
                    .putStringArray("parametros", parametros)
                    .build();



            ActividadPadre actAct = ActividadPadre.actividadEnEjecucion;
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionAServer.class).setInputData(datos).build();

            if (observador != null) {
                WorkManager.getInstance(actAct).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(actAct, observador);

            } else {
                ActividadPadre.lockRedirectsYPeticionesAServer(false);
            }

            WorkManager.getInstance(actAct).enqueue(otwr);
        }


    }


    public static void pushearTokenABDYLoggear(String user) {

        ActividadPadre.añadirAIntent("user", user);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    String[] datos = {user, token};

                    ActividadPadre.peticionAServidor("usuarios", 2, datos, null);

                    ActividadPadre.lockRedirectsYPeticionesAServer(false);
                    ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

                } else {
                    Exception exception = task.getException();

                }
            }
        });

    }

    public static class MiFragmento extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragmento, container, false);
        }
    }


}

/*
    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDWebService.class).setInputData(datos).build();
                    WorkManager.getInstance(MainActivity.this).getWorkInfoByIdLiveData(otwr.getId())
            .observe(MainActivity.this, new MainActivity.ObservadorDeAutentificacion(user));
                    WorkManager.getInstance(MainActivity.this).enqueue(otwr);


 */