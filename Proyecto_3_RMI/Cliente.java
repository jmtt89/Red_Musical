/**
 *      @file Cliente.java
 *      @brief Esta es la Implementacion del Cliente que es el que se encarga de realizar la conexion con cualquier servidor
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 7/12/2011
 * 
 */

import java.util.*;
import java.io.*;
import java.util.Random;
import java.rmi.*;

public class Cliente {
    
    int p;
    String host;
    String d;

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
        this.host = host;
    }

    /**
    * Procesa el requerimiento de Buscar en la Red un Archivo
    *
    * @param Consulta String que envia la consulta que se realizara al Servidor (Nodos)
    * @return Regresa una lista de Arreglos de Mp3 que contiene la informacion requerida
    */		
    private List<Mp3> Buscar(String Consulta){
	
        List<Mp3> Res = null;
        try {
            InterfaceRemota Nodo = (InterfaceRemota)Naming.lookup ("rmi://" + host + ":" + p + "/Servidor");
            Random rnd = new Random(new Date().getTime()); //Se crea un numero pseudoaleatorio en funcion de la hora del sistema
            int IDCliente = rnd.nextInt();
            Res = Nodo.Buscar(IDCliente+"",Consulta);
        } catch (Exception e){
            System.out.println( "Error de conexion con el Servidor en Buscar: " + e.getMessage() );
            return null;
        }

        return Res;
    }

    /**
    * Procesa el requerimiento de Descargar un Archivo en la red
    * @param Archivo que contiene toda la informacion del archivo que se desea descargar
    */	
    private void Descargar(Mp3 Archivo){
        try {

             String[] IP = Archivo.getNodo().split("/");

             InterfaceRemota Nodo = (InterfaceRemota)Naming.lookup ("rmi://"+IP[1]+":"+p+"/Servidor");
             File f1 = Nodo.Descargar(Archivo.getPath().replace('\n', '\0'));
             File f2 = new File(d + Archivo.getTitulo() + ".mp3");
             InputStream in = new FileInputStream(f1);
             OutputStream out = new FileOutputStream(f2);

             byte[] buf = new byte[1024];
             int len;
             while ((len = in.read(buf)) > 0){
            	 out.write(buf, 0, len);
             }
             in.close();
             out.close();
             System.out.println("Archivo Descargado.");
    	} catch(Exception e){ 
    		System.out.println( "Error de conexion con el Servidor en Descargar: " + e );
    	}
    }


	/**
	* Reproduce un Archivo de musica que este en en Directorio de descarga
	*
	* @param ruta String que Representa la Ruta del Archivo que se desea reproducir
	*/
    private void Reproducir(Mp3 Archivo){
/**       
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
**/
    }


    /**
    * Procesa el requerimiento conocer los nodos conectados de la red
    *
    * @return Lista que contiene todos los nodos accesibles desde la red
    */		
    private List<String> ConexionesDisponibles(){
        List<String> Res = null;
        try {
            InterfaceRemota Nodo = (InterfaceRemota)Naming.lookup ("rmi://"+host+":"+p+"/Servidor");
            Random rnd = new Random(new Date().getTime()); //Se crea un numero pseudoaleatorio en funcion de la hora del sistema
            int IDCliente = rnd.nextInt(); 
            Res = Nodo.RevisarRed(IDCliente+"");
        } catch (Exception e){
            System.out.println( "Error de conexion con el Servidor en Conexiones Disponibles: " + e.getMessage() );
            return null;
        }

        return Res;

    }

    /**
    * Imprime una Lista de String que contiene toda la informacion de las canciones que se estaban buscando
    *
    * @param Table Lista de arreglo de Mp3 que contiene toda la informacion de las canciones que se estaban buscando
    */	
    private void printTable(List<Mp3> Table){
        Mp3 Tmp;
        for(int i=0;i<Table.size();i++){ //Para Cada Entrada
            Tmp = Table.get(i);          //Imprime la informacion
            System.out.println(i+".");
            System.out.println("Propietario: "+Tmp.getID());
            System.out.println("Titulo: "+Tmp.getTitulo());
            System.out.println("Autor: "+Tmp.getAutor());
            System.out.println("Album: "+Tmp.getAlbum());
            System.out.println("Genero: "+Tmp.getGenero());
            System.out.println("Numero de Track: "+Tmp.getNumero());
            System.out.println("Año: "+Tmp.getAno());
            System.out.println("Comentario: "+Tmp.getComentario());
        }
    }

    /**
    * Imprime una Lista de String que contiene todos los nodos Accesibles desde el que este conectado este cliente
    *
    * @param Nodos Lista de arreglo de String que contiene los nodos accesibles desde el nodo asociado a este cliente
    */	
    private	void printNodos(List<String> Nodos){
        String Tmp;
        for(int i=0;i<Nodos.size();i++){ //para cada entrada de la lista
            Tmp = Nodos.get(i); 
            System.out.println(i + " Nodo: " + Tmp); //Imprime Su nombre
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
            d = miDir.getCanonicalPath()+"/";
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
        
        List<Mp3> Resultados = null;
        String comando;
		Scanner sc = new Scanner(System.in);
        while(!exit){
        	
            comando = "";
            System.out.print("->");
            comando = sc.nextLine();
		
            String[] Tmp = comando.split(" ");

            if(Tmp[0].charAt(0)=='C' || Tmp[0].charAt(0)=='c') //Consulta
            {
                Resultados = null;
                if(Tmp.length>1)
                    if(Tmp[1].charAt(1)=='a' || Tmp[1].charAt(1)=='t' || Tmp[1].charAt(1)=='y' || Tmp[1].charAt(1)=='l' || Tmp[1].charAt(1)=='g' || Tmp[1].charAt(1)=='c' || Tmp[1].charAt(1)=='n')
                        Resultados = Datos.Buscar(comando.substring(2));
                    else if (Tmp[1].charAt(1)=='h')
		    	System.out.println("Sintaxys: -a para Autores, -t para Titulo, -y para Año, -l para Album, -g para Genero, -c para Comentario, -n para Numero dejar Vacio para todo");
                         else
                              System.out.println("Error en Sintaxys/ -a para Autores, -t para Titulo, -y para Año, -l para Album, -g para Genero, -c para Comentario, -n para Numero  dejar Vacio para todo");
                else
                    Resultados = Datos.Buscar("     ");
                
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

            if(Tmp[0].charAt(0)=='P' || Tmp[0].charAt(0)=='p') //Play (Reproducir)
            {
                if( Resultados == null )
                    System.out.println("Error--Primero Realice una Busqueda\n");
                else
                    Datos.Reproducir(Resultados.get(Integer.parseInt(Tmp[1])));

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
