Red Musical
==================
Proyecto 2 y 3 de Redes
------------------------

El proyecto consiste en generar una libreria compartida de musica, que permita: Consultar que amigos estan Conectados, descargar musica de los Amigos conectados y los Amigos conectados de ellos.

Se utiliza un Grafo implicito ( Cada Contacto Representa un Nodo y la lista de amigos representa la conexiones entre nodos ) por esto los algoritmos de Busqueda en el grafo detectan y anulan ciclos.

Se permiten busquedas por Autor, Titulo, Genero, Año, Album, Numero o Todo

Para reconocer la informacion de cada Archivo de audio y realizar busquedas sobre esta informacion se utliza una la libreria entagged-audioformats-0.15.jar

Se carga un Servidor en cada Nodo de la red y el cliente es el que se conecta a este servidor para buscar y descargar las canciones

La interfaz de ambos en por Consola

Diferencias
-----------

El proyecto 2 utiliza interfaz de Sockets en Java

El proyecto 3 utiliza Java RMI envez de Sockets

Integrantes
-----------
	Jesus Torres
	Julio Lopez


