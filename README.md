# P2PFileSharer

UDP based Peer-To-Peer File Sharing System

Requirements: 
Make sure you have JAVA version 7 installed.
Set JAVA_HOME variable point to JAVA version 7 installation directory.

How to compile? (Linux)

Execute compile-bootstrap.sh to compile bootstrap server related java classes which will create the class files in bin/bootsrap directory.
Execute compile.sh to compile java classes which will create the class files in bin directory.

How to run? (Linux)
Parameters related to bootstrap server(ip and host), local node(ip, host, username) and node type(udp or rpc) can be configured via config.properties file. Make sure they are configured correctly. We used node.iteration property to keep track of the iteration id in doing the performance analysis test such that we can evaluate queries based on iteration id.
Execute run-bootstrap.sh to run the Bootstrap Server(we have configured default boostrap server port as 5000).
Execute run.sh to run the P2P File Sharing System.
