/**
 *      @file Nodo.java
 *      @brief Esta es El Servidor que Controla el Objeto Remoto Servidor
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 7/12/2011
 * 
 */

import java.rmi.*;
import java.util.*;
import java.io.*;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;
import entagged.audioformats.exceptions.CannotWriteException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import nanoxml.XMLElement;
import nanoxml.*;

public class Nodo 
{
    private int p;
    private String[] c;
    private String rB;
    private String ID;    
    
    private List<String[]> xspf = new ArrayList<String[]>();
    
    /**
    * Constructor de la Clase Nodo
    *
    * @param p   Entero que representa el Puerto
    * @param rC  String que representa la direccion a la lista de conocidos
    * @param rB  String que representa la direccion a la Biblioteca
    * @param ID  String que representa el ID del Nodo
    */
    public Nodo(int p,String rC,String rB, String ID) {
        this.p = p; 
        this.ID = ID;
        this.c = ProcesarConocidos(rC); //Convierte la Ruta de conocidos a un arreglo de IP's
        
        List<String> Visitado = new ArrayList<String>();        
        
        try 
        {
            this.rB = XspfMaker(dirXspf(rB));  //Ruta de Biblioteca

            java.rmi.registry.LocateRegistry.createRegistry(p) ;		
            // Se publica el objeto remoto
            InterfaceRemota Servidor = new Servidor(c,this.rB, ID,Visitado,p);
            Naming.rebind ("rmi://localhost:"+p+"/Servidor", Servidor);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
	/**
	* Convierte los String que se leen de la lista de conocidos en InetAddress para utilizarlos en los sockets
	*
	* @param Path que es el String de donde se encuentra la lista de conocidos
	* @return un Arreglo con todos los InetAddress que representan la lista de conocidos
	*/
	private String[] ProcesarConocidos(String Path){
        List<String> Aux = new ArrayList<String>();
        try{
            FileReader fr = new FileReader(Path);  
            BufferedReader bf = new BufferedReader(fr);
            String Linea;
            while ((Linea = bf.readLine())!=null) { //para Cada linea dentro del Archivo de Conocidos
				if(Linea.charAt(0) != ' ' && Linea.charAt(0) != '\n')
					Aux.add(Linea);//Agrega La Linea
            }
        } catch(Exception e) {
			System.out.println( "Error2" );
            System.out.println( e.getMessage() );
        }
        
        String[] Tmp = new String[Aux.size()];
        
        for(int i=0;i<Aux.size();i++) //Para cada miembro de la lista
            Tmp[i] = Aux.get(i); //lo Guarda en un arreglo
                    
        return Tmp;
    }

//Creacion del Xspf
	private String XspfMaker(List<String[]> xspf) throws IOException {
		String cam = ".";
		String camino = "";
		FileWriter fichero = null;
		PrintWriter pw = null;
		camino = cam+File.separatorChar+"biblioteca.xspf";
		fichero = new FileWriter(camino);
		pw = new PrintWriter(fichero,true);
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		XMLElement elt = new XMLElement();
		elt.setName("playlist");
		elt.setAttribute("version", "1");
		String atr1 = "http://xspf.org/ns/0/";
		elt.setAttribute("xmlns", atr1);
		XMLElement child1 = new XMLElement();
		child1.setName("trackList");
		elt.addChild(child1);
	     
		
		for (int i = 0; i < xspf.size(); i++){
			XMLElement child2 = new XMLElement();
			XMLElement child3 = new XMLElement();
			XMLElement child4 = new XMLElement();
			XMLElement child5 = new XMLElement();
			XMLElement child6 = new XMLElement();
			XMLElement child7 = new XMLElement();
			XMLElement child8 = new XMLElement();
			XMLElement child9 = new XMLElement();
			XMLElement child10 = new XMLElement();
			XMLElement child11 = new XMLElement();

			child2.setName("track");
			child1.addChild(child2);
			String[] temp = xspf.get(i);
			child3.setName("location");
			child3.setContent(temp[6]);
			child2.addChild(child3);
			child4.setName("title");
			child4.setContent(temp[0]);
			child2.addChild(child4);
			child5.setName("creator");
			child5.setContent(temp[1]);
			child2.addChild(child5);
			child6.setName("album");
			child6.setContent(temp[2]);
			child2.addChild(child6);
			child7.setName("extension");
			child2.addChild(child7);

			child8.setName("genre");
			child8.setContent(temp[3]);
			child7.addChild(child8);
			child9.setName("numTrack");
			child9.setContent(temp[4]);
			child7.addChild(child9);
			child10.setName("year");
			child10.setContent(temp[5]);
			child7.addChild(child10);
			child11.setName("comment");
			child11.setContent(temp[7]);
			child7.addChild(child11);
		            
		}
	     
		String pro = elt.toString();
		String delims = "[<]+";
		String[] tokens = pro.split(delims);
		for (int i = 0; i < tokens.length; i++){
			if(i == 0)
			    pw.println(tokens[i]);
			else
			    if(tokens[i].startsWith("track"))
				pw.println("<"+tokens[i]);
			    else
				pw.println("<"+tokens[i]);
		}


		return camino;
	}   

	private List<String[]> dirXspf(String dirPath) throws CannotReadException, CannotWriteException, IOException{
            //le paso el path de la biblioteca, creo un File con ese path, ciclo en ese dir, si es dir llamo
            //a la director recursivamente, si es un mp3 llamo a Id3tags
            File actual = new File(dirPath);
            String files = "";
            boolean blah = actual.isDirectory();
            if(blah){
                File[] listOfFiles = actual.listFiles(); 

                for (int i = 0; i < listOfFiles.length; i++){
                    String u = listOfFiles[i].getAbsolutePath();
                    if (listOfFiles[i].isFile()){
                        files = listOfFiles[i].getName();
                        JFileChooser chooser = new JFileChooser();
                        String fType = chooser.getTypeDescription(listOfFiles[i]);
                        String fileName; 
                        String extension;  

                        String s2 = listOfFiles[i].getName();

                        fileName = s2.trim();

                        int dotPos = fileName.lastIndexOf(".");
                        extension = fileName.substring(dotPos);
                        if(fType.equals("Audio MPEG Layer 3")||fType.startsWith("MP3 audio")||fType.equals("MP3 audio (audio/mpeg)")||extension.equals(".mp3"))
                                xspf.add(id3Tags(u));
                    }
                    else
                        dirXspf(u);
                }        
            }
            return xspf;
	}

	    
	 private String[] id3Tags(String path1) throws CannotReadException, CannotWriteException, IOException{
		File directory = new File(path1);

		AudioFile audioFile = AudioFileIO.read(directory); 
		int bitrate = audioFile.getBitrate(); 
		List artist = audioFile.getTag().getArtist(); 
		int tam = artist.size();
		
		List title = audioFile.getTag().getTitle(); 
		String tit = audioFile.getTag().getFirstTitle();
		String art = audioFile.getTag().getFirstArtist();
		String alb = audioFile.getTag().getFirstAlbum();
		String gen = audioFile.getTag().getFirstGenre();
		String tra = audioFile.getTag().getFirstTrack();
		String yea = audioFile.getTag().getFirstYear();
		String com = audioFile.getTag().getFirstComment();
		
		String[] salida = new String[8];
		salida[0] = tit;
		salida[1] = art;
		salida[2] = alb;
		salida[3] = gen; 
		salida[4] = tra; 
		salida[5] = yea;     
		salida[6] = path1;
		salida[7] = com;
		
		return salida;
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



    

	
