package com.das.tresenjuerga.otrasClases;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.ActividadPadre;


public class DialogoSalir extends DialogFragment {

    // Dialogo con la confirmación de salida de la app. Es un pop-up en el que se da la opción
    // de confirmar la salida o quedarse en la app.

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Crear el pop-up en sí

        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.tituloSalir);
        builder.setMessage(R.string.descSalir);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActividadPadre.cerrarApp();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Listener de negar la salida, el usuario se queda en la app
                // Por defecto este onClick cierra el pop-up, que es lo que se necesita


                // NOP (No operation)
            }
        });


        return builder.create();
    }
}
