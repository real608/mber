<!DOCTYPE html>
<html>
<head>
</head>
<body>
<?php
if(isDomainAvailible('http://www.syncedonline.com'))
{
	echo "We are online folks checking again in rougly 10 seconds......";
	echo "<script>setTimeout(location.reload.bind(location), 10000);</script>";
}else{
	echo "We are dead folks restarting script will resume in rougly 1 minute......</br>";
	system("/home/desktop/Desktop/stopServer");
	echo "<script>setTimeout(location.reload.bind(location), 60000);</script>";
}

function isDomainAvailible($domain)
       {
               //check, if a valid url is provided
               if(!filter_var($domain, FILTER_VALIDATE_URL))
               {
                       return false;
               }

               //initialize curl
               $curlInit = curl_init($domain);
               curl_setopt($curlInit,CURLOPT_CONNECTTIMEOUT,10);
               curl_setopt($curlInit,CURLOPT_HEADER,true);
               curl_setopt($curlInit,CURLOPT_NOBODY,true);
               curl_setopt($curlInit,CURLOPT_RETURNTRANSFER,true);

               //get answer
               $response = curl_exec($curlInit);

               curl_close($curlInit);

               if ($response) return true;

               return false;
       }
?>
	Notice this script should only be open by 1 client at a time! </br>

	.htaccess file should contain the following for security sake </br>

	&lt;Files "AutoRestart.php"&gt; </br>
		Order Deny,Allow </br>
		Allow from localhost 127.0.0.1 </br>
		Deny from all </br>
	&lt;/Files&gt;
</body>


</html>
