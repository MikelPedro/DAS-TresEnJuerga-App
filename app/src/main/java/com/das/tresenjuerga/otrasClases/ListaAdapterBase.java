package com.das.tresenjuerga.otrasClases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import com.das.tresenjuerga.actividades.ActividadPadre;

public abstract class ListaAdapterBase extends BaseAdapter {


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

    protected int getInteger(int idCardView, int idInfo) {

        if (!(this.listaValores[0] instanceof Object[])) {
            return (int) this.listaValores[idCardView];
        } else {
            return (int) ((Object[]) this.listaValores[idCardView])[idInfo];
        }

    }

    protected View crearLayout() {
        View view = ((LayoutInflater)this.actividad.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.cardViewTarget,null);
        this.setEstilo(view);
        return view;
    }

    private void setEstilo(View view) {
        // TODO: Settear estilo here

    }
}
