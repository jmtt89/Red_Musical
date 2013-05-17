JCC = javac
JFLAGS = -g

default: InterfaceRemota.class Cliente.class Nodo.class Servidor.class Mp3.class Servidor_Stub.class

Servidor_Stub.class: 
				rmic Servidor

InterfaceRemota.class: InterfaceRemota.java
				$(JCC) $(JFLAGS) InterfaceRemota.java
               
Cliente.class: Cliente.java
				$(JCC) $(JFLAGS) Cliente.java
               
Nodo.class: Nodo.java
				$(JCC) $(JFLAGS) -classpath .:/net/raquella/ldc/redes/nanoxml/java/nanoxml-lite-2.2.3.jar:./entagged-audioformats-0.15.jar:./Mp3.java Nodo.java

Servidor.class: Servidor.java
				$(JCC) $(JFLAGS) -classpath .:/net/raquella/ldc/redes/nanoxml/java/nanoxml-lite-2.2.3.jar Servidor.java

Mp3.class: Mp3.java
				$(JCC) $(JFLAGS) Mp3.java

clean: 
				$(RM) *.class