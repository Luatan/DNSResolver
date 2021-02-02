# DNSResolver

Base Functionality
-

Logic
-
- [x] DNS Resolving
- - [x] add CNAME (Because im stupid)
- - [x] get DNS-Records by type
- - [x] Get all DNS-Records (Any) at the same time
- [x] if subdomain is chosen, also get records from main domain
- [x] Error handling
- - [x] Unknown host Handling
- - [x] Unknown Host Handling on Windows 7
- [x] Implement Whois
- - [x] extension checker class 
- - [x] most important tld which whois.com supports
- - [x] .swiss
- - [x] .de
- - [x] API to call for whois
- - - [x] .ch, .li
 
GUI
-   
- [x] Make a query with The GUI
- - [x] Possible to choose which record you want
- - [x] Enter domain name
- [x] Copy the Output directly to the clipboard
- [x] Button to query the main Domain (click on Hostname) 
- [x] Nameserver on top and not only in the results
- [x] Scroll automatically to the top (/w txtAreaRecords.home() function)
- - [x] make a Button to scroll up
- [x] add Interface for domain checker

Additional Features  
-   
- [x] insert IP/Domain into text field by clicking on it
- [ ] Visual feedback after coping
- [x] Press Start if "Enter" is pressed
- [x] Button Show/Hide Empty Records Logic
- - [x] add Button Show/Hide Empty
- [x] if Subdomain is in query use the main domain to get the nameservers
- [ ] improve showing Scroll Top Button
- [ ] Implement History (Log)
- [ ] Check at Input for Regex if it is a possible domain
- [x] Check for online/offline Status domain
- [x] Get rid of spaces (Domain Input)
