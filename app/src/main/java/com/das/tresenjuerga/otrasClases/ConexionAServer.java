package com.das.tresenjuerga.otrasClases;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;





/*



        Estructura de la BD (solo requiere estos comandos y no hay instancias añadidas a ella por defecto en
        ninguna tabla)


        CREATE DATABASE tresEnRaya;

        CREATE TABLE USUARIOS(Nombre VARCHAR(256), Contraseña VARCHAR(64), SaltContraseña VARCHAR(10), Foto BLOB, PRIMARY KEY(Nombre));
        CREATE TABLE DISPOSITIVOS(Token VARCHAR(200), Cuenta VARCHAR(256), PRIMARY KEY(Token), FOREIGN KEY(Cuenta) REFERENCES USUARIOS(Nombre));
        CREATE TABLE AMISTADES(UsuarioA VARCHAR(256), UsuarioB VARCHAR(256), Aceptado TINYINT, PRIMARY KEY(UsuarioA, UsuarioB), FOREIGN KEY(UsuarioA) REFERENCES USUARIOS(Nombre), FOREIGN KEY(UsuarioB) REFERENCES USUARIOS(Nombre));
        CREATE TABLE PARTIDAS(UsuarioA VARCHAR(256), UsuarioB VARCHAR(256), Aceptado TINYINT, TurnoDeA TINYINT, Tablero VARCHAR(9), PRIMARY KEY(UsuarioA, UsuarioB), FOREIGN KEY(UsuarioA) REFERENCES USUARIOS(Nombre), FOREIGN KEY(UsuarioB) REFERENCES USUARIOS(Nombre));


        Para hacer llamada a server:  http://IP:port/[0].php?id=[1]&&dato1=[2]&&...&&datoN-1=[N]

        Nombre de los parámetros para pasarle al worker (el contenido de los parametros debe ser en string)

        0: "recurso" -> el nombre del recurso sin .php (usuarios, amistades, partidas)
        1: "id" -> el id de la petición. Abajo se lista cada recurso que peticiones tiene disponibles
                   Id=0 llama a la primera petición de la lista, Id=1 al segundo, etc. Recuerda que el número
                   debe ser en string, no en int (o, sea en formato "0", "1", etc.)


        2: "dato1" -> primer parámetro de la llamada. Si el metodo es funcion(a,b), dato1 aloja el valor de la variable a
        3: "dato2" -> segundo parámetro de la llamada. Si el metodo es funcion(a,b), dato2 aloja el valor de la variable b
        ...
        N: "dato(N-1)"


        Se pueden hacer llamadas a 3 recursos en BD para obtener datos. Listados por orden de id por recurso:

        usuarios.php:

        - insertarUsuario(nombre, password) -> bool: se insertó?
        - comprobarCredenciales(nombre , password) -> bool: correcto?
        - setTokenDeUsuario(nombre, token) -> void   [token es el token del movil de firebase, para settearlo a la cuenta]
        - subirFotoDePerfil(nombre, fotoBase64) -> void
        - bajarFotoDePerfil(nombre) -> string (Foto)


        amistades.php:

        - usuariosFactiblesASolicitud(nombre) -> String[] [nombres de users que no son amigos tuyos ni están en solicitudes pendientes]
        - crearSolicitud(solicitante, solicitado) -> int [respuesta con si se ha añadido o el error ocurrido]
        - verSolicitudes(nombre) -> String[] [nombres de los users que han mandado friend request]
        - aceptarSolicitud(solicitado, solicitante) -> void
        - borrarAmistad(nombre, elAmigoAQuitar) -> void [sirve para rechazar solicitudes o borrar gente de la friendlist]


        partidas.php

        - solicitarPartida(solicitante, solicitado) -> bool: se pudo solicitar?  [la solicitud falla si ya hay una queueada (cualquiera de los dos pueden ser el solicitante) o si ya tienen una partida en curso]
        - aceptarPartida(solicitado, solicitante) -> void
        - obtenerPartidas(nombre) -> String[] y int[] [Lista de strings tiene los nombres de los oponentes. Lista de ints guarda un estado por partida: (0: solicitud de partida, 1: turno del otro, 2: tu turno)]
        - obtenerGamestate(nombre, oponente) -> String(9 chars), String(1 char). [Primer string devuelve estado del tablero, segundo string que figura usas. Ej: "---AB--A-"  y "B"]
        - realizarJugada(nombre, contrario, tablero) -> void [Pone al match ese el tablero dado, la idea es ir actualizando char a char por jugada]
        - quitarPartida(nombre, contrario) -> void [Sirve para rechazar la partida o eliminarla tras acabarla]



         Las notificaciones a firebase se llaman internamente desde el servidor cuando algo interesante ocurre.

 */


public class ConexionAServer extends Worker {

    private static final String IP = "34.163.130.62";
    private static final String PUERTO = "80";

    public ConexionAServer(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }



