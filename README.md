TrustYou Connect Service
------------------------
This script shows an example of how to use the TY Connect service.

Requirements
------------

Requires Python 2.7, 3.4 or 3.5
oauthlib==1.0.3
PyJWT==1.4.0
cryptography==1.3.1

Example
-------
Run it like so

```
python connect_sso.py <partner_id> <hotel_id> <path/to/private/key>
```


Or so, specifying the URL of TY Connect

```
python connect_sso.py <partner_id> <hotel_id> <path/to/private/key> --connect-url <http://other.url.here>
```

 How to create the private-public key pairs
-------------------------------------------

Creating a key pair is easy. On a Linux system, simply perform the steps below twice, choose first "YOURNAME-testing-public.pem" and then a second time "YOURNAME-production-public.pem" as an output file name.

```
$ # First generate a private/public key pair
$ openssl genrsa -out YOURNAME-testing-private-public.pem 2048

$ # Then extract the public component
$ openssl rsa -in YOURNAME-testing-private-public.pem -outform PEM -pubout -out YOURNAME-testing-public.pem
```
After running these commands:

Share the YOURNAME-testing-public.pem and YOURNAME-production-public.pem file with TrustYou by email.
Share the private keys with your developer charged with developing the SSO integration. They need to be accessible from the SSO code to sign the requests to us. Make sure to keep these files secret - don't email them to us or any other third parties.
