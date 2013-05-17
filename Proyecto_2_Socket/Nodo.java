/**
 *      @file Nodo.java
 *      @brief Esta es la Implementacion del Nodo y NodoServidor que son los que se encargan de mantener la red de Conexiones
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 16/11/2011
 * 
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nanoxml.*;

class NodoServer implements Runnable{
	//Atributos del Nodo
    private int p;
    private InetAddress[] c;
    private String rB;
    private String ID;    
    
    //Control Interno
    private List<String> Visitado;
    String ID_Consulta;
    Socket NodoCliente;
    
    //Espacio de Trabajo de Cada Conexion
    private BufferedReader Entrada = null;
    private PrintWriter Salida = null;
    private String IDLinea = "";
    private String TipoConsulta = ""; //Buscar,Descargar,Revisar    
    
	/**
	* Constructor de la Clase NodoServer que se encarga de Cargar el Hilo que realizara el trabajo para cada Nodo
	*
	* @param p   Entero que representa el Puerto
	* @param c   Arreglo de InetAddress que guarda todos los conocidos de un nodo
	* @param rB  String que representa la direccion a la Biblioteca
	* @param ID  String que representa el ID del Nodo
	* @param Visitado Arreglo que contiene el nodo que lo visito en cada consulta para evitar ciclos
	*/
    public NodoServer(Socket Cliente,int p,InetAddress[] c,String rB, String ID,List<String> Visitado){
        NodoCliente=Cliente;
        this.ID = ID;
        this.c = c;
        this.p = p;
        this.rB = rB;
		this.Visitado = Visitado;
    }
	
	/**
	* Metodo Implementado que Realiza para Cada Hilo la Labor del servidor en Comunicacion con su Conectado
	* Es el que se encarga del manejo directo de las Solicitudes al Servidor y de organizar el trabajo
	*/		
	//@Override
    public void run(){
        try {
            Entrada =  new BufferedReader(new InputStreamReader(( NodoCliente.getInputStream())));


            //Lee La Primera Linea con La informacion de la conexion
            IDLinea = Entrada.readLine();
            //Lee la Segunda Linea con el tipo de Consulta
            TipoConsulta = Entrada.readLine();

            ID_Consulta = IDLinea.concat(ID); //ID de la Consulta para Evitar Ciclos				
			
            if(Visitado.contains(ID_Consulta)){ // Se encuentra un Ciclo, retorna la salida Vacia y cierra la conexion
				Salida  = new PrintWriter( NodoCliente.getOutputStream(),true );
                Salida.println("");
                NodoCliente.close();
            } else { //No hay ciclo Se Realiza lo que sea necesario en Este nodo

                if(TipoConsulta.equals("Buscar")) //En caso de que se Quiera Realizar una Busqueda
                        Buscar(Entrada);

                if(TipoConsulta.equals("Descargar")) //En Caso de que se quiera Realizar una Descarga
                        Descargar(Entrada);

                if(TipoConsulta.equals("Revisar")) //En Caso de que se quiera Realizar una Revision de Conectados
                        RevisarRed();

                NodoCliente.close();//Cierra la Conexion
            }

        } catch( Exception e ) {
            System.out.println( "Error en run" );
            System.out.println( e.getMessage() );
        }
    }
	
	/**
	* Pasa de una Lista de Arreglo de String al Buffer
	* el orden de lo que contiene el Buffer se Conoce por Construccion
	* Aunque no retorna nada pasa lo que necesita al Buffer para que lo lea el Solicitante
	*
	* @param Lista de arreglo de String que contiene lo que se guardara en el Buffer de salida
	*/		
    private void ListToBuffer(List<String[]> Lista){
        String[] Tmp;
        for(int i=0;i<Lista.size();i++){ //Mientras Hayan Elementos en la Lista
            Tmp = Lista.get(i);			
            Salida.println(Tmp[0]+"|"+Tmp[1]+"|"+Tmp[2]+"|"+Tmp[3]+"|"+Tmp[4]); //Se envia cada elemento separado por un "|"
        }
    }
    
	/**
	* Pasa del Buffer a una Lista de Arreglo de String el Contenido del Buffer
	* el orden de lo que contiene el Buffer se Conoce por Construccion
	*
	* @param Buff Buffer que se quiere pasar a la lista
	* @return Lista con un Arreglo de String Que contiene el contenido del Buffer		
	*/		
    private List<String[]> BufferToList(BufferedReader Buff){
	
        List<String[]> Resultado = new ArrayList<String[]>(); //Lista de Resultado
        String Linea; //String que llevara Cada Linea
        
        try{
            while ((Linea = Buff.readLine()) != null) //Mientras queden cosas en el Buffer
				if(Linea.length() > 0) //Si la Linea no es Vacia
					Resultado.add(Linea.split("\\|")); // Se separa lo que este separado por "|" una cosa en cada posicion del Arreglo
        }catch(Exception e){
            System.out.println( "Error en BufferToList" );
            System.out.println( e.getMessage() );
        }
        return Resultado;
    }
		 
	/**
	* Busca un String dado en un archivo XSPF dado y devuelve una lista de
	* String[], con los datos de las canciones que coincidan con el String.
	*
	* @param String Autor Nombre del autor de la canci?n.
	* @return Lista con un Arreglo de String Que contiene los datos de las
	* canciones.
	*/			 
	private List<String[]> BibliotecaLocalA(String Autor) throws Exception {
	List <String[]> list = new ArrayList<String[]>();
	XMLElement xml = new XMLElement();
	FileReader reader = new FileReader(rB);
	xml.parseFromReader(reader);
		Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
				String[] part = new String[5];
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
						part[4] = cont;
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
						part[1] = cont;
					}
					if(child2.getName().equals("creator")){
						if(child2.getContent().startsWith(Autor)){
							name = true;
						}
						String cont = child2.getContent();
						part[0] = cont;
					}
					part[2] = ID;
					part[3] = NodoCliente.getLocalAddress().getHostName();

				}
				if(name == true){
					list.add(part);
				}
			}
		}
        return list;
	}

	/**
	* Busca un String dado en un archivo XSPF dado y devuelve una lista de
	* String[], con los datos de las canciones que coincidan con el String.
	*
	* @param String Nombre Nombre de la canci?n.
	* @return Lista con un Arreglo de String Que contiene los datos de las
	* canciones.
	*/	 
	private List<String[]> BibliotecaLocalN(String Nombre) throws Exception {
		List <String[]> list = new ArrayList<String[]>();
	    XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(rB);
	    xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
				String[] part = new String[5];
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
						part[4] = cont;
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
						if(child2.getContent().startsWith(Nombre)){
							name = true;
						}
						part[1] = cont;
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
						part[0] = cont;
					}
					part[2] = ID;
					part[3] = NodoCliente.getLocalAddress().getHostName();
				}
				if(name == true){
					list.add(part);
				}
			}
		}
        return list;
	}

	/**
	* Busca todas las canciones en un archivo XSPF dado y devuelve una lista de
	* String[], con los datos de esas canciones.
	*
	* @return Lista con un Arreglo de String Que contiene los datos de las
	* canciones.
	*/		 
	private List<String[]> BibliotecaLocalTodo() throws Exception{

		List <String[]> list = new ArrayList<String[]>();
		XMLElement xml = new XMLElement();
		FileReader reader = new FileReader(rB);
		xml.parseFromReader(reader);   
		Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
				String[] part = new String[5];
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					 if(child2.getName().equals("location")){
						String cont = child2.getContent();
						part[4] = cont;
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
						part[1] = cont;
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
						part[0] = cont;
					}
					part[2] = ID;
					part[3] = NodoCliente.getLocalAddress().getHostName();
				}
				list.add(part);
			}
		}
		return list;
	}

	/**
	* Funcion que se encarga de Buscar por la Red de Conocidos Todos los Archivos que su Autor haga Match con AUTOR
	*
	* @param Autor String por el que se Filtraran los autores
	* @return Lista con Todos los Resultados que su Autor haga Match con AUTOR
	*/	
    private List<String[]> BuscarRedA(String Autor){
    	Visitado.add(ID_Consulta); //Para Verificar Ciclos
    	
    	List<String[]> Aux = new ArrayList<String[]>();
        // Canal de entrada para el Nodo
        BufferedReader Entrada = null;
        // Canal de salida hacia otro Nodo
        PrintWriter Salida = null;

        for(int i=0;i<c.length;i++){ //Para Cada Conocido
            try {
                Socket client = new Socket(c[i], p); //Se Crea una Conexion		
				
				if( ! client.isConnected()) // Si no se pudo establecer conexion prueba otro conocido
					continue;
				
                Entrada = new BufferedReader(new InputStreamReader( client.getInputStream())); //Se Abren los Procesos de Comunicacion Estandar
                Salida  = new PrintWriter(client.getOutputStream(), true);
            } catch (Exception e){}

            Salida.println(IDLinea);
            Salida.println("Buscar");
            Salida.println(("-a ").concat(Autor));

            Aux.addAll(BufferToList(Entrada));
            
        }
        
        return Aux;
    }

	/**
	* Funcion que se encarga de Buscar por la Red de Conocidos Todos los Archivos que su Titulo haga Match con NOMBRE
	*
	* @param Nombre String que representa con lo que se Realizara la Busqueda
	* @return Lista con Todos los Resultados que su Titulo haga Match con NOMBRE
	*/			
    private List<String[]> BuscarRedN(String Nombre){
    	Visitado.add(ID_Consulta); //Para Verificar Ciclos
    	List<String[]> Aux = new ArrayList<String[]>();
        // Canal de entrada para el Nodo
        BufferedReader Entrada = null;
        // Canal de salida hacia otro Nodo
        PrintWriter Salida = null;

        for(int i=0;i<c.length;i++){ //Para Cada Conocido
            try {
                Socket client = new Socket(c[i], p); //Se Crea Una Conexion con este
				
				if( ! client.isConnected()) // Si no se pudo establecer conexion prueba otro conocido
					continue;				
				
                Entrada = new BufferedReader(new InputStreamReader( client.getInputStream()));  //Se Crean los Canales de Comunicacion estandar
                Salida  = new PrintWriter(client.getOutputStream(), true);
            } catch (Exception e){}
            Salida.println(IDLinea);//Se Cumple El protocolo de Comunicacion
			Salida.println("Buscar");
            Salida.println(("-t ").concat(Nombre));

            Aux.addAll(BufferToList(Entrada)); //Se Agregan a La lista los Resultados
        }
           
        return Aux;
    }
	
	/**
	* Funcion que se encarga de Buscar por la Red de Conocidos Todos los Archivos que esta contenga
	*
	* @return Lista con Todos los Resultados de los Conocidos
	*/		
	private List<String[]> BuscarRedTodo(){
		Visitado.add(ID_Consulta); //Para Verificar Ciclos		
        List<String[]> Aux = new ArrayList<String[]>();
        // Canal de entrada para el Nodo
        BufferedReader Entrada = null;
        // Canal de salida hacia otro Nodo
        PrintWriter Salida = null;

        for(int i=0;i<c.length;i++){ //Para cada Conocido
            try {
                Socket client = new Socket(c[i], p); //Se Crea Una Conexion
				
				if( ! client.isConnected()) // Si no se pudo establecer conexion prueba otro conocido
					continue;				
				
                Entrada = new BufferedReader(new InputStreamReader( client.getInputStream())); //Se Crean los Canales de Comunicacion Estandar
                Salida  = new PrintWriter(client.getOutputStream(), true);
            } catch (Exception e){}
            Salida.println(IDLinea); //Se Cumple el protocolo de Comunicacion
			Salida.println("Buscar");
            Salida.println(" ");

            Aux.addAll(BufferToList(Entrada)); //Se Agregan los resultados Devueltos a los Resultados Totales
        }
        		
        return Aux;
	}
	
	/**
	* Busca Todos Los Nodos Conocidos por Este Nodo
	*
	* @return Lista con un Arreglo de String Que contiene el resultado de la Busqueda
	*/	
	private List<String[]> BuscarConocidos(){
		Visitado.add(ID_Consulta); //Para Verificar Ciclos
        List<String[]> Aux = new ArrayList<String[]>();
        // Canal de entrada para el Nodo
        BufferedReader Entrada = null;
        // Canal de salida hacia otro Nodo
        PrintWriter Salida = null;

		/*
		* El Arreglo de String Se mentiene con posiciones asi porque es
		* el estandar que se adopto para las busquedas de canciones y para
		* no cambiar las funciones se mantiene igual
		*/
        String[] Tmp = new String[5];
        
        Tmp[0] = " ";
        Tmp[1] = " ";
        Tmp[2] = ID; 
        Tmp[3] = NodoCliente.getInetAddress().getHostName();
        Tmp[4] = " ";
		
        Aux.add(Tmp); //El Nodo se Agrega a si mismo
		
        for(int i=0;i<c.length;i++){ //Y envia la Solicitud para que se Agregen todos Sus amigos
		
            try {
                Socket client = new Socket(c[i], p);
				
				if( ! client.isConnected()) // Si no se pudo establecer conexion prueba otro conocido
					continue;				
				
                Entrada = new BufferedReader(new InputStreamReader( client.getInputStream())); 
                Salida  = new PrintWriter(client.getOutputStream(), true);
            } catch (Exception e){}
			
			//Protocolo de Comunicacion
            Salida.println(IDLinea);
            Salida.println("Revisar");

            Aux.addAll(BufferToList(Entrada));
        }
        
        return Aux;
	
	}
	
	/**
	* Funcion que se encarga de Descargarun archivo
	* Aunque No retorna nada, devuelve el archivo a descargar al socket cliente
	*
	* @param Entrada Buffer de entrada que utilizan los Sockets para Comunicarse		
	*/		
    private void Descargar(BufferedReader Entrada){
        String[] Tmp = null;
		try{
            String Linea = Entrada.readLine(); //Datos del Archivo a Descargar
			Tmp = Linea.split("\\|"); //Procesa los Datos del Archivo a Descargar
        }catch(Exception e){
            System.out.println( "Error en Descargar: " );
            System.out.println( e.getMessage() );
        }
		
		java.io.FileInputStream in = null;  //Archivo que esta en el Servidor
		OutputStream  pt = null;            //Via por la que se enviara al Cliente
		try {

			pt = NodoCliente.getOutputStream() ; //Via de comunicacion del Socket                            
			in = new java.io.FileInputStream(new java.io.File(Tmp[4])); //Archivo que se Enviara al Cliente

			byte[] buf = new byte[1024]; //Buffer de Envio
			int len; 
			while ((len = in.read(buf)) > 0 ){  //Mientras Hayan Bytes en el Archivo
				System.out.println("Enviando..."); 
				pt.write(buf, 0, len); //Enviarlos
			}
			pt.close(); //Cierra el Buffer de Envio
			in.close(); //Cierra el Archivo que se envio
		} catch(Exception e) {
			System.out.println("Error al enviar mensaje\n"+e);
		}	
	
    }
	
	/**
	* Funcion que se encarga de Filtrar que Busqueda se desea Hacer y Devolver los Resultados
	* Aunque No retorna nada, devuelve la informacion al que se la solicito por el socket	
	*
	* @param Entrada Buffer de entrada que utilizan los Sockets para Comunicarse		
	*/		
    private void Buscar(BufferedReader Entrada){
        List<String[]> Resultados = null;
        String Linea;
        
        try {
			Salida  = new PrintWriter( NodoCliente.getOutputStream(),true );
            Linea = Entrada.readLine(); //Consulta
           
			
			String[] Tmp = Linea.split(" ");
			
            if(Tmp.length>0){
                if(Linea.charAt(1)=='a')//Busqueda por Autor
                    Resultados = BuscarAutor(Linea.substring(3));
                if(Linea.charAt(1)=='t')//Busqueda por Nombre
                    Resultados = BuscarNombre(Linea.substring(3));    
			}					
            else//Devuelve todo el contenido de la biblioteca
                Resultados = BuscarTodo();
            
            //Retorna el Resultado
            ListToBuffer(Resultados);                    
            
        } catch (Exception e) {
				System.out.println( "Error en Buscar: " );
                System.out.println( e.getMessage() );
        }
    }

	/**
	* Buscar los Identificadores de Todos los Nodos Alcansables desde Este Nodo
	* Aunque No Retorna Nada LA funcion la Envia por El buffer al Cliente
	*/		
    private void RevisarRed(){
		try{
			Salida  = new PrintWriter( NodoCliente.getOutputStream(),true );
		}catch (Exception e){
			System.out.println( "Error En RevisarRed: " );
			System.out.println( e.getMessage() );			
		}
        List<String[]> Resultados;
		Resultados = BuscarConocidos(); //Busca los Conocidos de Este nodo
		
        //Retorna el Resultado
        ListToBuffer(Resultados);                    

    }

	/**
	* Busca Todos Los Archivos de Musica que hagan Match en el campo Autor con el String de Consulta
	*
	* @param Consulta Es el String A Buscar en el Autor de la cancion
	* @return Lista con un Arreglo de String Que contiene el resultado de la Busqueda		
	*/		
    private List<String[]> BuscarAutor(String Consulta){
    	List<String[]> Aux = null;
    	try{
    		Aux = BibliotecaLocalA(Consulta); //Busca en la Biblioteca Local
    		Aux.addAll(BuscarRedA(Consulta)); //Busca en la Red de Nodos
    	}catch(Exception e){
			System.out.println( "Error En BuscarAutor: " );
			System.out.println( e.getMessage() );			
		};
        return Aux;
    }

	/**
	* Busca Todos Los Archivos de Musica que hagan Match en el Campo Titulo con el String de Consulta
	*
	* @param Consulta Es el String A Buscar en el Titulo de la cancion
	* @return Lista con un Arreglo de String Que contiene el resultado de la Busqueda	
	*/	
    private List<String[]> BuscarNombre(String Consulta){
    	List<String[]> Aux = null;
    	try{
			Aux = BibliotecaLocalN(Consulta); //Busca en la Biblioteca Local
			Aux.addAll(BuscarRedN(Consulta)); //Busca en la Red de Nodos
    	}catch(Exception e){
			System.out.println( "Error En BuscarNombre: " );
			System.out.println( e.getMessage() );			
		};
        return Aux;
    }

	/**
	* Busca Todos Los Archivos de Musica de la Biblioteca que se desee y de los Amigos conectados a esta
	*
	* @return Lista con un Arreglo de String Que contiene el resultado de la Busqueda	
	*/
    private List<String[]> BuscarTodo(){
    	List<String[]> Aux = null;
    	try{
    		Aux = BibliotecaLocalTodo(); //Busca todos los de la Biblioteca local
    		Aux.addAll(BuscarRedTodo()); //Tambien Agrega los de la Biblioteca de Amigos
    	}catch(Exception e){
			System.out.println( "Error En BuscarTodo: " );
			System.out.println( e.getMessage() );		
		};
    	return Aux;
    }

	/**
	* Imprime una lista de String.
	*
	* @param Aux Es una Lista de String que se Desea Imprimir
	*/
    private void PrintList(List<String> Aux){
		for(int i=0;i<Aux.size();i++)
            System.out.println( Aux.get(i) );
    }

}

