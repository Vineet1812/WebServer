# WebServer

A multi-threaded (e.g. file-based) web server with thread-pooling implemented in Java. **This repository is a fork from [ibogomolov/WebServer](https://github.com/ibogomolov/WebServer).**  I extended this project and documented the code.  

---

## I have done the following changes  

- Documented the code.  
- Created a license (MIT-license)  
- Changed the **README.md**  
- Verification of the command-line arguments in the main(...)-method. Checks whether the command-line arguments are in the proper form. Otherwise it raise a certain error-message.  
- I changes only the **.java**-files and the binaries **.class** files and the **.jar** file.     

---

## Usage 

You simply type in the console:  

```sh
./WebServer.jar
```

This starts the web server under [http://localhost:8080](http://localhost:8080) with working-directory **wwwroot**.  
In addition you can given some command-line arguments:  

```sh
./WebServer.jar <port> <working-directory> <max number of threads>
```

It must be three arguments in the order above.  
