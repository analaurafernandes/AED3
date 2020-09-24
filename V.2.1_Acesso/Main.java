package Acesso;
import java.io.*;
import java.util.*;
public class Main
{

    private static CRUD<Usuario> arqUsers; 

    public static void novoUsuario() throws Exception
    {
        arqUsers = new CRUD<>(Usuario.class.getConstructor(), "usuarios");
        Usuario user;
        //INTERFACE DE ACESSO
        System.out.println();
        System.out.println("NOVO USUÁRIO");
        System.out.println();
        System.out.print("E-mail: ");
        //receber o email
        BufferedReader leitor = new BufferedReader(new InputStreamReader(System.in));
        String email = leitor.readLine();
        String nome, senha;
        if(email.equals(""))
            menu();
        //caso o email nao tenha sido usado, solicitar os demais dados do usuario
        else if(arqUsers.read(email) == null) 
        {
            System.out.println();
            System.out.println("Insira o seu nome de usuário:");
            nome = leitor.readLine();
            System.out.println();
            System.out.println("Insira a sua senha:");
            senha = leitor.readLine();
            System.out.println();
            System.out.println("Confirma seus dados?");
            System.out.println();
            System.out.println("Nome de usuário: " + nome);
            System.out.println("E-mail: " + email);
            System.out.println("Senha: " + senha);
            System.out.println();
            System.out.println("Digite para confirmar (s/n):_");
            String conf = leitor.readLine();
            if(conf.equals("s") || conf.equals("S"))
            {
                user = new Usuario();
                user.setEmail(email);
                user.setNome(nome);
                user.setSenha(senha);
                System.out.println(user.toString());
                //gravar no arquivo
                arqUsers.create(user); 
                System.out.println("Usuário criado com sucesso.");
                menu();

            }
            else
                menu();
        }
        else
        {
            System.out.println("E-mail já cadastrado.");
            novoUsuario();
        }
    }
    public static void acessoUsuario() throws Exception
    {
        arqUsers = new CRUD<>(Usuario.class.getConstructor(), "usuarios");
        BufferedReader leitor = new BufferedReader(new InputStreamReader(System.in));
        //tela de login
        System.out.println();
        System.out.println("LOGIN");
        System.out.println("======");
        System.out.println();
        System.out.print("E-mail: ");
        //solicitar email
        String email = leitor.readLine();
        System.out.println();
        //buscar usuario no banco de dados
        Usuario user = arqUsers.read(email);
        if(user == null)
        {
            System.out.println("E-mail/Usuário não cadastrado.");
            menu();
        }
        else
        {
            //solicitar senha
            System.out.print("Senha: ");
            String senha = leitor.readLine();
            System.out.println();
            //comparar com a senha do usuario
            if(user.getSenha().equals(senha))
            {
                //redirecionar para a tela principal que ainda nao existe
                //entao mostro uma mensagem de sucesso no login e volto pro menu
                System.out.println("SUCESSO! Login realizado.");
                menu();
            }
            else
            {
                //mensagem de erro e retornar ao menu
                System.out.println("Senha inválida.");
                menu();
            }
        }


    }
    public static void menu() throws Exception
    {
        //INTERFACE DE ACESSO
        System.out.println();
        System.out.println("Perguntas 1.0");
        System.out.println("=============");
        System.out.println();
        System.out.println("ACESSO");
        System.out.println();
        System.out.println();
        System.out.println("1) Acesso ao sistema");
        System.out.println("2) Novo usuário (Primeiro acesso)");
        System.out.println();
        System.out.println("0) Sair");
        System.out.println();
        System.out.println();
        System.out.print("Opção: ");

        //receber a opção escolhida
        Scanner leitor = new Scanner(System.in);
        int x = Integer.parseInt(leitor.nextLine());
        
        //leitor.close();
        switch(x)
        {
            case 0:
								System.out.println("Obrigado por utilizar esse programa!");
                break;
            case 1:
                acessoUsuario();
                break;
            case 2:
                novoUsuario();
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }
    public static void main(String[] args) throws Exception
    {
        new File("dados/usuarios.db").delete();
        new File("dados/usuarios.diretorio.idx").delete();
        new File("dados/usuarios.arvore.idx").delete();
        new File("dados/usuarios.cesto.idx").delete();
         
        menu();
    }
    
}

