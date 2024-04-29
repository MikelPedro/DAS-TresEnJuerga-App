package com.das.tresenjuerga.otrasClases;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.actividades.PerfilActivity;

public class ListaAdapterMisAmigos extends ListaAdapterBase{


    private String amigo;
    public ListaAdapterMisAmigos(Object[] listaValores, int cardViewTarget) {
        super(listaValores, cardViewTarget);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        // Generar el layout según la plantilla dada por la clase que le llama
        View view = super.crearLayout();

        // Llenar los campos de contra quien juega y a quien le toca

        this.amigo = super.getString(pos, 0);
        ((TextView)view.findViewById(R.id.instanciaAmigoT_Nombre)).setText(this.amigo);


        // Dar los listeners correspondientes a los botones
        view.findViewById(R.id.instanciaAmigoB_VerInfo).setOnClickListener(new BotonListener(0));
        view.findViewById(R.id.instanciaAmigoB_Retar).setOnClickListener(new BotonListener(1));
        view.findViewById(R.id.instanciaAmigoB_Eliminar).setOnClickListener(new BotonListener(2));





        return view;
    }



    private class BotonListener implements View.OnClickListener {


        private int id;
        public BotonListener(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {

            switch (this.id) {
                case 0:
                    // Ver perfil
                    ListaAdapterMisAmigos.super.getActividad().añadirAIntent("userAVisualizar", ListaAdapterMisAmigos.this.amigo);
                    ListaAdapterMisAmigos.super.getActividad().redirigirAActividad(PerfilActivity.class);
                    break;
                case 1:
                    break;
                case 2:

            }

        }
    }
}
