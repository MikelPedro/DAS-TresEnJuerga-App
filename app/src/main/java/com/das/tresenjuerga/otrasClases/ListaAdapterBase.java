package com.das.tresenjuerga.otrasClases;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.das.tresenjuerga.actividades.ActividadPadre;

public abstract class ListaAdapterBase extends BaseAdapter {


    // La clase padre que hereda al resto de ArrayAdapters, tiene las funcionalidades básicas
    // que sus hijos heredan

    private Object[] listaValores;
    private int cardViewTarget;
    private ActividadPadre actividad;
    public ListaAdapterBase(Object[] listaValores, int cardViewTarget) {
        this.listaValores = listaValores;
        this.cardViewTarget = cardViewTarget;
        this.actividad = ActividadPadre.getActividadActual();
    }

    @Override
    public int getCount() {
        return this.listaValores.length;
    }

    @Override
    public Object getItem(int position) {
        return this.listaValores[position];
    }

    @Override
    public long getItemId(int position) {return position;}


    protected ActividadPadre getActividad() {return this.actividad;}
    protected String getString(int idCardView, int idInfo) {

        if (!(this.listaValores[0] instanceof Object[])) {
            return (String) this.listaValores[idCardView];
        } else {
            return (String) ((Object[]) this.listaValores[idCardView])[idInfo];
        }

    }

    protected long getLong(int idCardView, int idInfo) {

        if (!(this.listaValores[0] instanceof Object[])) {
            return (long) this.listaValores[idCardView];
        } else {
            return (long) ((Object[]) this.listaValores[idCardView])[idInfo];
        }

    }

    protected View crearLayout() {
        View view = ((LayoutInflater)this.actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.cardViewTarget,null);
        this.setEstilo(view);
        return view;
    }

    private void setEstilo(View view) {
        // TODO: Settear más estilos here, por ahora solo están día y neón de la app de biliboteca

        // Pintar los distintos elementos de la UI según el estilo elegido. Usa la misma lógica que estilos
        // de actividades



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActividadPadre.getActividadActual());
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) view;

        if (estilo.contentEquals("1")) {
            // Estilo día

            view.setBackgroundColor(Color.WHITE);

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

            view.setBackgroundColor(Color.BLUE);

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
}
