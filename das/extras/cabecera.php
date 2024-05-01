<?php

$DB_SERVER="35.197.255.16"; #la dirección del servidor
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
	$puntero = fopen("./extras/clave.txt", "r");
	$clave = fgets($puntero, 50);
	$clave = hash('sha256', $clave);
	$iv = substr($clave, 0, 16);
	return base64_encode(openssl_encrypt($dato, 'AES-256-CBC', $clave, 0, $iv));
	

}

function descifrar($dato) {
	$puntero = fopen("./extras/clave.txt", "r");
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

    return hash('sha256', $contTotal, false);

}

function notificarAFirebase($id, $enviador, $target) {
    
    /*
        Eres X, otro Y
    
        Notif friend req   (X: Y te ha mandado) 
        Notif accept friened req (X: Y te ha aceptado)
        Notif req match 
        Notif accept match
        Notif play
        Notif end match (draw)
        Notf end match (loss)
    
    */
    
    $url = 'http://35.197.255.16:80/usuarios.php?id=5&&dato1='.strval($target);
	echo $url;    
    // Realizar la solicitud GET
    $tokens = file_get_contents($url);
    echo $tokens;
    $tokens = json_decode($tokens, true);
    
    switch ($id) {
        case "0":
            $titulo = "Nueva solicitud de amistad";
            $msgNotif = $target.": ".$enviador." te ha mandado una solicitud de amistad";
            break;
        case "1":
            $titulo = "Tienes un nuevo amigo";
            $msgNotif = $target.": ".$enviador." ha aceptado tu solicitud de amistad";
            break;
        case "2":
            $titulo = "Solicitud de partida";
            $msgNotif = $target.": ".$enviador." te ha retado a una partida";
            break;
        case "3":
            $titulo = "Solicitud de partida aceptada";
            $msgNotif = $target.": ".$enviador." ha aceptado la solicitud de partida";
            break;
        case "4":
            $titulo = "Oponente ha jugado";
            $msgNotif = $target.": ".$enviador." ha realizado una jugada en la partida";
            break;
        case "5":
            $titulo = "Empate";
            $msgNotif = $target.": ".$enviador." ha jugado y terminado la partida en empate";
            break;
        case "6":
            $titulo = "Derrota";
            $msgNotif = $target.":".$enviador." ha ganado la partida contra ti..." ;
            break;    
    }
    
        
    
    
    if (!empty($tokens['tokens'])) {
    
        $cabecera= array(
            'Authorization: key=AAAA4oLSdW0:APA91bHMRr6ABO7ECxWm85-rCeO5uqYdbzxmPRAze97ilEkarz1VQ7QiihP2GDcSHMo5tFF9D2Dq97ktFVJ-uixy08XIZInfZLt-2qi1UtEULW3AamjEwYX5mfeVZPFrx-NUD3J350Ra',
            'Content-Type: application/json'
            );
        $msg= array(
            'registration_ids'=> $tokens['tokens'],
        
                'notification' => array(
                    'body' => $msgNotif,
                    'title' => $titulo,
                    'icon' => 'checkbox_on_background'
                ),
                'data' => array(
                    'id' => $id,
                    'enviador' => $enviador,
                    'recibidor' => $target
                )
            
        );
        $msgJSON= json_encode ( $msg);
        
        $ch = curl_init(); #inicializar el handler de curl
        #indicar el destino de la petición, el servicio FCM de google
        curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
        #indicar que la conexión es de tipo POST
        curl_setopt( $ch, CURLOPT_POST, true );
        #agregar las cabeceras
        curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
        #Indicar que se desea recibir la respuesta a la conexión en forma de string
        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
        #agregar los datos de la petición en formato JSON
        curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
        #ejecutar la llamada
        $resultado= curl_exec( $ch );
        #cerrar el handler de curl
        curl_close( $ch );
        echo $resultado;        
        if (curl_errno($ch)) {
            print curl_error($ch);
        }
        
        
    }
    
}



?>
