package
{
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.net.URLRequestMethod;
	import flash.display.MovieClip;

	/**
	* PayPal the main class for making paypal requests
	* @author Khaled Garbaya khaledgarbaya@gmail.com
	*/
	public class PayPal extends MovieClip
	{
		/**
		* @private
		**/
		private static  var urlRequest:URLRequest = new URLRequest;
		
		public function PayPal()
		{
			trace("Success - PayPal class has been instantiated.");
		}
		
		/**
		* Make PayPal Request
		* @param paypalRequest PayPalRequest 
		* @param sandbox Boolean default false when you change it to true the class will redirect you to
		* http://sandbox.paypal.... so you can use test accounts to do fake payment, don't forget to change it back to false
		* when you publish your app in production server
		*/
		public static function makeRequest( paypalRequest:PayPalRequest, sandbox:Boolean=false ) : void
		{
			urlRequest.url =  sandbox ? PayPalRequest.SANDBOX_API_URL : PayPalRequest.API_URL;
			urlRequest.data  = paypalRequest.getUrlVariables();
			urlRequest.method = URLRequestMethod.POST;
			
			navigateToURL( urlRequest , "_blank" );
		}
	}

}