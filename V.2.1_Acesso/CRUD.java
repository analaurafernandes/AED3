import java.io.*;
import java.lang.reflect.Constructor;
import aed3.*;

public class CRUD<T extends Registro>
{
    /*ORDEM DO ARQUIVO
    Cabeçalho: int ultimoID (4b)
    Registro: byte lapide (1b), short tamanho (2b), int id(4b), restante do vetor de bytes */
    //atributos
    private Constructor<T> construtor;
    private RandomAccessFile file;
    private String fileName, separador;

    HashExtensivel indiceDir;
    ArvoreBMais_String_Int indiceInd;
    //construtor
    public CRUD(Constructor<T> construtor, String fileName) throws Exception
    {
        this.construtor = construtor;
        this.fileName = fileName;

        // Armazena o separador (Linux = / e Windows = \\)
        separador = System.getProperty("file.separator");

        // Apaga arquivos anteriores
        /*new File("dados"+ separador + fileName + ".db").delete();
        new File("dados"+ separador + fileName + ".diretorio.idx").delete();
        new File("dados"+ separador + fileName +".cesto.idx").delete();
        new File("dados"+ separador + fileName + ".arvore.idx").delete();*/

        File d = new File("dados");//new File(fileName);
        File arq;
        if(!d.isDirectory())
        {
            d.mkdir();
        }


        arq = new File("dados" + separador + fileName + ".db");
        if(!arq.isFile())
        {
            file = new RandomAccessFile(arq.getPath(), "rw");
	          file.writeInt(0); //coloca o valor 0 como ultimo id usado caso seja um arquivo ainda nao criado
        }
        else
        {
          file = new RandomAccessFile(arq.getPath(), "rw");
        }
        indiceDir = new HashExtensivel(10, "dados" + separador + fileName + ".diretorio.idx", "dados" + separador + fileName + ".cesto.idx"); 
        
        indiceInd = new ArvoreBMais_String_Int(5, "dados" + separador + fileName + ".arvore.idx");
    }

    // Método que exclui um diretório e seus arquivos
    boolean excluiDiretorio(File diretorio) {
        File[] conteudoDiretorio = diretorio.listFiles();
        if (conteudoDiretorio != null) {
            for (File file : conteudoDiretorio) {
                excluiDiretorio(file);
            }
        }
        return diretorio.delete();
    }

    public int create(T objeto) throws Exception
    {
        //Posicionar o ponteiro no inicio do arquivo e ler ultimo id usado
        file.seek(0);
        int id = file.readInt() + 1;
        //escreve o novo ultimo id utilizado
        file.seek(0);
        file.writeInt(id); 
        //atualizar id do objeto
        objeto.setID(id);
        //obter objeto como um array de bytes
        byte[] ba = objeto.toByteArray();
        //mover o ponteiro para o final do arquivo
        file.seek(file.length());
        long address = file.getFilePointer();
        //escrever a lapide
        file.writeByte(' '); //1 byte
        //escrever o tamanho do registro
        file.writeShort(ba.length); //2 bytes
        //escrever o vetor de bytes correspondente ao registro
        file.write(ba);
        //atualizar indices
        indiceDir.create(id, address);
        indiceInd.create(objeto.chaveSecundaria(), id);

        return id;
    }
    public T read(int id) throws Exception 
    {
        T objeto = construtor.newInstance();
        //posicionar o ponteiro no inicio do registro
        if(id != -1 && indiceDir.read(id) != -1)
        {
            file.seek(indiceDir.read(id));
            //ler lapide
            byte lapide = file.readByte();
            //ler tamanho do registro
            short tam = file.readShort();
            //ler vetor de bytes do registro e criar objeto a partir desse vetor
            byte[] ba = new byte[tam];
            file.read(ba);
            objeto.fromByteArray(ba);
        }
        else
        {
            objeto = null;
        }
        return objeto;
    }
    public T read(String chave) throws Exception
    {
        int num = indiceInd.read(chave);
        if(num != -1)
        {
            return read(num);
        }
        else
        {
            return read(-1);
        }
    }
    public boolean update(T objeto) throws Exception
    {
        boolean resp = false;
        //busca o id e retorna a posicao do mesmo no arquivo
        long pointer = indiceDir.read(objeto.getID());
        if(pointer != -1)
        {
            file.seek(pointer + 1); //andar a lapide
            short tam = file.readShort(); //ler tamanho do registro
            byte[] ba = objeto.toByteArray();
            //o registro mantem seu tamanho - somente atualizar 
            //o registro novo é menor que o antigo - somente substituir, nao diminuir o indicador de tamanho do reg
            //mantem lapide, mantem tam do registro, so escreve as novas infos
            if(ba.length <= tam)
            {
                file.write(ba);
            }
            //o registro novo é maior que o antigo - marcar a lapide e add no final do arquivo
            else
            {
                file.seek(pointer);
                file.write('*');
                //ir para o final do arquivo
                file.seek(file.length());
                //escrever registro no fim do arq
                indiceDir.delete(objeto.getID());
                indiceDir.create(objeto.getID(), file.length());
                //escrever a lapide
                file.writeByte(' '); //1 byte
                //escrever o tamanho do registro
                file.writeShort(ba.length); //2 bytes
                //escreve o vetor de bytes do registro
                file.write(ba);
            }
            resp = true;
        }
        
        return resp; //retorna se a operacao foi bem sucedida
    }
    public boolean delete(int id) throws Exception
    {
        //apenas buscar pelo id e marcar lapide
        long pointer = indiceDir.read(id);
        boolean resp = false;
        if(pointer != -1)
        {
            T objeto = construtor.newInstance();
            objeto = read(id);
            file.seek(pointer);
            file.write('*');
            indiceInd.delete(objeto.chaveSecundaria());
            indiceDir.delete(id);
            resp = true;
        }
        return resp; //retorna se a operação foi bem sucedida
    }
    
}
