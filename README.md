# Scalable Tinder

## To Run the Full Application:

### In Terminal:

1- sudo rabbitmq-server  
2- In a new terminal shell,  sudo gedit  /etc/haproxy/haproxy.cfg   
3- In text editor, Replace the content of haproxy.cfg with /resources/haproxy.cfg  
4- sudo haproxy -f /etc/haproxy/haproxy.cfg  
5- In IntelliJ, load the maven dependencies from pom.xml  
6- Run "RunBackEnd.java".  

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
"path":""  
}  
