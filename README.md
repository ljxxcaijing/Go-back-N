# Go-back-N
- Group member: Jing Cai(jcai3), Weiran Fu(wfu4)  
- Implement the Go-back-N automatic repeat request (ARQ) scheme and carry out a number of experiments to evaluate its performance. In the process I expect that you will develop a good understanding of
ARQ schemes and reliable data transfer protocols and build a number of fundamental skills related to writing transport layer services.

+ First, compile java file   
```javac ./src/GBN/*.java```  
+ Use terminal to start a Server  
```java -cp "./src/" GBN.Server port# file-name p ```  
For example```java -cp "./src/" GBN.Server 7735 data/result.txt 0.01 ```  
+ New a terminal to start a Client  
```java -cp "./src/" GBN.Client server-host-name server-port# file-name N MSS ```   
For example```java -cp "./src/" GBN.Client appledeMBP.lan 7735 data/file.txt 4 100```

+ [Report](Report.pdf) for this project
+ [Data and plot](task_plot.xlsx) in the report