<?php

   $host        = "host=DATABASEHOSTHERE";
   $port        = "port=DATABASEPORTHERE";
   $dbname      = "dbname=DATABASENAMEHERE";
   $credentials = "user=USERHERE password=PASSWORDHERE";
   $sql 		= "";
   
   $db = pg_connect( "$host $port $dbname $credentials"  );
   if(!$db){
      echo "Error : Unable to open database.\n";
   } else {
      echo "Opened database successfully.\n";
   }
   
$SECRET_KEY     = "abcdef123456"; // secret api code for my app
$transaction_id = $_REQUEST['id']; //transaction id
$user_id        = $_REQUEST['uid']; //user id (synced player id)
$offer_id       = $_REQUEST['oid']; // completed offer or payment method
$new_currency   = $_REQUEST['new']; //number of in-game currency awarded
$hash_signature = $_REQUEST['sig']; //security hash to verify

$hash = md5($transaction_id.':'.$new_currency.':'.$user_id.':'.$SECRET_KEY);
if ($hash != $hash_signature) {
  // signature doesn't match, respond with a failure
  echo "0\n"; 
  exit;
}
// all good! update our database
      $sql =<<<EOF
      UPDATE "MemberAccountRecord" set bars = bars + $new_currency where "memberId"=$user_id;
EOF;

				//Update the sql query
				$ret = pg_query($db, $sql);
				if(!$ret){
				echo pg_last_error($db);
				exit;
				} else {
				echo "Record updated successfully\n";
				}
				
		pg_close($db); //close the database
echo "1\n";
?>