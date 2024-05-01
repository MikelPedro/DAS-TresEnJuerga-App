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


    if ($solicitante != $solicitado) {

        $com = $conn->prepare("SELECT Nombre FROM USUARIOS WHERE Nombre = ?");	    
        $com->bind_Param('s', $solicitado);
        $com->execute();
        $com->store_result();
        $com->bind_result($usuario);

        if ($com->fetch()) {

            $com->close();
            $com = $conn->prepare("SELECT Aceptado, UsuarioA FROM AMISTADES WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?) ");	    
            $com->bind_Param('ssss', $solicitante, $solicitado, $solicitado, $solicitante);
            $com->execute();
            $com->store_result();
            $com->bind_result($aceptado, $solicitanteDeLaFriendRequestExistente);
        
        
            if (!$com->fetch()) {
                $com->close();
                $val = true;
                $com = $conn->prepare("INSERT INTO AMISTADES VALUES (?, ?, 0)");	    
                $com->bind_Param('ss', $solicitante, $solicitado);
                $com->execute();
                $com->close();

                $statusCode = 0;

                // Todo correcto, mandar la notif a Firebase.
                notificarAFirebase(0, $solicitante, $solicitado);
                



            } else {
                $com->close();

                if ($aceptado == 0) {

                    if ($solicitanteDeLaFriendRequestExistente == $solicitante) {
                        $statusCode = 3;
                        // ERROR 3: Ya has mandado una friend request y esta pending

                    } else {
                        $statusCode = 4;
                        // ERROR 4: La otra persona te ha mandado una friend request y esta pending
                    }

                } else {
                    $statusCode = 5;
                    // ERROR 5: Ya soys friends
                }

            }
            $com->close();

        } else {
            $com->close();
            $statusCode = 2;
            // ERROR 2: Friend request a none
        }

    } else {
        $statusCode = 1;
        // ERROR 1: No friend request a self
    }

    $resultados = array(
        'respuesta' => $statusCode
    );

    echo json_encode($resultados);

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


    // Notificar a firebase
    notificarAFirebase(1, $solicitado, $solicitante);


    

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