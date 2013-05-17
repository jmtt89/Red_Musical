/**
 *      @file Servidor.java
 *      @brief Esta es la Implementacion de la Interface, Osea el Objeto Remoto que se Accede para las Busquedas y descargas
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 7/12/2011
 * 
 */

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.net.*;


import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import nanoxml.XMLElement;

public class Servidor extends UnicastRemoteObject 
    implements InterfaceRemota
{
    //Atributos del Servidor
    private String[] c;
    private String ruta;
    private String ID;    
    private String host;
    private int p;
	
    //Control Interno
    private List<String> Visitado;
    
    /**
    * Constructor de la Clase Servidor que se encarga de Crear el Objeto Remoto
    *
    * @param c   Arreglo de InetAddress que guarda todos los conocidos de un Servidor
    * @param rB  String que representa la direccion del archivo xspf
    * @param ID  String que representa el ID del Servidor
    * @param Visitado Arreglo que contiene el Servidor que lo visito en cada consulta para evitar ciclos
    * @param p Entero que Representa el Puerto de Conexion
    */
    public Servidor (String[] c,String rB, String ID,List<String> Visitado,int p) throws RemoteException
    {
        super();
        this.ID = ID;
        this.c = c;
        this.ruta = rB; 
        this.Visitado = Visitado;
        this.p = p;

        try{
            this.host = InetAddress.getLocalHost().toString();//Genera el Host Actual
        } catch (Exception e) {};
    }

    /**
    * Funcion que Enviar el Archivo que se Quiere Descargar
    *
    * @param Path Direccion Del Archivo que se Quiere Descargar
    */
    public File Descargar(String Path){

	File DOC = new File(Path);	
	return DOC;
	
    }

    /**
    * Funcion que Revisa los Nodos Conectados a este
    *
    * @param ID_Colsulta String Que Repreasenta Cada Consulta de Algun Cliente, Se Utiliza para Evitar Ciclos
    */
    public List<String> RevisarRed(String ID_Consulta){
        List<String> Res = new ArrayList<String>();
        if(!Visitado.contains(ID_Consulta)){
            Res.add(ID);
            Visitado.add(ID_Consulta); //Para Verificar Ciclos
            InterfaceRemota Servidor;
            for(int i=0;i<c.length;i++){
                try{
                    Servidor = (InterfaceRemota)Naming.lookup ("rmi://" + c[i] + ":" + p + "/Servidor");
                    Res.addAll(Servidor.RevisarRed(ID_Consulta));
                }catch (Exception e){
                }
            }
        }
        return Res;
        
    }
// Grupo de Funciones Realizan la Busqueda en el Xml	

	//Por Nombre
     public List<Mp3> BibliotecaLocalN(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
						if(child2.getContent().startsWith(Nombre)){
							name = true;
						}
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Por Album
     public List<Mp3> BibliotecaLocalAl(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                       if(child2.getName().equals("album")){
						String cont = child2.getContent();
						if(child2.getContent().startsWith(Nombre)){
							name = true;
                                                        mp3.setAlbum(cont);
						}
					}
                                       mp3.setID(ID);
                                       mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Por Genero
     public List<Mp3> BibliotecaLocalG(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                        if(child2.getName().equals("extension")){
                                            List temp = child2.getChildren();
                                            for(int o1 = 0; o1 < temp.size(); o1++){
                                                XMLElement child3 = (XMLElement)temp.get(o1);
                                                if(child3.getName().equals("genre")){
                                                    String cont = child3.getContent();
                                                    if(cont.startsWith(Nombre)){
                                                            name = true;
                                                            mp3.setGenero(cont);
                                                    }
                                                }
                                            }
					}
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Por Numero
     public List<Mp3> BibliotecaLocalNu(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                        if(child2.getName().equals("extension")){
                                            List temp = child2.getChildren();
                                            for(int o1 = 0; o1 < temp.size(); o1++){
                                                XMLElement child3 = (XMLElement)temp.get(o1);
                                                if(child3.getName().equals("numTrack")){
                                                    String cont = child3.getContent();
                                                    if(cont.startsWith(Nombre)){
                                                            name = true;
                                                            mp3.setNumero(cont);
                                                    }
                                                }
                                            }
						
					}
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Por Año
     public List<Mp3> BibliotecaLocalY(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                        if(child2.getName().equals("extension")){
                                            List temp = child2.getChildren();
                                            for(int o1 = 0; o1 < temp.size(); o1++){
                                                XMLElement child3 = (XMLElement)temp.get(o1);
                                                if(child3.getName().equals("year")){
                                                    String cont = child3.getContent();
                                                    if(cont.startsWith(Nombre)){
                                                            name = true;
                                                            mp3.setAno(cont);
                                                    }
                                                }
                                            }
						
					}
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Por Comentario
     public List<Mp3> BibliotecaLocalC(String Nombre) throws Exception {
	List <Mp3> list = new ArrayList<Mp3>();
	XMLElement xml = new XMLElement();
        FileReader reader = new FileReader(ruta);
	xml.parseFromReader(reader);
        Enumeration enu = xml.enumerateChildren();
		while (enu.hasMoreElements()) {
			XMLElement child = (XMLElement) enu.nextElement();
			Enumeration enu1 = child.enumerateChildren();
			while (enu1.hasMoreElements()) {
				XMLElement child1 = (XMLElement) enu1.nextElement();
                                Mp3 mp3 = new Mp3();
				boolean name = false;
				Enumeration enu2 = child1.enumerateChildren();
				while (enu2.hasMoreElements()) {
					XMLElement child2 = (XMLElement) enu2.nextElement();
					if(child2.getName().equals("location")){
						String cont = child2.getContent();
                                                mp3.setPath(cont);
					}
					if(child2.getName().equals("title")){
						String cont = child2.getContent();
                                                mp3.setTitulo(cont);
					}
					if(child2.getName().equals("creator")){
						String cont = child2.getContent();
                                                mp3.setAutor(cont);
					}
                                        if(child2.getName().equals("extension")){
                                            List temp = child2.getChildren();
                                            for(int o1 = 0; o1 < temp.size(); o1++){
                                                XMLElement child3 = (XMLElement)temp.get(o1);
                                                if(child3.getName().equals("comment")){
                                                    String cont = child3.getContent();
                                                    if(cont.startsWith(Nombre)){
                                                            name = true;
                                                            mp3.setComentario(cont);
                                                    }
                                                }
                                            }
						
					}
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
				}
				if(name == true){
					list.add(mp3);
				}
			}
		}
        return list;
	}

	//Todo
public List<Mp3> BibliotecaLocalTodo() throws Exception {

	   List <Mp3> list = new ArrayList<Mp3>();
	   XMLElement xml = new XMLElement();
       FileReader reader = new FileReader(ruta);
	   xml.parseFromReader(reader);
       Enumeration enu = xml.enumerateChildren();
               while (enu.hasMoreElements()) {
                       XMLElement child = (XMLElement) enu.nextElement();
                       Enumeration enu1 = child.enumerateChildren();
                       while (enu1.hasMoreElements()) {
                               XMLElement child1 = (XMLElement) enu1.nextElement();
                               Mp3 mp3 = new Mp3();
                               boolean name = false;
                               Enumeration enu2 = child1.enumerateChildren();
                               while (enu2.hasMoreElements()) {
                                       XMLElement child2 = (XMLElement) enu2.nextElement();
                                       if(child2.getName().equals("location")){
                                               String cont = child2.getContent();
                                               mp3.setPath(cont);
                                       }
                                       if(child2.getName().equals("title")){
                                               String cont = child2.getContent();
                                               mp3.setTitulo(cont);
                                       }
                                       if(child2.getName().equals("creator")){
                                               String cont = child2.getContent();
                                               mp3.setAutor(cont);
                                       }
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
                               }
                               list.add(mp3);
                               
                       }
               }
       return list;
       }
	
	// Por Autor
public List<Mp3> BibliotecaLocalA(String Nombre) throws Exception {
               List <Mp3> list = new ArrayList<Mp3>();
           XMLElement xml = new XMLElement();
       FileReader reader = new FileReader(ruta);
           xml.parseFromReader(reader);
       Enumeration enu = xml.enumerateChildren();
               while (enu.hasMoreElements()) {
                       XMLElement child = (XMLElement) enu.nextElement();
                       Enumeration enu1 = child.enumerateChildren();
                       while (enu1.hasMoreElements()) {
                               XMLElement child1 = (XMLElement) enu1.nextElement();
                               Mp3 mp3 = new Mp3();
                               boolean name = false;
                               Enumeration enu2 = child1.enumerateChildren();
                               while (enu2.hasMoreElements()) {
                                       XMLElement child2 = (XMLElement) enu2.nextElement();
                                       if(child2.getName().equals("location")){
                                               String cont = child2.getContent();
                                               mp3.setPath(cont);
                                       }
                                       if(child2.getName().equals("title")){
                                               String cont = child2.getContent();
                                               mp3.setTitulo(cont);
                                       }
                                       if(child2.getName().equals("creator")){
                                               String cont = child2.getContent();
                                               mp3.setAutor(cont);
                                               if(child2.getContent().startsWith(Nombre)){
                                                       name = true;
                                               }
                                       }
                                        mp3.setID(ID);
                                        mp3.setNodo(host);
                               }
                               if(name == true){
                                       list.add(mp3);
                               }
                       }
               }
       return list;
       }
	
    private List<Mp3> FuncionNula(){
        List<Mp3> Res = new ArrayList<Mp3>();
        Mp3 Tmp = new Mp3();
        
        Tmp.setID (ID);
        Tmp.setPath( "/preg/06-40386/Desktop/Proy3/Servidor/1 Calling.mp3");
        Tmp.setTitulo ("TITULO OO");
        Tmp.setAutor  ("AUTOR"); 
        Tmp.setAlbum  ("ALBUM");
        Tmp.setAno    ("ANO"); 
        Tmp.setGenero ("ROCK");
        Tmp.setComentario ("ASDASDASDSADASD");
        Tmp.setNumero(45+"");
        Tmp.setNodo (host);
        
        Res.add(Tmp);
        return Res;
    }
    
    /**
    * Funcion que Busca por una lista de parametos de Busqueda y Luego Realiza la Busqueda por la Red en los nodos conocidos por este
    *
    * @param ID_Colsulta String Que Repreasenta Cada Consulta de Algun Cliente, Se Utiliza para Evitar Ciclos	
    * @param Colsulta String Que Representa la Consulta que se esta realizado sobre la Biblioteca Local y la de los Conocidos
    */
    public List<Mp3> Buscar(String ID_Consulta,String Consulta){

        List<Mp3> Res = new ArrayList<Mp3>();
        List<Mp3> Aux = new ArrayList<Mp3>();
        
        String Busqueda;
        String[] Tmp = Consulta.split("-");	
		
		int Del;
		if(Tmp.length>1)
			Del = 1;
		else
			Del =0;
		
        for(int i=Del;i<Tmp.length && !Visitado.contains(ID_Consulta);i++){
		
            Busqueda = Tmp[i].substring(2);
            try{
	            switch(Tmp[i].charAt(0)){
	                case 't': //Titulo
	                	Aux = BibliotecaLocalN(Busqueda);
	                    break;    
	                case 'a': //Autor
	                    Aux = BibliotecaLocalA(Busqueda);//Cambiar Aqui por La funcion que Busca por Autor
	                    break;    
	                case 'y': //Año
	                    Aux = BibliotecaLocalY(Busqueda);
	                    break;    
	                case 'l': //Album
	                    Aux = BibliotecaLocalAl(Busqueda);
	                    break;    
	                case 'g': //Genero
	                    Aux = BibliotecaLocalG(Busqueda);
	                    break;    
	                case 'c': //Comentario
	                    Aux = BibliotecaLocalC(Busqueda);
	                    break;  
	                case 'n': //Numero
	                    Aux = BibliotecaLocalNu(Busqueda);
	                    break;
	                default: //Todos
	                    Aux = BibliotecaLocalTodo();
	                    break;
	            }
            } catch (Exception e) {
		System.out.println("Error en las Busquedas de la Biblioteca Local" +e);
            }
            
            for(int j=0;j<Aux.size();j++){ //Solo Agrega las que no esten
                if(!Res.contains(Aux.get(j)))
                    Res.add(Aux.get(j));
            }
        } 

        if(!Visitado.contains(ID_Consulta)) //Busca en los demas Nodos Conocidos
            Res.addAll(BuscarRed(ID_Consulta,Consulta));

        return Res;
    }


    /**
    * Funcion que Envia la Consulta a los Nodos Conocidos y Retorna lo que Devuelven
    *
    * @param ID_Colsulta String Que Repreasenta Cada Consulta de Algun Cliente, Se Utiliza para Evitar Ciclos	
    * @param Colsulta String Que Representa la Consulta que se esta enviando a los nodos Conocidos
    */    
    private List<Mp3> BuscarRed(String ID_Consulta,String Consulta){
        List<Mp3> Res = new ArrayList<Mp3>();
        Visitado.add(ID_Consulta); //Para Verificar Ciclos
        InterfaceRemota Servidor;
        for(int i=0;i<c.length;i++){
            try{
                Servidor = (InterfaceRemota)Naming.lookup ("rmi://" + c[i] + ":" + p + "/Servidor");
                Res.addAll(Servidor.Buscar(ID_Consulta,Consulta));
                
            }catch (Exception e){
            }
        }
        return Res;
    }
    
}
