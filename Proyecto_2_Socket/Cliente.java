/**
 *      @file Cliente.java
 *      @brief Esta es la Implementacion del Cliente que es el que se encarga de realizar la conexion con cualquier servidor
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 16/11/2011
 * 
 */

import java.net.*;
import java.util.*;
import java.io.*;
import java.util.Random;
import javax.sound.sampled.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;


public class Cliente {
    
	
    int p;
    InetAddress host;
    String d;
    

    // Socket por el que se conectara el cliente
    Socket cliente;

	// Canal de entrada para el cliente
    BufferedReader Entrada = null;
	// Canal de salida hacia el servidor
    PrintWriter Salida = null;
    
	/**
	* Constructor del Cliente
	*
	* @param p Entero que representa el puerto de Conexion
	* @param host String que representa el Host al que se Conectara
	* @param d	String que representa el Directorio de Descarga
	*/		
    public Cliente(int p,String host,String d){
        this.p = p;
        this.d = d;
        this.host = getHost(host);
    }

	/**
	* Imprime el Contenido del Buffer por Consola
	*
	* @param buff Buffer que se desea Imprimir para Ver que contiene
	*/			
    private void BufferToPrint(BufferedReader Buff){
        String Linea;
        
        try{
            while ((Linea = Buff.readLine()) != null)//Mientras aun haya contenido
                System.out.println(Linea); //imprimelo
        }catch(Exception e){
            System.out.println( e.getMessage() );
        }
    }
    
	/**
	* Convierte el String que representa a un host a un InnetAddress que puede ser usado por un Socket
	*
	* @param Host String que contiene el Host que se utilizara por defecto
	*/	
    private InetAddress getHost(String Host){
        InetAddress Aux = null;
        
        try{
            Aux =(InetAddress.getByName(Host));        
        }
        catch(Exception e)
        {
            System.out.println("Error en getHost");
			System.out.println(e.getMessage());
        }
        
        return Aux;
    }
	
	/**
	* Convierte El contenido del Buffer a una lista de arreglo de String
	*
	* @param Buff Buffer que contiene lo que se desea Transformar
	*/		
    private List<String[]> BufferToList(BufferedReader Buff){
        List<String[]> Aux = new ArrayList<String[]>();
        String Linea;
        String[] Tmp;
        try{
            while ((Linea = Buff.readLine()) != null){  //Para cada Linea del Buffer 
				Tmp = Linea.split("\\|"); //Separa lo que viene separado por el caracter "|" en posiciones de un arreglo
                Aux.add(Tmp); //agregar ese arreglo a la lista
            }
        }catch(Exception e){
            System.out.println( e.getMessage() );
        }        
        
        return Aux;
    }    
	
	/**
	* Procesa el requerimiento de Buscar en la Red un Archivo
	*
	* @param Consulta String que envia la consulta que se realizara al Servidor (Nodos)
	* @return Regresa una lista de Arreglos de String que contiene la informacion requerida
	*/		
    private List<String[]> Buscar(String Consulta){
	
        try {
            Socket client = new Socket(host, p); // Se Crea la Conexion
			
			if( ! client.isConnected()){ // Si no se pudo establecer conexion da error y retorna null
				System.out.println("Error de Conexion con el Nodo");
				return null;
			}
			
            Entrada = new BufferedReader(new InputStreamReader( client.getInputStream()));  //Se Crean los mecanismos de comunicacion
            Salida  = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e){
            System.out.println( "Error de conexion con el Servidor" + e.getMessage() );
			return null;
        }
        
        Random rnd = new Random(new Date().getTime()); //Se crea un numero pseudoaleatorio en funcion de la hora del sistema
        int IDCliente = rnd.nextInt(); 
        
        Salida.println(IDCliente); //se envia el Numero pseudoaleatorio para diferenciar las consultas
		Salida.println("Buscar"); //Se envia la palabra Buscar para que el servidor sepa de que tipo de consulta se trata
        Salida.println(Consulta); // se envia el contenido de la consulta para que sepa que buscar

        return BufferToList(Entrada);
    }

