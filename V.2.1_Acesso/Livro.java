import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.text.DecimalFormat;
//import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
class Livro implements Registro
{
    //atributos
    protected int id;
    protected String titulo;
    protected String autor;
    protected float preco;

    DecimalFormat df = new DecimalFormat("#,##0.00");
    //construtor
    public Livro(int id, String t, String a, float p)
    {
        this.id = id;
        this.titulo = t;
        this.autor = a;
        this.preco = p;
    }
    public Livro()
    {
        this.id = -1;
        this.titulo = "";
        this.autor = "";
        this.preco = 0F;
    }
    public String toString()
    {
        return "\nID: " + this.id +
                "\nTitulo: " + this.titulo +
                "\nAutor: " + this.autor +
                "\nPreço: " + df.format(this.preco);
    }
    //metodo que descreve o objeto livro em um vetor de bytes
    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos;  
        try
        {
            dos = new DataOutputStream(baos);
            dos.writeInt(this.id);
            dos.writeUTF(this.titulo);
            dos.writeUTF(this.autor);
            dos.writeFloat(this.preco);
        }
        catch(IOException e)
        {
            throw e;
        }
        return baos.toByteArray();
    }
    //metodo que le o objeto de um arquivo como um vetor de bytes e seta seus atributos
    public void fromByteArray(byte[] li) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(li);
        DataInputStream dis = new  DataInputStream(bais);
        //System.out.println("TAM ARRAY: " + li.length);
        this.id = dis.readInt();
        //System.out.println("PASSEI id");
        this.titulo = dis.readUTF();
        //System.out.println("PASSEI titulo");
        this.autor = dis.readUTF();
        //System.out.println("PASSEI autor");
        this.preco = dis.readFloat();
        //System.out.println("PASSEI preco"); 
    }
    public int getID()
    {
        return this.id;
    }
    public void setID(int id)
    {
        this.id = id;
    }
}