import javax.swing.JOptionPane;
import java.util.ArrayList;


public class Front {
    public ArrayList<SenhaBanco> senhasB;
    public ArrayList<SenhaGeral> senhasG;
    public String senhaMestre = null;

    public Front(ArrayList<SenhaBanco> senhasB, ArrayList<SenhaGeral> senhasG) {
        this.senhasB = senhasB;
        this.senhasG = senhasG;
    }

    public Front() {

    }

    public void Start(){
        JOptionPane.showMessageDialog(null, "Bem Vindo ao Gerensenhador");
        if(senhaMestre == null){
            this.senhaMestre = JOptionPane.showInputDialog("Cadastre uma senha mestre para Iniciar o APP");
            Open();
        } else{
            String senhaMestra = JOptionPane.showInputDialog("Faça login no APP com sua senha mestre cadastrada");
            if(senhaMestra == this.senhaMestre){
                Open();
            }else{
                JOptionPane.showMessageDialog(null, "Erro, senha mestre incorreta, tente novamente");
                Start();
            }
        }
    }

    public void Open(){
        JOptionPane.showMessageDialog(null, "Para Cadastrar Senha, Digite: 1\n" +
                                                                   "Para Acessar Senhas, Digite: 2" +
                                                                   "..." +
                                                                   "Para Fechar o APP, Digite: 9");
        int opcao = Integer.parseInt(JOptionPane.showInputDialog("Qual ação deve ser executada?"));
        switch (opcao) {
            case (1):
                Cadastra();
            case(2):
                Menu();
            case(9):
                JOptionPane.showMessageDialog(null, "Até o próximo Gerensenhamento");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente");
                Open();
        }
    }

    public void Cadastra(){
        int opcao = Integer.parseInt(JOptionPane.showInputDialog("Para cadastrar Senha de Banco, digite: 1\n" +
                                                                 "Para cadastrar Senha Geral, Digite: 2\n" +
                                                                 "Para voltar ao menu anterior, Digite: 3"));

        String senha;
        String nome;
        int cvv;

        switch (opcao) {
            case(1):
                senha = JOptionPane.showInputDialog("Digite sua nova senha");
                nome = JOptionPane.showInputDialog("Digite o nome do Titular");
                cvv = Integer.parseInt(JOptionPane.showInputDialog("Digite a chave ce criptografia"));
                int mes = Integer.parseInt(JOptionPane.showInputDialog("Digite o mês de validade"));
                int ano = Integer.parseInt(JOptionPane.showInputDialog("Digite o ano de validade"));

                Validade data = new Validade(mes, ano);
                SenhaBanco senhaNova = new SenhaBanco(senha, nome, cvv, data);

                senhasB.add(senhaNova);
                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!");
                Open();
            case(2):
                senha = JOptionPane.showInputDialog("Digite sua nova senha");
                nome = JOptionPane.showInputDialog("Digite o nome do Titular");
                cvv = Integer.parseInt(JOptionPane.showInputDialog("Digite a chave ce criptografia"));

                SenhaGeral senhaNovaG = new SenhaGeral(senha, nome, cvv);

                senhasG.add(senhaNovaG);

                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!");
                Open();
            case(3):
                Open();

            default:
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente");
                Cadastra();
        }
    }

    public void Menu(){


        Open();
    }
}