	/**
	* Procesa el requerimiento de Descargar un Archivo en la red
	*
	* @param Archivo Arreglo que contiene toda la informacion del archivo que se desea descargar
	*/	
    private void Descargar(String[] Archivo){
		try {
			Socket client = new Socket(host, p); //Crea un Socket para la conexion con el servidor
		   
			if( ! client.isConnected()){ // Si no se pudo establecer conexion da error y retorna null
				System.out.println("Error de Conexion con el Nodo");
			}	   
			else {
			
				Salida  = new PrintWriter(client.getOutputStream(), true); //crea un Mecanismo de Comunicacion con el servidor
				
				Random rnd = new Random(new Date().getTime()); //Se crea un numero pseudoaleatorio en funcion de la hora del sistema
				int IDCliente = rnd.nextInt();
				
				Salida.println(IDCliente); //se envia el Numero pseudoaleatorio para diferenciar las consultas
				Salida.println("Descargar"); //Se envia la palabra Descargar para que el servidor sepa de que tipo de consulta se trata			
				Salida.println(Archivo[0]+"|"+Archivo[1]+"|"+Archivo[2]+"|"+Archivo[3]+"|"+Archivo[4]);  // se envia los datos del archivo que se quiere descargar

				
				String fullpath = d + Archivo[4].substring(Archivo[4].lastIndexOf('/'));
				
				//ABRIMOS UN BUFER PARA DESCARGAR LO QUE EL CLIENTE NOS ESTA ENVIANDO
				java.io.InputStream in = client.getInputStream();
				//CREAMOS LA INSTANCIA PARA ESCRIBIR EL ARCHIVO EN DISCO
				java.io.FileOutputStream out = new java.io.FileOutputStream(new java.io.File(fullpath));
				byte[] buf = new byte[1024]; //se crea el buffer de lectura
				int len;
				while ((len = in.read(buf)) > 0) { //mientras haya algo que leer
					out.write(buf, 0, len); //lo lee y lo escribe en el nuevo archivo
				}
				in.close(); //se cierran los buffer 
				out.close(); //se cierran los buffer
			}
		} catch(Exception e){ 
            System.out.println( "Error de conexion con el Servidor" + e.getMessage() );
		}
    }

	/**
	* Procesa el requerimiento conocer los nodos conectados de la red
	*
	* @return Lista que contiene todos los nodos accesibles desde la red
	*/		
    private List<String[]> ConexionesDisponibles(){
        try {
            Socket client = new Socket(host, p); //crea una nueva conexion
			
			if( ! client.isConnected()){ // Si no se pudo establecer conexion da error y retorna null
				System.out.println("Error de Conexion con el Nodo");
				return null;
			}			
			
            Entrada = new BufferedReader(new InputStreamReader( client.getInputStream()));  //crea los mecanismos de comunicacion 
            Salida  = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e){
            System.out.println( "Error de conexion con el Servidor" + e.getMessage() );
			return null;
        }
        
        Random rnd = new Random(new Date().getTime());//se envia el Numero pseudoaleatorio para diferenciar las consultas
        int IDCliente = rnd.nextInt();        
        
        Salida.println(IDCliente);//se envia el Numero pseudoaleatorio para diferenciar las consultas
		Salida.println("Revisar");//Se envia la palabra Revisar para que el servidor sepa de que tipo de consulta se trata

        return BufferToList(Entrada);
		
    }

	/**
	* Imprime una Lista de String que contiene toda la informacion de las canciones que se estaban buscando
	*
	* @param Table Lista de arreglo de String que contiene toda la informacion de las canciones que se estaban buscando
	*/	
    private void printTable(List<String[]> Table){
        String[] Tmp;

		//Encabezado
        System.out.print("Num"); 
        System.out.print("Autor");
        System.out.print("Cancion");
        System.out.println("Nodo");

        for(int i=0;i<Table.size();i++){ //Para Cada Entrada
            Tmp = Table.get(i);          //Imprime la informacion
            System.out.print(i);
            System.out.print(Tmp[0]);
            System.out.print(Tmp[1]);
            System.out.println(Tmp[2]);
        }
    }
      
	/**
	* Imprime una Lista de String que contiene todos los nodos Accesibles desde el que este conectado este cliente
	*
	* @param Nodos Lista de arreglo de String que contiene los nodos accesibles desde el nodo asociado a este cliente
	*/	
	private	void printNodos(List<String[]> Nodos){
        String[] Tmp;
        System.out.println("Nodo");

        for(int i=0;i<Nodos.size();i++){ //para cada entrada de la lista
            Tmp = Nodos.get(i); 
            System.out.println(Tmp[2]); //Imprime Su nombre
        }
	}
	
