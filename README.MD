Name: Rhey Mark John Q. Casero
Student Number: 2012-69715
Section: B-4L

Description:
	This project enables to mimic the behavoir of TCP int UDP implemented in Java.
	
Installation:
	To install Java, type the following command to your machine:
	
	$ sudo apt-get update
	$ sudo apt-get install openjdk-6-jre  and/or  $ sudo apt-get install openjdk-6-jdk
	
Running:
	To run the project, open two(2) terminals and you must go to the directory where the project is and compile each file in each terminal.
		
		$ javac Server.java
		$ javac Client.java
		
	After that, run the compiled program in the two terminals using:
		$java Server
		$java Client
		
	You can now see the connection of the server and a client that starts using a threeway handshake and you can see the sending and receiving of packets.
	You can also see a packet dropping depending on the probabalities assigned to each packet.