import javax.swing.JOptionPane

class Front {
    var senhasB: ArrayList<SenhaBanco?>
    var senhasG: ArrayList<SenhaGeral?>
    var senhaMestre: String? = null

    constructor(senhasB: ArrayList<SenhaBanco?>, senhasG: ArrayList<SenhaGeral?>) {
        this.senhasB = senhasB
        this.senhasG = senhasG
    }

    constructor() {
        senhasB = ArrayList<SenhaBanco?>()
        senhasG = ArrayList<SenhaGeral?>()
    }

    fun Start() {
        JOptionPane.showMessageDialog(null, "Bem Vindo ao Gerensenhador")
        if (senhaMestre == null) {
            this.senhaMestre = JOptionPane.showInputDialog("Cadastre uma senha mestre para Iniciar o APP")
            Open()
        } else {
            val senhaMestra = JOptionPane.showInputDialog("Faça login no APP com sua senha mestre cadastrada")
            if (senhaMestra === this.senhaMestre) {
                Open()
            } else {
                JOptionPane.showMessageDialog(null, "Erro, senha mestre incorreta, tente novamente")
                Start()
            }
        }
    }

    fun Open() {
        JOptionPane.showMessageDialog(
            null, "Para Cadastrar Senha, Digite: 1\n" +
                    "Para Acessar Senhas, Digite: 2\n" +
                    "...\n" +
                    "Para Fechar o APP, Digite: 9"
        )
        val opcao = JOptionPane.showInputDialog("Qual ação deve ser executada?").toInt()
        when (opcao) {
            (1) -> {
                Cadastra()
            }

            (2) -> {
                SelecaoMenu()
            }

            (9) -> {
                JOptionPane.showMessageDialog(null, "Até o próximo Gerensenhamento")
            }

            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                Open()
            }
        }
    }

    fun Cadastra() {
        val opcao = JOptionPane.showInputDialog(
            "Para cadastrar Senha de Banco, digite: 1\n" +
                    "Para cadastrar Senha Geral, Digite: 2\n" +
                    "Para voltar ao menu anterior, Digite: 3"
        ).toInt()

        var senha: String
        var nome: String
        var cvv: Int

        when (opcao) {
            (1) -> {
                senha = JOptionPane.showInputDialog("Digite sua nova senha")
                nome = JOptionPane.showInputDialog("Digite o nome do Titular")
                cvv = JOptionPane.showInputDialog("Digite a chave ce criptografia").toInt()
                val mes = JOptionPane.showInputDialog("Digite o mês de validade").toInt()
                val ano = JOptionPane.showInputDialog("Digite o ano de validade").toInt()

                val data = Validade(mes, ano)
                val senhaNova = SenhaBanco(senha, nome, cvv, data)

                senhasB.add(senhaNova)
                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!")
                Open()
            }

            (2) -> {
                senha = JOptionPane.showInputDialog("Digite sua nova senha")
                nome = JOptionPane.showInputDialog("Digite o nome da sua Senha")
                cvv = JOptionPane.showInputDialog("Digite a chave de criptografia").toInt()

                val senhaNovaG = SenhaGeral(senha, nome, cvv)

                senhasG.add(senhaNovaG)

                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!")
                Open()
            }

            (3) -> {
                Open()
            }

            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                Cadastra()
            }
        }
    }

    fun SelecaoMenu() {
        val escolha =
            JOptionPane.showInputDialog("Para consultar senhas de Banco: Digite 1 \nPara consultar senhas Gerais: Digite 2 \nPara retornar ao menu anterior: Digite 3")
                .toInt()

        when (escolha) {
            (1) -> {
                if(senhasB.size < 1){
                    JOptionPane.showMessageDialog(null, "Você não possui senhas de banco cadastradas")
                    Open()
                }
                else {
                    MenuBanco()
                }
            }

            (2) -> {
                if(senhasG.size < 1){
                    JOptionPane.showMessageDialog(null, "Você não possui senhas cadastradas")
                    Open()
                }
                else {
                    MenuGeral()
                }
            }

            (3) -> {
                Open()
            }

            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                SelecaoMenu()
            }
        }
    }

    fun MenuBanco() {
        JOptionPane.showMessageDialog(null, "Você tem" + senhasB.size + " senhas de Banco")

        try {
            val indexBusca =
                JOptionPane.showInputDialog("Qual o dentre suas " + senhasB.size + " você deseja acessar? (digite um número de 1 à " + senhasB.size + ")")
                    .toInt() - 1

            if (indexBusca > senhasB.size) {
                JOptionPane.showMessageDialog(null, "Index de busca inexistente, tente novamente!")
                MenuBanco()
            }

            val nome: String?
            val senha = senhasB.get(indexBusca)
            if (senha != null) {
                nome = senha.getNome()
            } else {
                nome = "Nome não encontrado"
            }
            JOptionPane.showMessageDialog(null, "Nome do Títular: " + nome)
            JOptionPane.showMessageDialog(null, "Senha do Cartão: " + senhasB.get(indexBusca)!!.senhaDescriptografada)
            JOptionPane.showMessageDialog(null, "Validade do Cartão: 0" + senhasB.get(indexBusca)!!.validade.toString())
        } catch (e: Exception) {
            println(e)
            JOptionPane.showMessageDialog(null, "Erro de execução, tente novamente!")
            MenuBanco()
        }
        Open()
    }

    fun MenuGeral() {
        JOptionPane.showMessageDialog(null, "Você tem" + senhasG.size + " senhas")

        try {
            val indexBusca =
                JOptionPane.showInputDialog("Qual o dentre suas " + senhasG.size + " você deseja acessar? (digite um número de 1 à " + senhasG.size + ")")
                    .toInt() - 1

            if (indexBusca > senhasG.size) {
                JOptionPane.showMessageDialog(null, "Index de busca inexistente, tente novamente!")
                MenuBanco()
            }

            val nome: String?
            val senha = senhasG.get(indexBusca)
            if (senha != null) {
                nome = senha.getNome()
            } else {
                nome = "Nome não encontrado"
            }
            JOptionPane.showMessageDialog(null, "Título da Senha: " + nome)
            JOptionPane.showMessageDialog(null, "Senha: " + senhasG.get(indexBusca)!!.senhaDescriptografada)
        } catch (e: Exception) {
            println(e)
            JOptionPane.showMessageDialog(null, "Erro de execução, tente novamente!")
            MenuBanco()
        }
        Open()
    }
}
