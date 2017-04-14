<?php

// Connection Variables
   $host        = "host=DATABASEHOSTHERE";
   $port        = "port=DATABASEPORTHERE";
   $dbname      = "dbname=DATABASENAMEHERE";
   $credentials = "user=USERHERE password=PASSWORDHERE";
   $sql 		= "";
   $current_time = strtotime("now"); //current time variable
   
// Open database
   $db = pg_connect( "$host $port $dbname $credentials"  );
   if(!$db){
      echo "Error : Unable to open database.\n";
   } else {
      echo "Opened database successfully.\n";
   }

  $item 		= mysql_real_escape_string(stripslashes($_POST['item'])); //example: 25bars
  $playerid 		= mysql_real_escape_string(stripslashes($_POST['playerid'])); //example: 5345
				
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
?>
