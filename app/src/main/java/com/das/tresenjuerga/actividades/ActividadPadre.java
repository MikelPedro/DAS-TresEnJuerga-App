package com.das.tresenjuerga.actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ConexionAServer;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Locale;

public abstract class ActividadPadre extends AppCompatActivity {



    // Toda actividad que pueda ser ejecutada hereda de esta clase

    // Esta clase es abstracta, es decir, no existe una instancia de esta clase en sí porque no representa
    // ninguna pantalla del juego, solo sirve para agrupar funcionalidades comunes de todas las pantallas
    // de la app.



    // TODO: Decorar actividades, están todas creadas ya. Si quereis mover alguna de ellas a la toolbar
    // TODO: Debuggear screen de rematch y juego en vivo.
    // TODO: Reworkear locks (bloquear el boton de la interfaz en sí en vez de la redirección). Seguir bloqueando de manera de que solo se haga una petición al servidor a la vez (cambiar para poner listeners donde hayan dos peticione simultaneas)



    private static ActividadPadre actividadEnEjecucion; // La actividad que el user está visualizando

    private static final HashSet<String> ACTIVIDADES_SIN_TOOLBAR = new HashSet<String>(); //  Actividades sin toolbar


    public static ActividadPadre getActividadActual() {return ActividadPadre.actividadEnEjecucion;} // Getter de la actividad


    private static int fragmento; // el layout que se va a cargar
    private int idContenedor; // el contenedor en el que se va a cargar el layout

    private static int cantidadLocks = 0;

    static {
        // Las actividades que no implementan toolbar se añaden aquí para inicializar el HS
        ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.add("JugarActivity");
        ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.add("PantallaFinActivity");
        ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.add("PreferenciasActivity");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Al hacer onCreate se sobrescribe el puntero de la actividad anterior en ejecución con esta que se está creando
        ActividadPadre.actividadEnEjecucion = this;

        // Cargar el idioma correcto
        this.setIdioma();

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

        // Comprobar si se debe crear una toolbar
        if (!ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.contains(!ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.contains(this.getClass().getSimpleName()))) {
            // Settear toolbar si no es una Activity blacklisted
            super.setSupportActionBar(super.findViewById(R.id.toolbar));
        }

        // Dar el estilo al fragmento
        this.setEstilo(this.obtenerFragmentoOrientacion());

    }


    // Source de estos dos métodos: https://stackoverflow.com/questions/5123407/losing-data-when-rotate-screen

    // Esto es para que se guarde el bundle entre rotaciones
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Guardar el bundle para luego recuperarlo tras la rotación del movil

        Bundle bundle = super.getIntent().getExtras();

        for (String key : bundle.keySet()) {
            savedInstanceState.putString(key, bundle.getString(key));
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Recuperar el bundle tras la rotación del móvil

        super.onRestoreInstanceState(savedInstanceState);

        for (String key: savedInstanceState.keySet()) {
            super.getIntent().putExtra(key, savedInstanceState.getString(key));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Comprobar si la actividad debe tener una toolbar

        boolean toolbarPermitida = !ActividadPadre.ACTIVIDADES_SIN_TOOLBAR.contains(this.getClass().getSimpleName());

        if (toolbarPermitida) {
            // Si debe tenerla, inflarla al layout
            super.getMenuInflater().inflate(R.menu.toolbar,menu);

        }
        return toolbarPermitida;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Guardar que clase llamó para poder redirigir de vuelta ahí
        ActividadPadre.añadirAIntent("actividadQueLlama", this.getClass().getSimpleName());

        /*

        // Para elegir entre varias opciones en la toolBAR:

        int id=item.getItemId();

        if (id == R.id.toolbarConfiguracion) {

        } else if ...

         */

        // Por ahora solo una opcion, por lo que se asume que se pincha esa
        ActividadPadre.redirigirAActividad(PreferenciasActivity.class); // redirigir a la actividad de preferencias

        return super.onOptionsItemSelected(item);
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

         Source getClassName() -> https://stackoverflow.com/questions/6271417/java-get-the-current-class-name


         */

        // Obtener el nombre de la clase
        String nombreClase = this.getClass().getSimpleName();

        // Crear string builder (solo por razones de mejor rendimiento, funciona
        // también concatenar strings a secas)
        StringBuilder constructorNombreActividad = new StringBuilder();


        // Settear la lectura del nombre de la clase a que justo omita la palabra
        // "Activity" del final
        int charTarget = nombreClase.length() - 8; // No iteramos por la palabra "Activity", los ultimos 8 chars


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

        String nombreActividad = constructorNombreActividad.toString(); // cargar la string concatenada
        String nombreContenedor = "contenedor" + nombreActividad; // añadir "contenedor" delante para formar el nombre del contenedor
        String nombreLayout = ""; // reservar la variable para cargar el nombre del layout

        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            nombreLayout = "activity" + nombreActividad + "_landscape"; // dar "activity" delante y "_landscape" detras si landscape
        } else {
            nombreLayout = "activity" + nombreActividad + "_portrait"; // dar "activity" delante y "_portrait" detras si portrait
        }


        // Obtener los ids en int de los recursos respectivos para poder usarlos

        this.idContenedor = super.getResources().getIdentifier(nombreContenedor, "id", super.getPackageName());
        int idLayout = super.getResources().getIdentifier(nombreLayout, "layout", super.getPackageName());


        // Montar el layout "idLayout" en "idContenedor"

        fragmento = idLayout; // Esta es la única forma de pasarle una variable a la clase
                              // La constructora no se puede tocar y los setters no funcionan
                              // para onCreateView, por lo que un atributo estático es la única forma de hacerlo

        super.getSupportFragmentManager().beginTransaction()
                .replace(this.idContenedor, new MiFragmento())
                .commit();




    }

