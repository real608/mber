## Cash-Shop
This program uses the PayPal IPN system to validate payments.
We utilize 2 web-servers for this. One from a port 80 webserver (since IPN only allows port 80)
and one from a port 82 server. The port 80 webserver 
is a webserver provided by 000webhost. The port 82 server is our main Synced Online webserver in the 
dedicated server.

## How the server communication works
- The player buys an item from the cashshop, and the PayPal IPN is triggered in port 80
- The PayPal IPN validates the payment in port 80
- Once validated, it will send post variables through CURL to the BillingSuccess.php in port 82
- In the BillingSuccess.php it will update the SQL database with the purchased items in port 82