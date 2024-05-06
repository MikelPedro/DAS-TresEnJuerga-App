package com.das.tresenjuerga.otrasClases;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.das.tresenjuerga.actividades.ActividadPadre;

public class WorkerTimeoutConexion extends Worker {


    public WorkerTimeoutConexion(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Pasa al estado 7 (error de conexion) de la revancha. Se llama al de X tiempo si la conexion no se realiza


        if (ActividadPadre.obtenerDeIntent("estadoRevancha").contentEquals("4")) {

            // Eliminar la revancha creada de BD
            String[] data = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
            ActividadPadre.peticionAServidor("partidas",5, data, null);

            ActividadPadre.añadirAIntent("estadoRevancha", "6");
            ActividadPadre.recargarActividad();
        }
        return Result.success();
    }
}
