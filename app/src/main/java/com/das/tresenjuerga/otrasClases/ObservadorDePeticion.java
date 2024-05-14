package com.das.tresenjuerga.otrasClases;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;
import com.das.tresenjuerga.actividades.MainActivity;

public abstract class ObservadorDePeticion implements Observer<WorkInfo> {

    // Esta clase hereda a todos los Observers de peticiones a servidor. Incluye funciones genéricas
    // que sus hijos pueden usar

    private Data info;

    @Override
    public void onChanged(WorkInfo workInfo) {
        if (workInfo != null && workInfo.getState().isFinished()) {
            this.info = workInfo.getOutputData();

            this.ejecutarTrasPeticion();
            ActividadPadre.lockBotones(false);
        }
    }

    // Este es el método que sus hijos implementan, es ejecutado cuando el servidor responde con los datos


    protected abstract void ejecutarTrasPeticion();

    // Getters de los datos del cuerpo del servidor
    protected String getString(String key) {return this.info.getString(key);}
    protected long getLong(String key) {
        return this.info.getLong(key, 0);
    }

    protected boolean getBoolean(String key) {
        return this.info.getBoolean(key, false);
    }

    protected String[] getStringArray(String key) {
        return this.info.getStringArray(key);
    }

    protected long[] getLongArray(String key) {
        return this.info.getLongArray(key);
    }

    protected boolean[] getBooleanArray(String key) {
        return this.info.getBooleanArray(key);
    }
}
