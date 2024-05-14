package com.das.tresenjuerga.actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.das.tresenjuerga.R;
import com.das.tresenjuerga.otrasClases.ObservadorDePeticion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PerfilActivity extends ActividadPadre {


    /*
        Esta pantalla indica los datos de un perfil (puede ser el tuyo o el de otro)

        En el perfil se muestra la foto de perfil y el nombre del usuario
        (teniendo una foto placeholder si no se ha sacado una ya)

        Si estás visualizando tu perfil, se te permite cambiar la foto.
        Si estás visualizando otro perfil, se te permite retar a la persona (dado que tiene que
        ser tu amigo para que le puedas ver su perfil)



     */

    private boolean miPerfil; // indica si estoy viendo mi perfil o el de otra persona
    private ActivityResultLauncher<Intent> sacadorDeFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_perfil);



        // Checkear flags para ver si se requiere hacer redirect porque no tiene sentido mostrar la partida

        if (ActividadPadre.obtenerDeIntent("expulsadoPorNoAmigo").contentEquals("true")) {

            // Si el otro te quita como amigo cuando estás viendo su perfil, redirigir

            ActividadPadre.quitarDeIntent("expulsadoPorNoAmigo");
            ActividadPadre.redirigirAActividad(AmigosActivity.class);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobar si el usuario que estoy viendo y yo somos el mismo
        String userAVisualizar = ActividadPadre.obtenerDeIntent("userAVisualizar");
        this.miPerfil = userAVisualizar.contentEquals(ActividadPadre.obtenerDeIntent("user"));


        // Cargar el fragmento de la actividad
        View fragmento = ActividadPadre.obtenerFragmentoOrientacion();

        // Dar listeners a los botones
        fragmento.findViewById(R.id.perfilB_Volver).setOnClickListener(new BotonListener(2));
        ((TextView)fragmento.findViewById(R.id.perfilT_Nombre)).setText(userAVisualizar);


        // Este botón es interesante, pues se le da un listener distinto dependiendo de si puedo cambiar mi foto
        // o retar a mi amigo
        Button botonUtilidad = fragmento.findViewById(R.id.perfilB_CambiarFoto);



        if (this.miPerfil) {
            // si es mi perfil, dar el listener de cambiar la foto

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
                                // Fallback de error por rotacion de camara
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

                                    // Error por demasiada calidad de imagen
                                    ActividadPadre.mostrarToast(R.string.errorFotoTamaño);

                                }
                            }







                        }
                    }
                    );

        } else {
            // si no es mi perfil, dar al botón el listener de retar
            botonUtilidad.setText(super.getString(R.string.retar));
            botonUtilidad.setOnClickListener(new BotonListener(1));

        }

        // Pedir al servidor que nos de la foto de perfil del usuario que estamos viendo
        String[] datos = {userAVisualizar};
        ActividadPadre.peticionAServidor("usuarios", 4, datos, new ObservadorDeBajadaDeImagen());

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Procesar la respuesta del permiso de la camara

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Se ha dado el permiso, abrir la camara
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

            // El servidor responde con la foto de perfil

            String fotoString =  super.getString("foto");
            if (fotoString != null) {

                // Si la foto existe, cargarla
                byte[] imagen = Base64.decode(fotoString, Base64.NO_WRAP);

                Bitmap foto = BitmapFactory.decodeStream(new ByteArrayInputStream(imagen));
                ((ImageView)PerfilActivity.super.findViewById(R.id.perfilF_Foto)).setImageBitmap(foto);

            } else {

                // Si la foto no existe, poner una por defecto cargada del cliente de la app

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
                    // El primer botón en estado 0 cambia la foto del perfil

                    // Ver si tenemos permiso para la camara
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

                    // El primer botón en estado 1 sirve para retar al jugador, pedir al servidor que se rete
                    String[] datos = {ActividadPadre.obtenerDeIntent("user"), ActividadPadre.obtenerDeIntent("userAVisualizar")};
                    ActividadPadre.peticionAServidor("partidas", 0, datos, new ObservadorDeRetarPartida());

                    break;
                case 2:
                    // El segundo botón va una interfaz atrás

                    ActividadPadre.quitarDeIntent("userAVisualizar");

                    // Dependiendo de si estoy viendo mi perfil o no, deducir de donde vinimos para volver allí
                    if (PerfilActivity.this.miPerfil) {
                        // Si veo mi perfil, voy al menú principal de usuarios loggeados
                        ActividadPadre.redirigirAActividad(UsuarioLoggeadoActivity.class);

                    } else {
                        // Si no veo mi perfil, voy a mi lista de amigos
                        ActividadPadre.redirigirAActividad(AmigosActivity.class);

                    }


            }
        }

        private class ObservadorDeRetarPartida extends ObservadorDePeticion {
            @Override
            protected void ejecutarTrasPeticion() {

                // El servidor responde con si se puede retar o no, mostrar toast correspondiente

                if (super.getBoolean("respuesta")) {
                    ActividadPadre.mostrarToast(R.string.retarCorrecto);

                } else {
                    ActividadPadre.mostrarToast(R.string.retarError);

                }
            }
        }
    }
}