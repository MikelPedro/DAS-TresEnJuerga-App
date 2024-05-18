package com.das.tresenjuerga.otrasClases;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoComoJugar extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setTitle("¿Cómo jugar al Tres en Juerga?");
        builder.setMessage("Este juego sigue la misma dinamica que el famoso Tic-Tac-Toe " +
                "(Tres en raya). Puedes encontrar información sobre las reglas y cómo jugar una partida " +
                " haciendo clic en Ver Enlace\n");

        //Opcion para ver enlace que lleva a una pagina web mediante un intent implicito
        builder.setPositiveButton("Ver Enlace", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Intent para abrir una web
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tictactoefree.com/es/reglas"));
                startActivity(intent);
                dismiss();
            }
        });

        //Opcion para quitar el dialogo
        builder.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }
}
