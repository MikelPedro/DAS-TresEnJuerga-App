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

    private Data info;

    @Override
    public void onChanged(WorkInfo workInfo) {
        if (workInfo != null && workInfo.getState().isFinished()) {
            this.info = workInfo.getOutputData();
            for (String key: this.info.getKeyValueMap().keySet()) {
                System.out.println(key);

            }
            ActividadPadre.setPermitirCambiarActividad(true); // unlockear el thread de nuevo para que se permita usar los botones
            this.ejecutarTrasPeticion();
        }
    }

    protected abstract void ejecutarTrasPeticion();

    protected String getString(String key) {
        return this.info.getString(key);
    }
    protected long getLong(String key) {
        return this.info.getLong(key, 0);
    }

    protected boolean getBoolean(String key) {
        return this.info.getBoolean(key, false);
    }

    protected String[] getStringArray(String key) {
        return this.info.getStringArray(key);
    }

    protected int[] getIntArray(String key) {
        return this.info.getIntArray(key);
    }

    protected boolean[] getBooleanArray(String key) {
        return this.info.getBooleanArray(key);
    }
}
