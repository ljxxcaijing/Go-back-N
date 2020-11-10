# Go-back-N

Implement the Go-back-N automatic repeat request (ARQ) scheme and carry out a number of experiments to evaluate its performance. In the process I expect that you will develop a good understanding of
ARQ schemes and reliable data transfer protocols and build a number of fundamental skills related to writing transport layer services.

First, compile java file   
```javac ./src/GBN/*.java```  
Use terminal to start a Server  
```java -cp "./src/" GBN.Server 7735 result.txt 0.01 ```  
New a terminal to start a Client  
```java -cp "./src/" GBN.Client appledeMBP.lan 7735 data/file.txt 4 100```