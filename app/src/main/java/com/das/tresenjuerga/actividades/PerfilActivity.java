package com.das.tresenjuerga.actividades;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ListaAdapterMisAmigos;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLSyntaxErrorException;

public class PerfilActivity extends ActividadPadre {

    private boolean miPerfil;
    private ActivityResultLauncher<Intent> sacadorDeFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_perfil);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String userAVisualizar = ActividadPadre.obtenerDeIntent("userAVisualizar");
        this.miPerfil = userAVisualizar.contentEquals(ActividadPadre.obtenerDeIntent("user"));


        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();
        fragmento.findViewById(R.id.perfilB_Volver).setOnClickListener(new BotonListener(2));
        ((TextView)fragmento.findViewById(R.id.perfilT_Nombre)).setText(userAVisualizar);

        Button botonUtilidad = fragmento.findViewById(R.id.perfilB_CambiarFoto);


        // Esta interfaz se usa para ver los datos del perfil de ti y de tus amigos.
        // Si estás viendo el perfil de un amigo se le permite retar a una partida
        // Si estás viendo tu perfil, se te permite cambiar la foto

        if (this.miPerfil) {
            botonUtilidad.setText(super.getString(R.string.cambiarFoto));
            botonUtilidad.setOnClickListener(new BotonListener(0));

            // intent para camara

            this.sacadorDeFoto =
                    registerForActivityResult(new
                            ActivityResultContracts.StartActivityForResult(), result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                result.getData()!= null) {

                            // Obtener la foto elegida


                            Bundle bundle = result.getData().getExtras();
                            ImageView fotoView = findViewById(R.id.perfilF_Foto);
                            Bitmap foto = (Bitmap) bundle.get("data");

                            // Rescalar a 150dpx150dp

                            int anchoDestino = fotoView.getWidth();
                            int altoDestino = fotoView.getHeight();

                            int anchoImagen = foto.getWidth();
                            int altoImagen = foto.getHeight();
                            float ratioImagen = (float) anchoImagen / (float) altoImagen;
                            float ratioDestino = (float) anchoDestino / (float) altoDestino;
                            int anchoFinal = anchoDestino;
                            int altoFinal = altoDestino;

                            if (ratioDestino > ratioImagen) {
                                anchoFinal = (int) ((float)altoDestino * ratioImagen);
                            } else {
                                altoFinal = (int) ((float)anchoDestino / ratioImagen);
                            }

                            if (anchoFinal == 0 || altoFinal == 0) {
                                ActividadPadre.mostrarToast(R.string.errorFotoRotacion);

                            } else {
                                Bitmap fotoRedimensionada = Bitmap.createScaledBitmap(foto,anchoFinal,altoFinal,true);

                                try {
                                    // Codificar la imagen para subirla

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    fotoRedimensionada.compress(Bitmap.CompressFormat.JPEG, 10, stream);

                                    byte[] fototransformada = stream.toByteArray();
                                    String fotoen64 = Base64.encodeToString(fototransformada,Base64.NO_WRAP);

                                    // Pedir a BD que nos suba la foto

                                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), fotoen64};

                                    ActividadPadre.peticionAServidor("usuarios", 3, datos, new ObservadorDeSubidaDeImagen());


                                } catch (Exception e) {
                                    ActividadPadre.mostrarToast(R.string.errorFotoTamaño);

                                }
                            }







                        }
                    }
                    );

        } else {
            botonUtilidad.setText(super.getString(R.string.retar));
            botonUtilidad.setOnClickListener(new BotonListener(1));

        }
        String[] datos = {userAVisualizar};
        ActividadPadre.peticionAServidor("usuarios", 4, datos, new ObservadorDeBajadaDeImagen());

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Se ha dado el permiso, colocar al user en el mapa
                Intent intentDeFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                this.sacadorDeFoto.launch(intentDeFoto);            }
        }
    }


    private class ObservadorDeSubidaDeImagen extends ObservadorDePeticion {


        @Override
        protected void ejecutarTrasPeticion() {
            // La imagen se ha subido, recargar la actividad para mostrarla
            ActividadPadre.recargarActividad();

        }
    }
    private class ObservadorDeBajadaDeImagen extends ObservadorDePeticion {


        @Override
        protected void ejecutarTrasPeticion() {

            String fotoString =  super.getString("foto");
            if (fotoString != null) {
                byte[] imagen = Base64.decode(fotoString, Base64.NO_WRAP);

                Bitmap foto = BitmapFactory.decodeStream(new ByteArrayInputStream(imagen));
                ((ImageView)PerfilActivity.super.findViewById(R.id.perfilF_Foto)).setImageBitmap(foto);

            } else {

                // NO tiene foto de perfil, poner una por defecto

                // Source img: https://www.pngwing.com/es/free-png-pjkpq
                ((ImageView)PerfilActivity.super.findViewById(R.id.perfilF_Foto)).setImageResource(R.drawable.sin_imagen_perfil);

            }




        }
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
                    // Cambiar foto
                    if (ContextCompat.checkSelfPermission(ActividadPadre.getActividadActual(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Pedir permiso para usar cam para subir foto
                        ActivityCompat.requestPermissions(ActividadPadre.getActividadActual(),
                                new String[]{Manifest.permission.CAMERA},
                                1);

                    } else {
                        // Ya tiene el permiso, abrir cam

                        Intent intentDeFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        PerfilActivity.this.sacadorDeFoto.launch(intentDeFoto);
                    }
                    break;
                case 1:
                    // Retar
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("userAVisualizar")};
                    ActividadPadre.peticionAServidor("partidas", 0, datos, new ObservadorDeRetarPartida());

                    break;
                case 2:
                    // Volver

                    ActividadPadre.quitarDeIntent("userAVisualizar");

                    // Dependiendo de si estoy viendo mi perfil o no, deducir de donde vinimos para volver allí
                    if (PerfilActivity.this.miPerfil) {
                        ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

                    } else {
                        ActividadPadre.redirigirAActividad(AmigosActivity.class);

                    }


            }
        }

        private class ObservadorDeRetarPartida extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                if (super.getBoolean("respuesta")) {
                    ActividadPadre.mostrarToast(R.string.retarCorrecto);

                } else {
                    ActividadPadre.mostrarToast(R.string.retarError);

                }
            }
        }
    }
}