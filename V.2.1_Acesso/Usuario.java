package Acesso;

public class Usuario {
    
}
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

class Usuario implements Registro
{   
    protected int idUsuario;
    protected String nome;
    protected String email;
    protected String senha;
    
    public Usuario(int idUsuario, String nome, String email, String senha)
    {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario()
    {
        this.idUsuario = -1;
        this.nome = "";
        this.email = "";
        this.senha = "";
    }

    public String toString()
    {
        return "\nID: " + this.idUsuario +
                "\nNome: " + this.nome +
                "\nE-mail: " + this.email +
                "\nSenha: " + this.senha;
    }

    public void setID(int id)
    { 
      idUsuario = id; 
    }

    public int getID()
    { 
      return idUsuario; 
    }

    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos;  

        dos = new DataOutputStream(baos);
        dos.writeInt(this.idUsuario);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.senha);
    
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] li) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(li);
        DataInputStream dis = new  DataInputStream(bais);
        
        this.idUsuario = dis.readInt();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.senha = dis.readUTF();
    }

    public String chaveSecundaria() 
    { 
      return this.email; 
    }

    public void setNome(String nome)
    { 
      this.nome = nome; 
    }

    public String getNome()
    { 
      return this.nome; 
    }

    public void setEmail(String email)
    { 
      this.email = email; 
    }

    public String getEmail()
    { 
      return this.email; 
    }

    public void setSenha(String senha)
    { 
      this.senha = senha; 
    }

    public String getSenha()
    { 
      return this.senha; 
    }
}