public class Nodo {
    private int p;
    private InetAddress[] c;
    private String rB;
    private String ID;

	/**
	* Constructor de la Clase Nodo
	*
	* @param p   Entero que representa el Puerto
	* @param rC  String que representa la direccion a la lista de conocidos
	* @param rB  String que representa la direccion a la Biblioteca
	* @param ID  String que representa el ID del Nodo
	*/
    public Nodo(int p,String rC,String rB, String ID){

        this.p = p;
        this.ID = ID;
        this.c = ProcesarConocidos(rC); //Convierte la Ruta de conocidos a un arreglo de IP's
        this.rB = rB;  //Ruta de Biblioteca
		
		List<String> Visitado = new ArrayList<String>();
		
        try {
            ServerSocket NodoServidor = new ServerSocket(p);
			
            while(true)
            {
                Socket NodoCliente = NodoServidor.accept(); // Empieza la Conexion

				NodoCliente.setSoLinger(true, 10);
				
                Runnable nuevoCliente = new NodoServer(NodoCliente,p,c,rB,ID,Visitado); 
                Thread hilo = new Thread(nuevoCliente);
                hilo.start();       			

            }
        } catch( Exception e ) {
		    System.out.println( "Error1" );
            System.out.println( e.getMessage() );
        }
    }

