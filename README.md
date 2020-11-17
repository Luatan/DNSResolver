# DNSResolver

Base Functionality
-
Logic
 -  
    [x] DNS Resolving
        [x] add CNAME (Because im stupid)
        [x] get DNS-Records by type
        [x] Get all DNS-Records (Any) at the same time
        [x] if subdomain is chosen, also get records from main domain
    [x] Error handling
        [x] Unknown host Handling
        [ ] Unknown Host Handling on Windows 7
    [ ] add domain checker for domain registration
        [x] extension checker class 
        [x] most important tld which whois.com supports
        [ ] .ch, .de, .li, .swiss
GUI
-   
    [x] Make a querry with The GUI
        [x] Possible to choose which record you want
        [x] Enter domain name
    [x] Copy the Output directly to the clipboard
    [x] Button to query main Domain (click on Hostname) 
    [x] Nameserver on top and not only in the results
    [x] Scroll automaticly to the top (/w txtAreaRecords.home() function)
        [x] make a Button to scroll up
    [x] add Interface for domain checker

Additional Features  
-   
    [x] insert IP/Domain into text field by clicking on it
    [ ] Visual feedback after coping
    [ ] Zone Transfer?
    [ ] Improve "Any" query
    [x] Press Start if "Enter" is pressed
    [x] Button Show/Hide Empty Records Logic
        [x] add Button Show/Hide Empty
    [x] if Subdomain is in query use the main domain to get the nameservers
    [ ] improve showing Scroll Top Button
    [ ] Implement History (Log)
    [ ] Check at Input for Regex if it is a possible domain
