# DNSResolver
The DNSResolver is a tool to query dns Records. While searching domains, this tool
also requests the whois data for the specific domain. If the input is an ip-address then it will look up
the data of the owner (ISP).

![interface Look](interface.png)

## features

- Domain query
- Whois lookup
- ip check

## Interface
There is a light and dark mode available. Switchable with the moon Icon.

## Whois Servers
The Whois-Servers are pulled automatically from ``whois.iana.org`` 
after the first pull they get cached to minimize the requests. The Cache duration can
be defined in the settings.