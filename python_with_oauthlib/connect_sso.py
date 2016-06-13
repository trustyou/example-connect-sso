#!/usr/bin/env python
"""
Make requests to the TrustYou connect service

This script is an example of client-side code that synchronizes the session of
a user within the TrustYou system, allowing the external user to log into TrustYou.
"""
import argparse
import logging
import sys
try:
    from urllib import urlencode
except ImportError:
    from urllib.parse import urlencode

# These modules are used in the commented sections.
# import random
# import string
# import time

from oauthlib import oauth1

# Notice no trailing slash
CONNECT_STAGING_URL = 'https://analytics.staging.trustyou.com/connect/log_in'


def parse_args():
    parser = argparse.ArgumentParser(
        'Create signed SSO request for TY Connect',
        formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument(
        'partner_id', type=str,
        help="Value use for both the 'partner_id' and 'oauth_consumer_key' query parameters")
    parser.add_argument(
        'hotel_id', type=str, help="The hotel id, used as the 'oauth_token' query parameter")
    parser.add_argument(
        'private_key', type=lambda path: open(path).read(),
        help="Your private key's file path. Must be a RSA key encoded in PEM format")
    parser.add_argument(
        '--connect-url', dest='connect_url', default=CONNECT_STAGING_URL, required=False,
        help="The URL of the TrustYou Connect service")
    parser.add_argument(
        '--http-method', dest='http_method', default="GET", required=False,
        help="The HTTP Method to call the endpoint with. Default GET")

    return parser.parse_args()


def generate_signed_request(partner_id, hotel_id, private_key, connect_url, method):
    # At this moment we don't accept multiple query sting parameters,
    # so a dict can be used.
    params = {
        'partner_id': partner_id,           # partner_id must be identical to oauth_consumer_key
        'oauth_token': hotel_id,

        # These 4 mandatory parameters are generated by the oauth1.Client during the signing
        # process already, so they will be automatically added.
        # They are only left here, as an example, if using another library instead of oauthlib.
        # 'oauth_consumer_key': partner_id,
        # 'oauth_timestamp': int(time.time()),
        # 'oauth_nonce': ''.join(random.choice(string.letters) for _ in range(15)),
        # 'oauth_signature_method': 'RSA-SHA1',
    }

    client = oauth1.Client(
        client_key=partner_id,   # this will be the value of 'oauth_consumer_key' in the signed url
        signature_method=oauth1.SIGNATURE_RSA,
        rsa_key=private_key,
        signature_type=oauth1.SIGNATURE_TYPE_QUERY
    )

    unsigned_url = connect_url + '?' + urlencode(params)

    url, headers, body = client.sign(unsigned_url, http_method=method)

    return url

if __name__ == '__main__':
    args = parse_args()

    logging.basicConfig(stream=sys.stderr, level=logging.INFO)

    request_url = generate_signed_request(
        args.partner_id, args.hotel_id, args.private_key, args.connect_url, http_method)

    logging.info('The request url: \n\n{}\n\n'.format(request_url))

