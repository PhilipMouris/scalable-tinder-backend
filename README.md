# Scalable Tinder

## To Run the Full Application:

### In Terminal:
1- In text editor, Replace the content of haproxy.cfg with /resources/haproxy.cfg  
2- Run startup bash file in /Backend
3- In IntelliJ, load the maven dependencies from pom.xml  
4- Run "RunBackEnd.java".  


To Run Arangodb web interface go to http://localhost:8529 after running bash script (Username: root, password is empty)
For Postgresql run pgadmin username is postgres and password is vm, hostname is localhost
### The Load-Balancer Path is 127.0.0.1:90, all requests will be sent to this address.  

Sample Request:   
POST http://127.0.0.1:90  
{  
"command" : "SignIn",  
"application" : "User"  
}  

### Controller path is 127.0.0.1:8084.  

Sample Controller Request:  
POST http://127.0.0.1:8084  
{  
"command" : "freeze",  
"application" : "User",  
"param": "",  
"path":""   ,
"instance_num":"1"  
}  