    protected void setEstilo(View fragmento) {

        // Pintar los distintos elementos de la UI según el estilo elegido

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;

        if (estilo.contentEquals("1")) {
            // Estilo día

            // Settear fondo
            fragmento.setBackgroundColor(Color.WHITE);

            // Iterar por cada elemento de la UI
            for (int i = 0; i != viewGroup.getChildCount(); i++) {

                View elemento = viewGroup.getChildAt(i);
                if (elemento instanceof Button) {
                    // Crear estilo del boton
                    Button boton = (Button) elemento;
                    boton.setBackgroundResource(R.drawable.fondo_boton_claro); // Aplicar el fondo desde drawable
                    boton.setTextColor(Color.WHITE);

                } else if (elemento instanceof EditText) {
                    // Crear estilo del campo para poner texto
                    EditText edit = (EditText) elemento;
                    //edit.setBackgroundColor(Color.GRAY);
                    edit.setTextColor(Color.BLACK);


                } else if (elemento instanceof TextView) {
                    // Crear el estilo del texto predefinido
                    TextView texto = (TextView) elemento;
                    texto.setTypeface(null, Typeface.BOLD);
                    texto.setTextColor(Color.rgb(38, 186, 240));


                }


            }

            // Crear el estilo de la toolbar si existe
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.rojo)); // Establece el color de fondo de la Toolbar
                toolbar.setTitleTextColor(Color.WHITE); // Color titulo

            }



        } else {
            // Estilo neón (misma lógica que día, pero con disintos colores)

            fragmento.setBackgroundColor(Color.BLACK);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI
                View elemento = viewGroup.getChildAt(i);

                if (elemento instanceof  Button) {
                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.GREEN);
                    boton.setTextColor(Color.WHITE);

                } else if (elemento instanceof EditText) {
                    EditText edit = (EditText) elemento;
                    edit.setBackgroundColor(Color.rgb(0, 100, 0));
                    edit.setTextColor(Color.WHITE);


                } else if (elemento instanceof TextView) {

                    TextView texto = (TextView) elemento;
                    texto.setTypeface(null, Typeface.BOLD);
                    texto.setTextColor(Color.WHITE);
                }
            }

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setBackgroundColor(Color.GREEN); // Establece el color de fondo de la Toolbar
                toolbar.setTitleTextColor(Color.WHITE); // Color titulo

            }


        }


    }

    private void setIdioma() {

        // Post: Settear el idioma del usuario para asegurarse de que la actividad hija
        //       se cargue en el idioma correcto


        // Recoger las preferencias del usuario

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idioma","es");
        Locale nuevaloc = new Locale(idioma);

        // Settear el idioma usando el código de eGela

        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }


    protected static boolean enLandscape() {
        return actividadEnEjecucion.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    // Métodos estáticos para manejar el intent de la actividad
    public static void añadirAIntent(String key, String value) {
        actividadEnEjecucion.getIntent().putExtra(key, value);
    }

    protected static void quitarDeIntent(String key) {
        actividadEnEjecucion.getIntent().removeExtra(key);
    }

    public static String obtenerDeIntent(String key) {

        if (actividadEnEjecucion.getIntent().hasExtra(key)) {
            return actividadEnEjecucion.getIntent().getExtras().getString(key);
        } else {
            return "";
        }
    }



    protected static View obtenerFragmentoOrientacion() {
        return actividadEnEjecucion.getSupportFragmentManager().findFragmentById(actividadEnEjecucion.idContenedor).getView();
    }

    public static void redirigirAActividad(Class ActividadTarget) {

        // Cierra la actividad actual y abre ActividadTarget.
        // Todos los datos del intent de la actividad actual se pasan a la siguiente actividad

        // Solo se permite el cambio de actividad si sePermiteCambiar es true o se redirige a la
        // misma actividad



        if (ActividadPadre.comprobarUnlock() || ActividadTarget.isInstance(actividadEnEjecucion)){


            // Crear el nuevo intent
            Intent intent = new Intent(ActividadPadre.actividadEnEjecucion, ActividadTarget);


            // Guardar todos los datos del bundle al nuevo intent
            Bundle bundle = actividadEnEjecucion.getIntent().getExtras();

            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    intent.putExtra(key, bundle.getString(key));
                }
            }


            // Redirigir
            ActividadPadre.actividadEnEjecucion.startActivity(intent);
            ActividadPadre.actividadEnEjecucion.finish();

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
        // Redirigir a self
        ActividadPadre.redirigirAActividad(actividadEnEjecucion.getClass());
    }


    public static void cerrarApp() {
        ActividadPadre.actividadEnEjecucion.finish();
        System.exit(0);
    }

    public static void mostrarToast(int idMsg) {
        Toast.makeText(ActividadPadre.actividadEnEjecucion, idMsg, Toast.LENGTH_SHORT).show();
    }


    public static void peticionAServidor(String recurso, int idPeticion, String[] parametros, ObservadorDePeticion observador) {

        /*
            Realiza una petición al servidor de la app, usa 4 parámetros:

            - recurso: El recuso a llamar omitiendo ".php". Pueden ser: "usuarios", "amistades", "partidas" o "firebase"
            - idPeti : El id de la petición a usar. Cada recurso soporta distintos tipos de peticiones. Cada petición
                       tiene un id distinto (ver ConexionAServer para saber todos los tipos de peticiones posibles)
            - parametros: Cada petición requiere unas variables concretas para poder ser procesada. Estás se dan aquí
                          en forma de string

            - observador: El objeto que se encarga de ejecutar código justo cuando la respuesta del servidor llega de vuelta.
                          Puede ser null si no queremos atender la respuesta del servidor


         */



        if (ActividadPadre.comprobarUnlock()) {


            // Construir los datos con los parámetros de entrada

            Data datos = new Data.Builder()
                    .putString("recurso", recurso)
                    .putInt("idPeticion", idPeticion)
                    .putStringArray("parametros", parametros)
                    .build();


            // Crear la tarea

            ActividadPadre actAct = ActividadPadre.actividadEnEjecucion;
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionAServer.class).setInputData(datos).build();

            if (observador != null) {
                // Si el observador no es null, entonces también se tieen que linkar el observador a la tarea
                // para que se ejecute cuando la tarea acabe (o sea, cuando el servidor nos responda)

                // Lockear los botones durante el thread para que el user no pueda cambiar de actividad durante la petición al servidor
                // Se ejecuta el unlock en el observador tan pronto como se procese la respuesta
                ActividadPadre.lockRedirectsYPeticionesAServer(true);


                WorkManager.getInstance(actAct).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(actAct, observador);

            }

            // Encolar la tarea para que se ejecute ASAP
            WorkManager.getInstance(actAct).enqueue(otwr);
        }


    }


    public static void pushearTokenABDYLoggear(String user) {

        // Este método vincula el token del móvil con la cuenta en la que se está iniciando sesión
        // Tras esto, se redirige a la actividad de usuario loggeado


        // Añadir al intent el nombre del usuario registrado
        ActividadPadre.añadirAIntent("user", user);

        // Recoger el token del movil
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {

                    // Cuando se obtiene el token del movil, pushear la vinculación del token con la cuenta
                    String token = task.getResult();
                    String[] datos = {user, token};

                    ActividadPadre.peticionAServidor("usuarios", 2, datos, null);

                    // FInalmente, redirigir a la actividad de loggeado
                    ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

                } else {
                    Exception exception = task.getException();

                }
            }
        });

    }

    public static class MiFragmento extends Fragment {

        // La clase que crea el fragmento de la actividad. Puede estar orientado de forma portrait o landscape
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(fragmento, container, false);
        }
    }


}

