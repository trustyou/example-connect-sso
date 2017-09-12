TrustYou Connect Service
------------------------
This repository contains a series of examples of how to authenticate with 
the TrustYou Connect service

At the moment there are Python and PHP examples, but others might follow.

The examples here should not be considered a library. Use these only as templates
when writing your own code.

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

Some programming languages work better with different key types. For the java example for instance, we managed to best use the .der format. After you have generated the .pem files, these can easily be converted to .der format with these commands

```
$ # This converts a private PEM file to a private DER file
$ openssl pkcs8 -topk8 -inform PEM -outform DER -in ./my_private_key.pem -out ./my_private.der -nocrypt

$ # This creates a public DER file from a private PEM file
$ openssl rsa -in ./private.pem -outform DER -pubout -out ./public.der
```
