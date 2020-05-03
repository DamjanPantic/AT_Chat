# AT Chat

---
#### Author
- [@damjanpantic](https://github.com/DamjanPantic)

# Getting started

## Prerequisites:

The following should be installed in order to run the application

- Java 8
- Node ^12.8
- npm ^6.14
- Vue cli ^3.0
- WildFLy 11

## Building and running the application

### Running frontend
The frontend part of the application has been developed using Vue framework. 
```
# Navigate to the frontend directory
cd chat_front

# Install the dependecies 
npm install

# Run the frontend server 
npm run serve -- --port 8081
```

After running these commands the frontend application will be available at http://localhost:8081/ChatWAR/
* Note: all the functionalities will be available only after running the backend side as well *

### Running backend
Backend side of the application has been developed to run under WildFly server version 11.
It can be built via UI using Eclipse IDE: 
- Add WildFly server to eclipse environment.
- Import the ChatEAR project into workspace.
- Run it on the previously added WildFly server.

API should be available at http://localhost:8080/ChatWAR/

All the implemented functionalities would be now available via frontend.

#### Server-Server communication

To make this  possible  please change variabbles in this file `ConnectionManagerBean`

For master node:
-Set `master` variable to null
-Set `nodeName` variable to string with ip address concatenate with ":8080" or URL provided by ngrok

For non-master node:
-Set `master` variable to master nods ip address concatenate with ":8080" or URL provided by ngrok
-Set `nodeName` variable to string with ip address concatenate with ":8080" or URL provided by ngrok

These variable have to be set without `http://`.
