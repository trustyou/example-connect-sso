TrustYou Connect Service
------------------------
This script shows an example of how to use the TY Connect service in python, with the
library oauthlib

Requirements
------------

Requires Python 2.7, 3.4 or 3.5
```
$ pip install -r requirements.txt
```

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

For creating the private key, check out the README.md at the root folder of this repo.
