/**
 *      @file InterfaceRemota.java
 *      @brief Esta es la Interface que muesta las Funciones Visibles para El Cliente y su firma
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 7/12/2011
 * 
 */
import java.rmi.*;
import java.io.Serializable;
import java.util.*;
import java.io.*;

public interface InterfaceRemota extends Remote {

    /**
    * Funcion que Enviar el Archivo que se Quiere Descargar
    *
    * @param Path Direccion Del Archivo que se Quiere Descargar
    */
    public File Descargar(String Path) throws RemoteException;

    /**
    * Funcion que Revisa los Nodos Conectados a este
    *
    * @param ID_Colsulta String Que Repreasenta Cada Consulta de Algun Cliente, Se Utiliza para Evitar Ciclos
    */
    public List<String> RevisarRed(String ID) throws RemoteException;

    /**
    * Funcion que Busca por una lista de parametos de Busqueda y Luego Realiza la Busqueda por la Red en los nodos conocidos por este
    *
    * @param ID_Colsulta String Que Repreasenta Cada Consulta de Algun Cliente, Se Utiliza para Evitar Ciclos	
    * @param Colsulta String Que Representa la Consulta que se esta realizado sobre la Biblioteca Local y la de los Conocidos
    */
    public List<Mp3> Buscar(String ID,String Consulta) throws RemoteException;

}
