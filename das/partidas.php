<?php
include './extras/cabecera.php';
$id = $_GET['id'];

switch ($id) {

    case "0":
        solicitarPartida($conn, $_GET['dato1'], $_GET['dato2']);
        break;

    case "1":
        aceptarPartida($conn, $_GET['dato1'], $_GET['dato2']);
        break;

    case "2":
        obtenerPartidas($conn, $_GET['dato1']);
        break;    

    case "3":
        obtenerGamestate($conn, $_GET['dato1'], $_GET['dato2']);
         break;

    case "4":

        realizarJugada($conn, $_GET['dato1'], $_GET['dato2'], $_GET['dato3']);
        break;

    case "5":
        quitarPartida($conn, $_GET['dato1'], $_GET['dato2'], $_GET['dato3']); // Porque se acaba o porque se rechaza
        break;

    case "6":
       estadoSolicitudPartida($conn, $_GET['dato1'], $_GET['dato2']);
       break;
    case "7":
        miTurno($conn, $_GET['dato1'], $_GET['dato2']);
       break;
    case "8":
       finalizarPartida($conn, $_GET['dato1'], $_GET['dato2']);

}

$conn->close();


// Pedir la partida, aceptar la partida, ver partidas , obtener gamestate, realizar una jugada,  borrar la partida por acabar/rechazo

function solicitarPartida($conn, $user1, $user2) {
    $solicitante = cifrar($user1);
    $solicitado = cifrar($user2);

  
   

    // Comprobar si hay partida en curso

    $com = $conn->prepare("SELECT UsuarioA FROM PARTIDAS WHERE (UsuarioA = ? AND UsuarioB = ? AND Finalizado = 0) OR (UsuarioA = ? AND UsuarioB = ? AND Finalizado = 0)");       
    $com->bind_Param('ssss', $solicitante, $solicitado, $solicitado, $solicitante);
    $com->execute();
    $com->store_result();
    $com->bind_result($respuesta);

    $val = !$com->fetch();
    $com->close();  
    
    if ($val) {

    // Quitar la partida anterior (si habia)

        $com = $conn->prepare("DELETE FROM PARTIDAS WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?) "); 
        $com->bind_Param('ssss', $solicitante, $solicitado, $solicitado, $solicitante);
        $com->execute();
        $com->close();
         
        // Añadir la nueva partida

        $com = $conn->prepare("INSERT INTO PARTIDAS VALUES (?, ?, 0, 1, 0, '---------')");     
        $com->bind_Param('ss', $solicitante, $solicitado);
        $com->execute();
        $com->close();

        // Todo correcto, mandar la notif a firebase
        notificarAFirebase(2, $user1, $user2);

    }

    $resultados = array(
        'respuesta' => $val,
    );

    echo json_encode($resultados);

}

function aceptarPartida($conn, $user1, $user2) {
    $solicitante = cifrar($user2);
    $solicitado = cifrar($user1);

    $com = $conn->prepare("UPDATE PARTIDAS SET Aceptado = 1 WHERE UsuarioA = ? AND UsuarioB = ?");          
    $com->bind_Param('ss', $solicitante, $solicitado);
    $com->execute();
    $com->close();

    // Notificar a firebase
    notificarAFirebase(3, $user1, $user2);


}

