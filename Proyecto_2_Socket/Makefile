all: Nodo Cliente

Cliente: Cliente.o
	gcj --main=Cliente  Cliente.o -o Cliente ./jl1.0.1.o

Cliente.o: Cliente.java
	gcj -c  Cliente.java -o Cliente.o --classpath=./jl1.0.1.jar

Nodo: Nodo.o
	gcj --main=Nodo  Nodo.o -o Nodo /net/raquella/ldc/redes/nanoxml/gcj/nanoxml-lite-2.2.3.o

Nodo.o: Nodo.java
	gcj -c  Nodo.java -o Nodo.o --classpath=.:/net/raquella/ldc/redes/nanoxml/java/nanoxml-lite-2.2.3.jar

