<?php
include './extras/cabecera.php';

$id = $_GET['id'];

switch ($id) {

    case "0":
        insertarUsuario($conn, $_GET['dato1'], $_GET['dato2']);
        break;

    case "1":
        comprobarCredenciales($conn, $_GET['dato1'], $_GET['dato2']);
        break;
	
    case "2":
	    setTokenEnUsuario($conn, $_GET['dato1'], $_GET['dato2']);
	    break;

    case "3":

        subirFotoDePerfil($conn, $_GET['dato1'], $_GET['dato2']);
        break;

    case "4":
        bajarFotoDePerfil($conn, $_GET['dato1']);
        break;

    // Exclusivo llamado desde el servidor

    case "5":
        obtenerTokensDeCuenta($conn, $_GET['dato1']);


}

$conn->close();

function insertarUsuario($conn, $nombre, $pass) {

    $user = cifrar($nombre);

    $com = $conn->prepare("SELECT Nombre FROM USUARIOS WHERE Nombre = ?");	    
    $com->bind_Param('s', $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($nombre);

    if (!$com->fetch()) { 
        $com->close();
        $salt = generarSalt();
        $contra = hashValor($pass, $salt);
        $salt = cifrar($salt);
        $com = $conn->prepare("INSERT INTO USUARIOS(Nombre, Contraseña, SaltContraseña) VALUES(?, ?, ?)");	    
        $com->bind_Param('sss', $user, $contra, $salt);
        $com->execute();
        $insertado = true;
    } else {
        $insertado = false;
    }

    $com->close();

    $resultados = array(
        'respuesta' => $insertado
    );

    echo json_encode($resultados);


}

function comprobarCredenciales($conn, $nombre, $cont) {

    $user = cifrar($nombre);

    $com = $conn->prepare("SELECT Contraseña, SaltContraseña FROM USUARIOS WHERE Nombre = ?");	    

    $com->bind_Param('s', $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($pass, $passSalt);


    if ($com->fetch()) {
        $passSalt = descifrar($passSalt);
        $contHash = hashValor($cont, $passSalt);

        $val = $contHash == $pass;
        
    } else {
        $val = false;
    }

    $com->close();

    $resultados = array(
        'respuesta' => $val
    );

    echo json_encode($resultados);


}

function setTokenEnUsuario($conn, $nombre, $token) {
    
    $user = cifrar($nombre);

    $com = $conn->prepare("SELECT Cuenta FROM DISPOSITIVOS WHERE Token = ?");	    
    $com->bind_Param('s', $token);
    $com->execute();
    $com->store_result();
    $com->bind_result($cuentaAntigua);



    if (!$com->fetch()) {
        $com->close();
        $com = $conn->prepare("INSERT INTO DISPOSITIVOS VALUES(?,?)");	  
        $com->bind_Param('ss', $token, $user);
        $com->execute();

    } else if ($user != $cuentaAntigua) {
        $com->close();
        $com = $conn->prepare("UPDATE DISPOSITIVOS SET Cuenta = ? WHERE Token = ?");	  
        $com->bind_Param('ss', $user, $token);
        $com->execute();
    }


    $com->close();

}

function subirFotoDePerfil($conn, $nombre, $foto) {

    $user = cifrar($nombre);
    $com = $conn->prepare("UPDATE USUARIOS SET Foto = ? WHERE Nombre = ?");
    $com->bind_Param('ss', $foto, $user);
    $com->execute();
    $com->close();

}

function bajarFotoDePerfil($conn, $nombre) {

    $user = cifrar($nombre);
    $com = $conn->prepare("SELECT Foto FROM USUARIOS WHERE Nombre = ?");       
    $com->bind_Param('s', $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($foto);
    $com->fetch();
    $com->close();

    $resultados = array(
        'foto' => base64_decode($foto)
    );

    echo json_encode($resultados);
 
}

function obtenerTokensDeCuenta($conn, $nombre) {
    $user = cifrar($nombre);
    $com = $conn->prepare("SELECT Token FROM DISPOSITIVOS WHERE Cuenta = ?");       
    $com->bind_Param('s', $user);
    $com->execute();
    $com->store_result();
    $com->bind_result($token);
    $dispositivos = array();

    while ($com->fetch()) {
        array_push($dispositivos, $token);
    }

    $com->close();

    $resultados = array(
        'tokens' => $dispositivos
    );
    echo json_encode($resultados);
}



?>
