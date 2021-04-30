# Scalable Tinder

## To Run the Full Application:

###  Initialize Databases
#### SQL
Run all Scripts in ./db/SQL in pgAdmin or posgreSQL shell
#### NoSQL
1- Run Main Method in ArnagoInstance file

### In Terminal:
1. In a new terminal shell, sudo gedit /etc/haproxy/haproxy.cfg, this will open a text editor 
2. In text editor, Replace the content of the opened file with /resources/haproxy.cfg , then save and close the text editor 
3. If encountered 404 error when using ```yay``` run these two commands ```sudo pacman -Syy``` and ```sudo pacman -Syu``` 
4. Run "startup" bash file in /Backend and mediaServerStartup.bash in /Backend 
5. In terminal, run "mvn install:install-file -Dfile=/home/vm/Desktop/commons-dbcp2-2.8.0-bin/commons-dbcp2-2.8.0/commons-dbcp2-2.8.0.jar -DgroupId=org.apache.commons -DartifactId=commons-dbcp2 -Dversion=2.8.0 -Dpackaging=jar"
6. In IntelliJ, load the maven dependencies from pom.xml (By Building the project)
7. Run "RunBackEnd.java".  


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
