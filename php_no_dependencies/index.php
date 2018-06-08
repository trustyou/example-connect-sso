<?php                                                                          

//These parameters need replacement:
// Currently, the system supports PUT and GET (GET is suggested)
$method = "GET";                                                

// Replace this with your own partner_id
$partner_id = "local_test_partner_1";   

// Replace this with the chosen hotel's id
$hotel_id =  "hotel_1";                   

// Replace this with the URL pointing to the desired environment
$sso_endpoint = "https://analytics.integration.trustyou.com/connect/log_in";    

// Replace this with a path to your own PEM private key
$priv_key_path = "file:///path/to/priv_vi1_local_test_partner_1.pem";   
                                                                               
$priv_key = openssl_get_privatekey($priv_key_path);                            
$base_string = "" . $method;

$datetime = new DateTime();
$query_params = array(
	"oauth_signature_method" => "RSA-SHA1",
	"oauth_version" => "1.0",
	"oauth_token" => $hotel_id,
	"partner_id" => $partner_id,
	"oauth_consumer_key" => $partner_id,
	
	// Something like this can be used
	// "oauth_timestamp" => $datetime->getTimestamp(),
	
	// These values for test purposes only; Replace them with a proper timestamp and
	// for the nonce any random string will do
	"oauth_timestamp" => "1465682782",
	"oauth_nonce" => "55900245497468237011465682782",
);

ksort($query_params);

echo "<h3>The sorted parameter array:</h3>";
echo "<ul>";

$param_strings = array();
foreach($query_params as $k => $v){
	echo "<li>" . $k . "  -  " . $v . "</li>";
	array_push($param_strings, (urlencode($k . "=" .$v)));
};
echo "</ul>";

$base_string_param_component = join(urlencode("&"), $param_strings);
$base_string = join("&", array($method, urlencode($sso_endpoint), $base_string_param_component));
$unencoded_signature = "";
echo "<hr/>";
echo "<h3>Base string to be signed: (built as explained <a href=\"http://oauth.net/core/1.0/#anchor30\">here</a>)</h3>" . $base_string;

openssl_sign($base_string, $unencoded_signature, $priv_key, OPENSSL_ALGO_SHA1);
echo "<hr/>";                                                                  
echo "<h3>The signed (but unencoded) string:</h3>" . $unencoded_signature;

echo "<hr/>";                                                                  

echo "<h3>Private key path </h3>" . $priv_key_path;

echo "<hr>";                                                                   

/*                                                                             
    if we have                                                                 
GET                                                                            
http://asdf.com/asdf/zxcv?oauth_anything=b&oauth_something=a                                                              

    then the base string is. Important detail: when building this string, the query params must be sorted by key 
    (and if multiple identical keys are present, then the entryes should be sorted by value)

    GET&http%3A%2F%2Fasdf.com%2Fasdf%2Fzxcv&oauth_anything=b&oauth_something=a

    and it must be signed, then base64 encoded and then urlencoded.

	This process is described in the oauth1.0 signing process (example):
	http://oauth.net/core/1.0/#anchor30

*/                                                                             

$signature = urlencode(base64_encode($unencoded_signature));
echo "<h3>The resulted unencoded_signature </h3>" . $unencoded_signature;                              
echo "<hr/>";                                                                  
echo "<hr/><h3>The resulted encoded signature: </h3>" .$signature; 

echo "<hr/>";
echo "<h3>Query params:</h3> " . $query_string = http_build_query($query_params);
echo "<hr/>";
echo "<h3 style=\"color: #8AE32B\">The result: call this URL with HTTP method you chose, and you will be authentified with TrustYou Connect: </h3>";
echo $sso_endpoint . "?" . $query_string . "&" . "oauth_signature=" . $signature;
?>

