<?php
if(isset($_POST['playerid'])){ //if there is a post variable, let's update the playerid cookie
setcookie("playerID",$_POST['playerid']);
}
?>
<html>
<iframe src="https://wall.superrewards.com/super/offers?h=trjdhzqwjtn.721137735254&uid=<?php echo $_COOKIE["playerID"];; ?>" frameborder="0" width="1350" height="2400" scrolling="no"></iframe>
</html>