	/**
	* Convierte los String que se leen de la lista de conocidos en InetAddress para utilizarlos en los sockets
	*
	* @param Path que es el String de donde se encuentra la lista de conocidos
	* @return un Arreglo con todos los InetAddress que representan la lista de conocidos
	*/
	private InetAddress[] ProcesarConocidos(String Path){
        List<InetAddress> Aux = new ArrayList<InetAddress>();
        try{
            FileReader fr = new FileReader(Path);  
            BufferedReader bf = new BufferedReader(fr);
            String Linea;
            while ((Linea = bf.readLine())!=null) { //para Cada linea dentro del Archivo de Conocidos
				if(Linea.charAt(0) != ' ' && Linea.charAt(0) != '\n')
					Aux.add(InetAddress.getByName(Linea));//Crea el inetAddres  y lo agrega a la lista
            }
        } catch(Exception e) {
			System.out.println( "Error2" );
            System.out.println( e.getMessage() );
        }
        
        InetAddress[] Tmp = new InetAddress[Aux.size()];
        
        for(int i=0;i<Aux.size();i++) //Para cada miembro de la lista
            Tmp[i] = Aux.get(i); //lo Guarda en un arreglo
                    
        return Tmp;
    }
	
	/**
	* Main que Verifica la entrada correcta por Consola de parametros para la instanciacion del Nodo
	*/
    public static void main(String[] args){
		
        int p = 0; //Puerto
		String c = ""; //Conocidos
        String b = ""; //Biblioteca
        String ID = ""; //ID del Nodo
		
		
        for (int i = 0; i < args.length; i++) {
			
            if(args[i].charAt(1)=='p'){ //Puerto
                i++;
                p = Integer.parseInt(args[i]);
                continue;
            }
            
            if(args[i].charAt(1)=='c'){ //Conocidos
                i++;
                c = args[i];
                continue;
            }
			
            if(args[i].charAt(1)=='b'){ //Biblioteca
                i++;
                b = args[i];
                continue;
            }			
            
            if(args[i].charAt(1)=='i'){ //ID del Nodo
                i++;
                ID =  args[i];
                continue;
            }
            
            if(true){
                System.out.println("Error de Sintaxys En Creacion del Nodo");
            }
        }
			
        Nodo A = new Nodo(p,c,b,ID); //Crea una Nueva Instancia Nodo
    }
	
}
