<?php

    $url = 'http://www.syncedonline.com/';
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_NOBODY, true);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_exec($ch);
    $retcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
	
	if($_POST['hardReboot'] == "true")
	{
		system("/home/desktop/Desktop/stopServer"); // shut down Synced command; we have an automatic script to start it up
		exit("offline");
	}
    
	if (200==$retcode && $_POST['serverRestart'] == "false") { //this means we only want to get the server status
        // server is already online
		exit("online");
    } else if ($_POST['getServerStatus'] == "false"){
        //server is offline
		exit("offline");
    }
	
    if (200==$retcode && $_POST['serverRestart'] == "true") { //this means we want to do the command, not just get the status
        // server is already online
		exit("online");
    } else if ($_POST['getServerStatus'] == "true"){
        // server was offline, so we're restarting it
		system("/home/desktop/Desktop/stopServer"); // shut down Synced command; we have an automatic script to start it up
		exit("offline");
    }
	if (200==$retcode) { //this means we're just loading the php file not in flash
        // server is already online
		exit("online");
    } else {
        //server is offline
		exit("offline");
    }
?>
