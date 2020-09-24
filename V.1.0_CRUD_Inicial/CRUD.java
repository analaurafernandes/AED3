import java.io.*;
import java.lang.reflect.Constructor;
public class CRUD<T extends Registro>
{
    /*ORDEM DO ARQUIVO
    Cabeçalho: int ultimoID (4b)
    Registro: byte lapide (1b), short tamanho (2b), int id(4b), restante do vetor de bytes */
    //atributos
    //String fileName;
    Constructor<T> construtor;
    RandomAccessFile file;
    //construtor
    public CRUD(Constructor<T> construtor, String fileName) throws IOException
    {
        //this.fileName = fileName;
        File arq = new File("dados/" + fileName);
        if(!arq.isFile())
        {
            file = new RandomAccessFile("dados/" + fileName, "rw");
            file.writeInt(0); //coloca o valor 0 como ultimo id usado caso seja um arquivo ainda nao criado
        }
        this.construtor = construtor;
    }
    public int create(T objeto) throws IOException
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
        //escrever a lapide
        file.writeByte(' '); //1 byte
        //escrever o tamanho do registro
        file.writeShort(ba.length); //2 bytes
        //escrever o vetor de bytes correspondente ao registro
        file.write(ba);

        return id;
    }
    public T read(int id) throws IOException, InstantiationException, IllegalAccessException, ReflectiveOperationException
    {
        T objeto = construtor.newInstance();
        //posicionar o ponteiro no inicio do registro
        try
        {
            file.seek(busca(id));
        }
        catch(IOException e)
        {
            objeto = null;
        }
        if(objeto != null)
        {
            //ler lapide
            byte lapide = file.readByte();
            //ler tamanho do registro
            short tam = file.readShort();
            //ler vetor de bytes do registro e criar objeto a partir desse vetor
            byte[] ba = new byte[tam];
            file.read(ba);
            objeto.fromByteArray(ba);
        }
        return objeto;
    }
    public boolean update(T objeto) throws IOException
    {
        //busca o id e retorna a posicao do mesmo no arquivo
        long pointer = busca(objeto.getID());
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
                //escrever a lapide
                file.writeByte(' '); //1 byte
                //escrever o tamanho do registro
                file.writeShort(ba.length); //2 bytes
                //escreve o vetor de bytes do registro
                file.write(ba);
            }
            //fechar o arq
        }
        //file.close();
        return true; //retorna se a operacao foi bem sucedida
    }
    public boolean delete(int id) throws IOException
    {
        //apenas buscar pelo id e marcar lapide
        long pointer = busca(id);
        boolean resp = false;
        if(pointer != -1)
        {
            file.seek(busca(id));
            file.write('*');
            resp = true;
        }
        return resp; //retorna se a operação foi bem sucedida
    }
    public long busca(int id) throws IOException
    {
        //declaração de variaveis
        file.seek(4);
        long pointer = 0;
        long lp = 0;
        char lapide = ' ';
        long tamFile = file.length();
        int idsearch = -1;
        do
        {
            try
            {
                //ler lapide
                lp = file.getFilePointer();
                lapide = (char)file.readByte();
                //ler tamanho do registro
                short tam = file.readShort();
                //checar se id é o buscado
                idsearch = file.readInt(); //lendo o id do registro
                if(lapide == '*')
                {
                    idsearch = -1; //se o registro for um registro excluido ele recebe um valor invalido pro id(nao é alterado no arquivo em si, so aqui na busca) para nao dar problema na busca
                }
                if(idsearch != id)
                {
                    if((file.getFilePointer() - 4 + tam) < file.length())
                    {
                        file.seek(file.getFilePointer() - 4 + tam); //pular o registro //o -4 é para voltar os 4 bytes do id lido
                    }
                    else
                    {
                        pointer = -1;
                    }
                }
                else
                {
                    pointer = lp; //ao encontrar o id, atribuir a pointer a posicao anterior à lápide desse registro. (4b do id, 2b do tam arq, 1b lapide)
                    
                }
            }
            catch(IOException e)
            {
                e = new IOException("O id não foi encontrado");
                pointer = -1;
            }
        
        }while(idsearch != id && file.getFilePointer() < tamFile && pointer != -1);
        
        return pointer;
    }
}
