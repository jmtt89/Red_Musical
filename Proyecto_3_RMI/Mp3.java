/**
 *      @file Mp3.java
 *      @brief Esta Clase Guarda los parametros Relevantes de Canda Cancion que se quiera
 *      
 *      @author Jesus Torres / Julio Lopez
 *      
 *      @date 7/12/2011
 * 
 */
import java.io.Serializable;

public class Mp3 implements Serializable{

    private String titulo;
    private String autor;
    private String album;
    private String ano;
    private String genero;
    private String comentario;
    private String path;
    private String numero;
    private String ID;
    private String Nodo;

    public Mp3() {
    }

    public Mp3(String titulo, String autor, String album, String ano, String genero, String comentario, String path, String numero) {
        this.titulo = titulo;
        this.autor = autor;
        this.album = album;
        this.ano = ano;
        this.genero = genero;
        this.comentario = comentario;
        this.path = path;
        this.numero = numero;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNodo() {
        return Nodo;
    }

    public void setNodo(String Nodo) {
        this.Nodo = Nodo;
    }
    
    public boolean equals(Mp3 Object){
        return  Object.titulo == this.titulo && 
                Object.autor == this.autor && 
                Object.album == this.album && 
                Object.ano == this.ano && 
                Object.genero == this.genero && 
                Object.comentario == this.comentario && 
                Object.numero == this.numero &&
                Object.Nodo == this.Nodo;
        
    }    

}
