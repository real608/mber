<?php
// PayPal's notification (This program is in the billing page)

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
			   //Success! The purchase was validated. Let's send the post vars to the BillingSuccess.php
				
//set POST variables
$fields = array(
                  'item' => urlencode($item),
                  'playerid' => urlencode($playerid),
                );

//url-ify the data for the POST
foreach($fields as $key=>$value) { $fields_string .= $key.'='.$value.'&'; }
rtrim($fields_string, '&');

//open connection
$ch = curl_init();

//set the url, number of POST vars, POST data
curl_setopt($ch,CURLOPT_URL, 'http://syncedonline.com:82/BillingSuccess.php');
curl_setopt($ch,CURLOPT_POST, count($fields));
curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);

//execute post
$result = curl_exec($ch);

//close connection
curl_close($ch);

			} else if (strcmp ($readresp, "INVALID") == 0) 
			{
				//A hacking attempt?
			}
		}
fclose ($fh);
		}
?>