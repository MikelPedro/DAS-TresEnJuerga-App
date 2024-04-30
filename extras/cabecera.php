<?php

$DB_SERVER="34.163.130.62"; #la dirección del servidor
$DB_USER="root"; #el usuario para esa base de datos
$DB_PASS="123456"; #la clave para ese usuario
$DB_DATABASE="tresEnRaya"; #la base de datos a la que hay que conectarse
# Se establece la conexión:
$conn = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno()) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}


function cifrar($dato) {
	$puntero = fopen("clave.txt", "r");
	$clave = fgets($puntero, 50);
	$clave = hash('sha256', $clave);
	$iv = substr($clave, 0, 16);
	return base64_encode(openssl_encrypt($dato, 'AES-256-CBC', $clave, 0, $iv));
	

}

function descifrar($dato) {
	$puntero = fopen("clave.txt", "r");
	$clave = fgets($puntero, 50);
	$clave = hash('sha256', $clave);
	$iv = substr($clave, 0, 16);
	return openssl_decrypt(base64_decode($dato), 'AES-256-CBC', $clave, 0, $iv);

}

function generarSalt() {
    // Generar un salt de 10 letras generadas al azar para ponérselo a la contraseña

    $cont = 0;

    while ($cont < 10) {
        $h = $h.chr(random_int(65, 90));

        $cont++;
    }

    return $h;

}

function hashValor($cont, $salt) {


    // Añadir el salt a la contraseña en sí

    $contTotal = $cont.$salt;

    // Hashear la contraseña para impedir descifrado

    $g = hash('sha256', $contTotal, false);

}

?>