	/**
	* Envia reproducir un archivo que este en el directorio de descarga o lo descarga y luego lo envia a reproducir
	*
	* @param Archivo arreglo de String que contiene la informacion de la cancion que se quiere reproducir
	*/	
	private void Reproductor(String[] Archivo){

		//Buscar Si el Archivo esta Descargado
		String fullpath = d + Archivo[4].substring(Archivo[4].lastIndexOf('/'));
		File cancion=new File(fullpath);
		//Si esta Descargado Reproducelo
		if(cancion.exists())
			Reproducir(fullpath);
		else//Sino Descargalo y luego Reproducelo
		{
			Descargar(Archivo);
			Reproducir(fullpath);
		}

	}
	
	/**
	* Reproduce un Archivo de musica que este en en Directorio de descarga
	*
	* @param ruta String que Representa la Ruta del Archivo que se desea reproducir
	*/
    private void Reproducir(String ruta){
        try {
            FileInputStream fis;
            Player player;
            fis = new FileInputStream(ruta);
            BufferedInputStream bis = new BufferedInputStream(fis);

            player = new Player(bis); // Llamada a constructor de la clase Player
            player.play();          // Llamada al método play
        } catch (JavaLayerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		

	}
	
	/**
	* Main del Cliente que Crea la Instancia y inicia la corrida
	* 
	*/
    public static void main(String[] args) {
	
        File miDir = new File (".");
        int p = 0;
        String host = "";
        String d = "";
        try{
            d = miDir.getCanonicalPath();
        } catch (Exception e)
        {
			System.out.println("Error consiguiendo la direccion automatica del archivo");
			System.out.println( e.getMessage() );
        }
		
        for (int i = 0; i < args.length; i++) {
			
            if(args[i].charAt(1)=='p'){ //Puerto
                i++;
                p = Integer.parseInt(args[i]);
                continue;
            }
            
            if(args[i].charAt(1)=='n'){ //Nodo
                i++;
                host = args[i];
                continue;
            }
            
            if(args[i].charAt(1)=='d'){ //Directorio Descarga
                i++;
                d =  args[i];
                continue;
            }
            
            if(true){
                System.out.println("Error En Creacion del Cliente");
            }
        }
        
		System.out.println("*******************************");
		System.out.println("************INICIO*************");
		System.out.println("*******************************");
		
        Cliente Datos = new Cliente(p,host,d);
        boolean exit = false;
        
        List<String[]> Resultados = null;
        String comando;
        while(!exit){
        	
        	comando = "";
			System.out.print("->");
            Scanner sc = new Scanner(System.in);
			System.out.println("Presione Enter para Procesar");
			comando = sc.nextLine();
		
            String[] Tmp = comando.split(" ");
            
            if(Tmp[0].charAt(0)=='C' || Tmp[0].charAt(0)=='c') //Consulta
            {
				Resultados = null;
                if(Tmp.length>1)
					if(Tmp[1].charAt(1)=='a' || Tmp[1].charAt(1)=='t')
						Resultados = Datos.Buscar(comando.substring(2));
					else
						System.out.println("Error en Sintaxys/ -a para Autores, -t para Titulo, dejar Vacio para todo");
                else
                    Resultados = Datos.Buscar(" ");
                
				if(Resultados != null)
					Datos.printTable(Resultados);
                
                continue;
            }

            if(Tmp[0].charAt(0)=='D' || Tmp[0].charAt(0)=='d') //Descarga
            {
                if( Resultados == null )
                    System.out.println("Error--Primero Realice una Busqueda\n");
                else
                    Datos.Descargar(Resultados.get(Integer.parseInt(Tmp[1])));

                continue;
            }
            
            if(Tmp[0].charAt(0)=='P' || Tmp[0].charAt(0)=='p') //Reproduce
            {
                if( Resultados == null )
                    System.out.println("Error--Primero Realice una Busqueda\n");
                else
                    Datos.Reproductor(Resultados.get(Integer.parseInt(Tmp[1])));

                continue;
            }
			
            if(Tmp[0].charAt(0)=='A' || Tmp[0].charAt(0)=='a') //Conexiones Disponibles(Nodos Alcanzables)
            {
                Datos.printNodos(Datos.ConexionesDisponibles());
                continue;
            }
	
			
            if(Tmp[0].charAt(0)=='Q' || Tmp[0].charAt(0)=='q') //Quitar
            {
                exit = true;
				System.out.println("*******************************");
				System.out.println("**************FIN**************");
				System.out.println("*******************************");				
                continue;
            }
            
            if(true) //Error en Sintaxis
            {
                System.out.println("Error en Sintaxis");
            }
            
        }
    }	
    
}

