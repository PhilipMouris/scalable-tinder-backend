# Scalable Tinder

## To Run the Full Application:

### In Terminal:
1- In text editor, Replace the content of haproxy.cfg with /resources/haproxy.cfg  
2- If encountered 404 error when using ```yay``` run these two commands ```sudo pacman -Syy``` and ```sudo pacman -Syu```

3- Run startup bash file in /Backend and mediaServerStartup in /Backend (If first time run bash minioDownload)

4- In IntelliJ, load the maven dependencies from pom.xml  
mvn install:install-file -Dfile=/home/vm/Desktop/commons-dbcp2-2.8.0-bin/commons-dbcp2-2.8.0/commons-dbcp2-2.8.0.jar -DgroupId=org.apache.commons -DartifactId=commons-dbcp2 -Dversion=2.8.0 -Dpackaging=jar

5- Run "RunBackEnd.java".  


To Run Arangodb web interface go to http://localhost:8529 after running bash script (Username: root, password is empty)

For Postgresql run pgadmin username is postgres and password is vm, hostname is localhost

### To initialize Databases
#### SQL
Run all Scripts in ./db/SQL in pgAdmin or posgreSQL shell
#### NoSQL
1- Run Main Method in ArnagoInstance file


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
