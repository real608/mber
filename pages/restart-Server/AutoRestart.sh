while :
do
    nc -z -v -w5 syncedonline.com 80
    if [ "&?" = "0"
      echo "We are online foks"
      sleep 5
   else
    echo "We are offline folks"
    sudo fuser -n tcp -k 80
    sleep 60
  fi
done
