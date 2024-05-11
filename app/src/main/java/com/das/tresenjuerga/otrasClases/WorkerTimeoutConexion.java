package com.das.tresenjuerga.otrasClases;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.das.tresenjuerga.actividades.ActividadPadre;

public class WorkerTimeoutConexion extends Worker {


    // El worker que se encarga de updatear el estado de Revancha a error de conexión por timeout

    public WorkerTimeoutConexion(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Pasa al estado 6 (error de conexion) de la revancha. Esto se ejecuta al de X tiempo si la conexion no se realiza

        // Comprobar que el estado de revancha actualmente es efectivamente esperando conexión
        if (ActividadPadre.obtenerDeIntent("estadoRevancha").contentEquals("4")) {

            // Si lo es, eliminar la revancha creada de BD porque la partida no se va a jugar
            String[] data = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("oponente")};
            ActividadPadre.peticionAServidor("partidas",5, data, null);

            // actualizar el estado a error de conexión y recargar la interfaz de revancha para mostrar el error.
            ActividadPadre.añadirAIntent("estadoRevancha", "6");
            ActividadPadre.recargarActividad();
        }
        return Result.success();
    }
}