function obtenerPartidas($conn, $nombre) {
    $user = cifrar($nombre);
//$com = $conn->prepare("(SELECT UsuarioA AS Oponente, TurnoDeA+3, Aceptado FROM PARTIDAS WHERE UsuarioB = ?) ORDER BY Aceptado DESC, Oponente ASC");
    $com = $conn->prepare("(SELECT UsuarioA AS Oponente, TurnoDeA+3, Aceptado FROM PARTIDAS WHERE UsuarioB = ? AND Finalizado = 0) UNION (SELECT UsuarioB AS Oponente, TurnoDeA, Aceptado FROM PARTIDAS WHERE UsuarioA = ? AND Aceptado = 1 AND Finalizado = 0) ORDER BY Aceptado DESC, Oponente ASC");
    $com->bind_Param('ss', $user, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($oponente, $datoTurno, $aceptado);
    
    $listaOponentes = array();
    $listaEstados = array();

    while ($com->fetch()) {
        array_push($listaOponentes, descifrar($oponente));
        array_push($listaEstados, $aceptado * (($datoTurno % 2) + 1)); // 0 peticiones, 1 turno del otro, 2 mi turno

    }

    $com->close();

    $resultados = array(
        'oponentes' => $listaOponentes,
        'estados' => $listaEstados


    );

    echo json_encode($resultados);          
    


}


function obtenerGamestate($conn, $nombre, $contrario) {
    $user = cifrar($nombre);
    $contrario = cifrar($contrario);

    $com = $conn->prepare("(SELECT Tablero, 'X', Finalizado FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?) UNION (SELECT Tablero, 'O', Finalizado FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?)");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($tablero, $figura, $finalizado);

    $com->fetch();
    $com->close();

    
    $resultados = array(
        'tablero' => $tablero,
        'miFigura' => $figura,
        'finalizado' => $finalizado == "1"

    );

    echo json_encode($resultados);          

}

function realizarJugada($conn, $nombre, $contra, $tablero) {

    $user = cifrar($nombre);
    $contrario = cifrar($contra);

    // Safety check para comprobar que sí es tu turno (debería serlo si se llama aquí)
    // pero por si justo dos dispositivos piden a la vez)

    $com = $conn->prepare("(SELECT TurnoDeA FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?) UNION (SELECT TurnoDeA+1 FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?)");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($turno);
    $com->fetch();
    $com->close();  
    if (($turno % 2 == 1)) {
        $com = $conn->prepare("UPDATE PARTIDAS SET Tablero = ? WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?) ");
        $com->bind_Param('sssss', $tablero, $user, $contrario, $contrario, $user);
        $com->execute();
        $com->close();
        // Notificar a firebase

        notificarAFirebase(4, $nombre, $contra);

       // Cambiar el turno
       $com = $conn->prepare("UPDATE PARTIDAS SET TurnoDeA = MOD(TurnoDeA+1, 2) WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?)");
       $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
       $com->execute();
   
    }
   $com->close();
}

function quitarPartida($conn, $nombre, $contra, $statusPartida) {
  /*  $user = cifrar($nombre);
    $contrario = cifrar($contra);
    $com = $conn->prepare("DELETE FROM PARTIDAS WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?) ");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->close();
*/

    // statusPartida ->  0 (no se empezó), 1 (draw), 2 ("nombre" gana)


    switch ($statusPartida) {

	case "0":
   	    $user = cifrar($nombre);
            $contrario = cifrar($contra);
            $com = $conn->prepare("DELETE FROM PARTIDAS WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?) ");
            $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
	    $com->execute();
	    $com->close();
    // Notificar a firebase si fue el fin de una partida que se había empezado
            break;
       
        case "1":
            notificarAFirebase(5, $nombre, $contra);
            break;
       
         case "2":
            notificarAFirebase(6, $nombre, $contra);

    }
}

function estadoSolicitudPartida($conn, $nombre, $contrario) {

   // Post: 0 -> No game, 1 -> Pide rival, esperando, 2-> Pide yo, esperando, 3->  aceptado

    $user = cifrar($nombre);
    $contrario = cifrar($contrario);

    $com = $conn->prepare("(SELECT '2', Aceptado FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ? AND Finalizado = 0) UNION (SELECT '1', Aceptado FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ? AND Finalizado = 0)");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($val, $aceptado);

    if (!$com->fetch()) {
        $val = 0;
    } else if ($aceptado == 1) {
        $val = 3;
    }

    $com->close();  

    $resultados = array(
        'respuesta' => intval($val)
    );

    echo json_encode($resultados);

}


function miTurno($conn, $nombre, $contrario) {

    $user = cifrar($nombre);
    $contrario = cifrar($contrario);

    $com = $conn->prepare("(SELECT TurnoDeA FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?) UNION (SELECT TurnoDeA+1 FROM PARTIDAS WHERE UsuarioA = ? AND UsuarioB = ?)");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($turno);
    $com->fetch();
    $com->close();  

    $resultados = array(
        'respuesta' => ($turno % 2 == 1),
    );

    echo json_encode($resultados);


}

function finalizarPartida($conn, $nombre, $contra) {

    $user = cifrar($nombre);
    $contrario = cifrar($contra);

    $com = $conn->prepare("UPDATE PARTIDAS SET Finalizado = 1 WHERE (UsuarioA = ? AND UsuarioB = ?) OR (UsuarioA = ? AND UsuarioB = ?)");
    $com->bind_Param('ssss', $user, $contrario, $contrario, $user);
    $com->execute();
    $com->close();  
    notificarAFirebase(4, $nombre, $contra);
}

?>
