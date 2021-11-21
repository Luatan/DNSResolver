# DNSResolver
The DNSResolver is a tool to query dns Records from domains. While searching domains, this tool
also requests the whois data for the specific domain. If the input is an ip-address then it will look up
the data of the owner (ISP)

![interface Look](interface.png)

## features

- Domain query
- Whois lookup
- ip check

## Interface
There is a light and dark mode available. Switchable with the moon Icon.

## Whois Servers
For each domain extension, the whois server is saved in the file `app/config/whois_servers.json`.
Due to this it is possible to extend the whois and support more domain extensions.

