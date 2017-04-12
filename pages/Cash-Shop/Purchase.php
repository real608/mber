<?php
//Connection Variables
   $host        = "host=DATABASEHOSTHERE";
   $port        = "port=DATABASEPORTHERE";
   $dbname      = "dbname=DATABASENAMEHERE";
   $credentials = "user=USERHERE password=PASSWORDHERE";
   
//Receive Variables from Flash
   $CharacterID = mysql_real_escape_string(stripslashes($_POST['CharacterID']));
   $PurchaseAmount = mysql_real_escape_string(stripslashes($_POST['PurchaseAmount']));
   $MonthAmount = mysql_real_escape_string(stripslashes($_POST['MonthAmount']));
   $Coins = mysql_real_escape_string(stripslashes($_POST['Coins']));
   $Subscription = true; //At all times just make this true... This is because of an error we had with Subscription not posting

//Connect to the Database
   $db = pg_connect( "$host $port $dbname $credentials"  );
   if(!$db){
      echo "Error : Unable to open database.\n";
   } else {
      echo "Opened database successfully.\n";
   }
   
//Update the coins of the player if we're buying coins
if($Coins == true)
{
      $sql =<<<EOF
      UPDATE "MemberAccountRecord" set coins = coins + $PurchaseAmount where "memberId"=$CharacterID;
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

//Add synchronized status if we're buying a subscription
if($Subscription == true)
{
$current_time = strtotime("now"); //Current time variable

//Get the # of months of subscription that the player bought
if($MonthAmount == 1)
{
$subscribe_time = date("Y-m-d", strtotime("+1 month", $current_time)); //1 Month
} else if($MonthAmount == 3)
{
$subscribe_time = date("Y-m-d", strtotime("+3 months", $current_time)); //3 Months
} else if($MonthAmount == 6)
{
$subscribe_time = date("Y-m-d", strtotime("+6 months", $current_time)); //6 Months
} else if($MonthAmount == 12)
{
$subscribe_time = date("Y-m-d", strtotime("+1 year", $current_time)); //12 Months (1 Year)
}
	
   $sql =<<<EOF
      INSERT INTO "BarscriptionRecord" ("memberId",expires) VALUES ($CharacterID, '$subscribe_time');
      UPDATE "MemberRecord" set flags = 4096 where "memberId"=$CharacterID;
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

//Close the database
   pg_close($db);
?>
