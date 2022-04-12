Server:
mvn clean
mvn compile
tcp:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TCPMain" -Dexec.args="7777"
reactor:
mvn exec:java - Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" - Dexec.args="7777 5"

client:
make clean
make
./bin/BGSClient 127.0.0.1 7777
 
Fillterd words:
Server/src/main/java/bgu/spl/net/srv/DataBase
In the constractor.
