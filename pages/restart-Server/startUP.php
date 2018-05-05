<?php
	//check if synced online's server is online
    $url = 'http://www.syncedonline.com/';
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_NOBODY, true);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_exec($ch);
    $retcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
	
	if($_POST['hardReboot'] == "true")
	{
		system("startServer"); //start Synced bash script, an automatic script to start synced
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
		system("startServer"); //start Synced bash script, an automatic script to start synced
		exit("offline");
    }
	if (200==$retcode) { //this means we're just loading the php file not in flash, for security reasons
        // server is already online
		exit("online");
    } else {
        //server is offline
		exit("offline");
    }
?>
