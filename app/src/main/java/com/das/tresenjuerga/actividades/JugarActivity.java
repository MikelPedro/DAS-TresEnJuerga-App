package com.das.tresenjuerga.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.das.tresenjuerga.R;

public class JugarActivity extends ActividadPadre {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View fragmento = super.obtenerFragmentoOrientacion();

    }


}

