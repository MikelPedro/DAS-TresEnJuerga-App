<?php
include './extras/cabecera.php';


// Peticiones directas a firebase aquí (sin pasar por BD), las que se hacen tras una llamada a BD
// no pasan por aquí, la BD las llama ella sola



$id = $_GET['id'];

switch ($id) {

    case "0":
        // Rechazar revancha
        notificarAFirebase("7", $_GET['dato1'], $_GET['dato2']);
        break;

    case "1":
        // Ping request: Conectar revancha
        notificarAFirebase("8", $_GET['dato1'], $_GET['dato2']);
        break;
    case "2":
        // Expulsar al otro user de la partida porque se quito de amigos
        notificarAFirebase("9", $_GET['dato1'], $_GET['dato2']);
}


$conn->close();
?>
