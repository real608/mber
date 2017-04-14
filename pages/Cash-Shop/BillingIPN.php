<?php

//Connection Variables
   $host        = "host=DATABASEHOSTHERE";
   $port        = "port=DATABASEPORTHERE";
   $dbname      = "dbname=DATABASENAMEHERE";
   $credentials = "user=USERHERE password=PASSWORDHERE";
   
//Open database
   $db = pg_connect( "$host $port $dbname $credentials"  );
   if(!$db){
      echo "Error : Unable to open database.\n";
   } else {
      echo "Opened database successfully.\n";
   }
   
// PayPal's notification

header('HTTP/1.1 200 OK');

// Create the response we need to send back to PayPal for them to confirm that it's legit

$resp = 'cmd=_notify-validate';
foreach ($_POST as $parm => $var) 
	{
	$var = urlencode(stripslashes($var));
	$resp .= "&$parm=$var";
	}
	
// Extract the data PayPal IPN has sent us, into local variables 

  $item_name        = $_POST['item_name']; //bars example: 25bars_5345 AND subscription example: 1month_5345
  $item_number      = $_POST['item_number'];
  $payment_status   = $_POST['payment_status'];
  $payment_amount   = $_POST['mc_gross'];
  $payment_currency = $_POST['mc_currency'];
  $txn_id           = $_POST['txn_id'];
  $receiver_email   = $_POST['receiver_email'];
  $payer_email      = $_POST['payer_email'];
  $record_id	 	= $_POST['custom'];
  $purchase    		= explode("_", $item_name, 2); //split item_name into two strings
  
  $item 			= $purchase[0]; //example: 25bars
  $playerid 		= $purchase[1]; //example: 5345
 
// Get the HTTP header into a variable and send back the data we received so that PayPal can confirm it's genuine

$httphead = "POST /cgi-bin/webscr HTTP/1.0\r\n";
$httphead .= "Content-Type: application/x-www-form-urlencoded\r\n";
$httphead .= "Content-Length: " . strlen($resp) . "\r\n\r\n";
 
 // Now create a ="file handle" for writing to a URL to paypal.com on Port 443 (the IPN port)

$errno ='';
$errstr='';
 
$fh = fsockopen ('ssl://www.paypal.com', 443, $errno, $errstr, 30);

// Now send the data back to PayPal so it can tell us if the IPN notification was genuine
 
 if (!$fh) {
 
// Uh oh. This means that we have not been able to get thru to the PayPal server.  It's an HTTP failure
 } 
		   
// Connection opened, so spit back the response and get PayPal's view whether it was an authentic notification		   
		   
else {
           fputs ($fh, $httphead . $resp);
		   while (!feof($fh))
		{
			$readresp = fgets ($fh, 1024);
			if (strcmp ($readresp, "VERIFIED") == 0) 
			{
				$current_time = strtotime("now"); //current time variable in case we're purchasing a subscription
				//Payment notification was both genuine and verified!	
				
if($item == "10bars")
{
      $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 10 where "memberId"=$playerid;
EOF;
} else if($item == "25bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 25 where "memberId"=$playerid;
EOF;
} else if($item == "60bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 60 where "memberId"=$playerid;
EOF;
} else if($item == "115bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 115 where "memberId"=$playerid;
EOF;
} else if($item == "220bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 220 where "memberId"=$playerid;
EOF;
} else if($item == "330bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 330 where "memberId"=$playerid;
EOF;
} else if($item == "440bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 440 where "memberId"=$playerid;
EOF;
} else if($item == "550bars")
{
	  $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + 550 where "memberId"=$playerid;
EOF;
} else if($item == "1month")
{
   $subscribe_time = date("Y-m-d", strtotime("+1 month", $current_time)); //1 month
   
   $sql =<<<EOF
      INSERT INTO "BarscriptionRecord" ("memberId",expires) VALUES ($playerid, '$subscribe_time');
      UPDATE "MemberRecord" set flags = 4096 where "memberId"=$playerid;
EOF;
} else if($item == "3month")
{
   $subscribe_time = date("Y-m-d", strtotime("+3 month", $current_time)); //3 months
   
   $sql =<<<EOF
      INSERT INTO "BarscriptionRecord" ("memberId",expires) VALUES ($playerid, '$subscribe_time');
      UPDATE "MemberRecord" set flags = 4096 where "memberId"=$playerid;
EOF;
} else if($item == "6month")
{
   $subscribe_time = date("Y-m-d", strtotime("+1 month", $current_time)); //6 months
   
   $sql =<<<EOF
      INSERT INTO "BarscriptionRecord" ("memberId",expires) VALUES ($playerid, '$subscribe_time');
      UPDATE "MemberRecord" set flags = 4096 where "memberId"=$playerid;
EOF;
} else if($item == "12month")
{
   $subscribe_time = date("Y-m-d", strtotime("+12 month", $current_time)); //12 months
   
   $sql =<<<EOF
      INSERT INTO "BarscriptionRecord" ("memberId",expires) VALUES ($playerid, '$subscribe_time');
      UPDATE "MemberRecord" set flags = 4096 where "memberId"=$playerid;
EOF;
}
				
				//Update the sql query
				$ret = pg_query($db, $sql);
				if(!$ret){
				echo pg_last_error($db);
				exit;
				} else {
				echo "Record updated successfully\n";
				}
				
			  pg_close($db); //close the database
			} else if (strcmp ($readresp, "INVALID") == 0) 
			{
				//A hacking attempt?
			}
		}
fclose ($fh);
		}
?>