    private String obtenerParametros(int idPeticion, String[] parametrosEnUri) {
        String parametrosExtra = null;
        Uri.Builder builder = new Uri.Builder();

        if (idPeticion >= 0) {
            builder.appendQueryParameter("id", Integer.toString(idPeticion));


            int i = 1;
            for (String dato: parametrosEnUri) {
                builder.appendQueryParameter("dato"+i, dato);
                i++;
            }


        }
        return builder.build().getEncodedQuery();

    }


    private Data procesarPeticionUsuarios(int id, String result) throws ParseException, JSONException {

        JSONParser parser = new JSONParser();
        JSONObject json = null;

        if (id == 0 || id == 1 || id == 4) {
            json = (JSONObject) parser.parse(result);
        }


        switch (id) {

            case 0:
            case 1:
                return new Data.Builder().putBoolean("bool", (boolean) json.get("respuesta")).build();

            case 4:
                return new Data.Builder().putString("foto", (String)json.get("foto")).build();
            /*
            // TODO: Crear el bitmap en la actividad así
            try {
                   BitmapFactory.decodeStream(new ByteArrayInputStream(stringDeLaFoto.getBytes("UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            */

            default:
                return new Data.Builder().build();

        }
    }

    private Data procesarPeticionAmistades(int id, String result) throws ParseException, JSONException {

        JSONParser parser = new JSONParser();
        JSONObject json = null;



        if (id < 3 ||  id == 5) {
            json = (JSONObject) parser.parse(result);
        }


        switch (id) {

            case 0:
            case 2:
            case 5:
                return new Data.Builder().putString("nombres", (String)json.get("nombres")).build();
            case 1:
                return new Data.Builder().putInt("respuesta", (int)json.get("respuesta")).build();

            default:
                return new Data.Builder().build();

        }
    }

    private Data procesarPeticionPartidas(int id, String result) throws ParseException, JSONException {



        JSONParser parser = new JSONParser();
        JSONObject json = null;


        if (id == 0 || id == 2 || id == 3) {
            json = (JSONObject) parser.parse(result);
        }



        switch (id) {

            case 0:
                return new Data.Builder().putBoolean("bool", (boolean) json.get("respuesta")).build();


            case 2:
                JSONArray jsonArrayOponentes = (JSONArray) json.get("oponentes");
                JSONArray jsonArrayEstados = (JSONArray) json.get("estados");
                int cantidadPartidas = jsonArrayOponentes.size();
                String[] nombresOponentes = new String[cantidadPartidas];
                int[] estados = new int[cantidadPartidas];

                for(int i = 0; i < cantidadPartidas; i++) {
                    nombresOponentes[i] = (String) jsonArrayOponentes.get(i);
                    estados[i] = (int) jsonArrayEstados.get(i);

                }
                return new Data.Builder().putStringArray("oponentes", nombresOponentes).putIntArray("estados", estados).build();

            case 3:
                String figura = ((String) json.get("miFigura"));
                String tablero = (String) json.get("tablero");
                return new Data.Builder().putString("miFigura", figura).putString("tablero", tablero).build();

            default:
                return new Data.Builder().build();

        }


    }

    @NonNull
    @Override
    public Result doWork() {

        // Recoger parámetros

        Data inputData = getInputData();

        String recurso = inputData.getString("recurso");

        int idPeticion = inputData.getInt("idPeticion", -1);
        String[] parametros = inputData.getStringArray("parametros");

        Data respuesta;

        try {

            // Preparar conexion
            String direccion =  "http://"+ConexionAServer.IP+":"+ConexionAServer.PUERTO+"/"+recurso+".php?";;

            direccion = direccion + this.obtenerParametros(idPeticion, parametros);
            System.out.println(direccion);


            URL destino = new URL(direccion);
            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("POST");



            // Realizar la conexión
            urlConnection.connect();

            String line, result = "";
            BufferedInputStream inputStream = null;

            if (urlConnection.getResponseCode() == 200) {

                // Si se hace una peticion a firebase, no nos interesa recoger el body. Salir inmediatamente del método

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder concatenador = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    concatenador.append(line);
                }
                result = concatenador.toString();
                inputStream.close();






                // Tras recoger el body, pasarlo al método correspondiente para que parsee el json y
                // recoja sus campos relevantes

                switch (recurso) {
                    case "usuarios":
                        respuesta = this.procesarPeticionUsuarios(idPeticion, result);
                        break;
                    case "amistades":
                        respuesta = this.procesarPeticionAmistades(idPeticion, result);
                        break;
                    case "partidas":
                        respuesta = this.procesarPeticionPartidas(idPeticion, result);
                        break;

                    default:
                        respuesta = new Data.Builder().build();

                }


                urlConnection.disconnect();



            } else {return Result.failure();}

        } catch (Exception e) {e.printStackTrace(); return Result.failure();}


        return Result.success(respuesta);
    }
}


