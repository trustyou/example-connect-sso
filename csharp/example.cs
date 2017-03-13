using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;

class SSOExample {
  static string GetUnixTimestamp(){
    long epochTicks = new DateTime(1970, 1, 1).Ticks;
    // This is the unix timestamp
    return (((DateTime.UtcNow.Ticks - epochTicks) / TimeSpan.TicksPerSecond)).ToString();
  }
  
  static string BuildBaseString(){
    string method = "GET";
    string partnerId = "local_test_partner_1";
    string hotelId = "hotel_1";
    string ssoEndpoint = "https://analytics.integration.trustyou.com/connect/log_in";
    
    //string unixTimeStamp = GetUnixTimestamp();  // example of how to get the unix timestamp
    string unixTimeStamp = "1465682782";  //hardcoding value for now
    
    Dictionary<string, string> paramsDict = new Dictionary<string, string>();
    paramsDict.Add("oauth_signature_method", "RSA-SHA1");
    paramsDict.Add("oauth_version", "1.0");
    paramsDict.Add("oauth_token", hotelId);
    paramsDict.Add("partner_id", partnerId);
    paramsDict.Add("oauth_timestamp", unixTimeStamp);
    paramsDict.Add("oauth_nonce", "55900245497468237011465682782");
    
    List<string> keyList = paramsDict.Keys.ToList();
    keyList.Sort();
    
    List<string> paramsList = new List<string>();
    
    foreach(string dictKey in keyList){
      paramsList.Add(System.Uri.EscapeDataString(dictKey + "=" + paramsDict[dictKey]));
    }
    
    string paramsString = string.Join(System.Uri.EscapeDataString("&"), paramsList);
    
    string baseString = method +"&";
    
    baseString += System.Uri.EscapeDataString(ssoEndpoint) + "&";
    baseString += paramsString;
    
    
    return baseString;
  }
  
  static void Main(string []args){

    
    byte[] privateKeyContent = GetPrivateKeyContents();
    
    string baseString = BuildBaseString();
    System.Console.WriteLine("The base string:");
    System.Console.WriteLine(baseString);
    
    string unencoded_signature = "";
    System.Console.WriteLine();
    
    byte []baseStringBytes = Encoding.ASCII.GetBytes(baseString);
    System.Console.WriteLine("\n\nThe ASCII bytes in the signature");
    System.Console.WriteLine(string.Join("", baseStringBytes));
    
    System.Console.WriteLine("Build the base string, now encoding the signature");
    // TODO - create the signature from the base string and the keys
    //byte []encoded_signature = SignBytes(baseStringBytes);
    
  }
  
  static byte[] SignBytes(byte[] bytesToSign){
    // http://stackoverflow.com/questions/243646/how-to-read-a-pem-rsa-private-key-from-net
    X509Certificate2 certificate;
    //try{
      certificate = new X509Certificate2(GetPrivateKeyContents());
    
    //}catch{
      //throw new CryptographicException("Can't create the cert from the bytes");
    //}
    
    RSACryptoServiceProvider RSAalg; // stackoverflow ansewrs This
    if (certificate.HasPrivateKey){
      RSAalg = (RSACryptoServiceProvider)certificate.PrivateKey;
    } else{
      System.Console.WriteLine("The fertificate doesn't have a private key");
    }
    
    throw new SystemException("asdf");
    
    
    
    RSAParameters key = RSAalg.ExportParameters(true);
    
    byte[] signedBytes;
    
    try{
      // i think we could use the already existing RSACryptoServiceProvider
      RSACryptoServiceProvider RSAalg2 = new RSACryptoServiceProvider();
      
      RSAalg2.ImportParameters(key);  
      return RSAalg2.SignData(bytesToSign, new SHA1CryptoServiceProvider());
    } catch(CryptographicException e){
      return null;
    }
  }
  
  static RSACryptoServiceProvider GetCryptoProvider(){
    byte[] privateKey ;
  }
  
  
  static byte[] GetPrivateKeyContents(){
    
    return Encoding.ASCII.GetBytes(@"-----BEGIN RSA PRIVATE KEY-----
......................................................................
......................................................................
......Fill in from your key, or read it from a file...................
......................................................................
......................................................................
-----END RSA PRIVATE KEY-----"); 
  }
}