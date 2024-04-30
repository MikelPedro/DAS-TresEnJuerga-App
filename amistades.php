<?php
include './extras/cabecera.php';

$id = $_GET['id'];

switch ($id) {

    case "0":
        usuariosFactiblesParaSolicitud($conn, $_GET['dato1']);
        break;

    case "1":
        crearSolicitud($conn, $_GET['dato1'], $_GET['dato2']);
        break;
	
    case "2":
	    verSolicitudes($conn, $_GET['dato1']);
	    break;

    case "3":
        aceptarSolicitud($conn, $_GET['dato1'], $_GET['dato2']);
        break;

    case "4":
        borrarAmistad($conn, $_GET['dato1'], $_GET['dato2']); // Sirve para amigos ya aceptados o solicitudes de amistad
        break;
    
    case "5":
        verListaAmigos($conn, $_GET['dato1']);    


}

$conn->close();

//  Users que puedes enviar solicitud, Crear solicitud, Ver solicitudes, acpetar, (rechazar, quitar), ver amigos


function usuariosFactiblesParaSolicitud($com, $nombre) {

    $user = cifrar($nombre);

    $com = $conn->prepare("SELECT Nombre FROM USUARIOS WHERE Nombre NOT IN ((SELECT ?) UNION (SELECT UsuarioB FROM AMISTADES WHERE UsuarioA = ?) UNION (SELECT UsuarioA FROM AMISTADES WHERE UsuarioB = ?))");	    
    $com->bind_Param('sss', $user, $user, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($userPosible);

    $potencialesAmigos = array();

    while ($com->fetch()) {
        array_push($potencialesAmigos, descifrar($userPosible));
    }

    $com->close();

    $resultados = array(
        'nombres' => $potencialesAmigos
    );

    echo json_encode($resultados);
}

function crearSolicitud($conn, $solicitante, $solicitado) {
    $solicitante = cifrar($solicitante);
    $solicitado = cifrar($solicitado);

    $com = $conn->prepare("INSERT INTO AMISTADES VALUES (?, ?, 0)");	    
    $com->bind_Param('ss', $solicitante, $solicitado);
    $com->execute();
    $com->close();

}

function verSolicitudes($conn, $nombre) {

    $user = cifrar($nombre);

    $com = $conn->prepare("SELECT UsuarioA FROM AMISTADES WHERE UsuarioB = ? AND Aceptado = 0");	    
    $com->bind_Param('s', $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($solicitante);
    
    $listaSolicitantes = array();

    while ($com->fetch()) {
        array_push($listaSolicitantes, descifrar($solicitante));
    }

    $com->close();

    $resultados = array(
        'nombres' => $listaSolicitantes
    );

    echo json_encode($resultados);


}

function aceptarSolicitud($conn, $solicitado, $solicitante) {
    $solicitante = cifrar($solicitante);
    $solicitado = cifrar($solicitado);

    $com = $conn->prepare("UPDATE AMISTADES SET Aceptado = 1 WHERE UsuarioA = ? AND UsuarioB = ?");	    
    $com->bind_Param('ss', $solicitante, $solicitado);
    $com->execute();
    $com->close();


}

function borrarAmistad($conn, $nombre, $examigo) {
    $user = cifrar($nombre);
    $examigo = cifrar($examigo);

    $com = $conn->prepare("DELETE FROM AMISTADES WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?)");	    
    $com->bind_Param('ssss', $nombre, $examigo, $examigo, $nombre);
    $com->execute();
    $com->close();


}

function verListaAmigos($conn, $nombre) {

    $user = cifrar($nombre);

    $com = $conn->prepare("(SELECT UsuarioA FROM AMISTADES WHERE UsuarioB = ? AND Aceptado = 1) UNION (SELECT UsuarioB FROM AMISTADES WHERE UsuarioA = ? AND Aceptado = 1)");	    
    $com->bind_Param('ss', $user, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($amigo);
    
    $listaAmigos = array();

    while ($com->fetch()) {
        array_push($listaAmigos, descifrar($amigo));
    }

    $com->close();

    $resultados = array(
        'nombres' => $listaAmigos
    );

    echo json_encode($resultados);


}





?>