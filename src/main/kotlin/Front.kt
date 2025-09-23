import javax.swing.JOptionPane

class Front {
    var senhasB: HashMap<String, SenhaBanco>?
    var senhasG: HashMap<String, SenhaGeral>?
    var senhaMestre: String? = null

    constructor(master_senha: String) {
        senhasB = SenhaBanco.getAll(master_senha) ?: HashMap()
        senhasG = SenhaGeral.getAll(master_senha) ?: HashMap()
        senhaMestre = master_senha
    }

    fun start() {
        open()
    }

    fun open() {
        JOptionPane.showMessageDialog(
            null, "Para cadastrar Senha, Digite: 1\n" +
                    "Para Acessar Senhas, Digite: 2\n" +
                    "...\n" +
                    "Para Fechar o APP, Digite: 9"
        )
        val opcao = JOptionPane.showInputDialog("Qual ação deve ser executada?")?.toIntOrNull()
        when (opcao) {
            1 -> cadastra()
            2 -> selecaoMenu()
            9 -> JOptionPane.showMessageDialog(null, "Até o próximo Gerensenhamento")
            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                open()
            }
        }
    }

    fun cadastra() {
        val opcao = JOptionPane.showInputDialog(
            "Para cadastrar Senha de Banco, digite: 1\n" +
                    "Para cadastrar Senha Geral, Digite: 2\n" +
                    "Para voltar ao menu anterior, Digite: 3"
        )?.toIntOrNull()

        if (opcao == null) {
            JOptionPane.showMessageDialog(null, "Entrada inválida, tente novamente")
            cadastra()
            return
        }

        when (opcao) {
            1 -> {
                val senha = JOptionPane.showInputDialog("Digite sua nova senha") ?: ""
                val nome = JOptionPane.showInputDialog("Digite o nome do Titular") ?: ""
                val cvv = JOptionPane.showInputDialog("Digite o cvv")?.toIntOrNull() ?: 0
                val data = Validade.toValidade(JOptionPane.showInputDialog("Digite a validade") ?: "")
                val senhaNova = SenhaBanco(senha, nome, cvv, data, senhaMestre ?: "")
                senhaNova.insertData(senhaNova)
                senhasB?.put(senhaNova.getNome(), senhaNova)
                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!")
                open()
            }
            2 -> {
                val senha = JOptionPane.showInputDialog("Digite sua nova senha") ?: ""
                val nome = JOptionPane.showInputDialog("Digite o nome da sua Senha") ?: ""
                val senhaNovaG = SenhaGeral(senha, nome, senhaMestre ?: "")
                senhaNovaG.insertData(senhaNovaG)
                senhasG?.put(senhaNovaG.getNome(), senhaNovaG)
                JOptionPane.showMessageDialog(null, "Senha nova Adicionada com sucesso!")
                open()
            }
            3 -> open()
            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                cadastra()
            }
        }
    }

    fun selecaoMenu() {
        val escolha = JOptionPane.showInputDialog(
            "Para consultar senhas de Banco: Digite 1 \n" +
                    "Para consultar senhas Gerais: Digite 2 \n" +
                    "Para retornar ao menu anterior: Digite 3"
        )?.toIntOrNull()

        when (escolha) {
            1 -> {
                if (senhasB.isNullOrEmpty()) {
                    JOptionPane.showMessageDialog(null, "Você não possui senhas de banco cadastradas")
                    open()
                } else MenuBanco()
            }
            2 -> {
                if (senhasG.isNullOrEmpty()) {
                    JOptionPane.showMessageDialog(null, "Você não possui senhas cadastradas")
                    open()
                } else menuGeral()
            }
            3 -> open()
            else -> {
                JOptionPane.showMessageDialog(null, "Ação inválida, tente novamente")
                selecaoMenu()
            }
        }
    }

    fun MenuBanco() {
        JOptionPane.showMessageDialog(null, "Você tem ${senhasB?.size ?: 0} senhas de Banco")

        val nomeBusca = JOptionPane.showInputDialog("Digite o nome do titular da senha que deseja acessar:")

        if (nomeBusca.isNullOrBlank() || !senhasB!!.containsKey(nomeBusca)) {
            JOptionPane.showMessageDialog(null, "Nome não encontrado, tente novamente!")
            MenuBanco()
            return
        }

        val senha = senhasB!![nomeBusca]!!

        JOptionPane.showMessageDialog(null, "Nome do Títular: ${senha.getNome()}")
        JOptionPane.showMessageDialog(null, "Senha do Cartão: ${senha.getSenhaDecriptografada(senhaMestre ?: "")}")
        JOptionPane.showMessageDialog(null, "Validade do Cartão: ${senha.getValidade()}")

        open()
    }

    fun menuGeral() {
        JOptionPane.showMessageDialog(null, "Você tem ${senhasG?.size ?: 0} senhas")

        val nomeBusca = JOptionPane.showInputDialog("Digite o nome da senha que deseja acessar:")

        if (nomeBusca.isNullOrBlank() || !senhasG!!.containsKey(nomeBusca)) {
            JOptionPane.showMessageDialog(null, "Nome não encontrado, tente novamente!")
            menuGeral()
            return
        }

        val senha = senhasG!![nomeBusca]!!

        JOptionPane.showMessageDialog(null, "Título da Senha: ${senha.getNome()}")
        JOptionPane.showMessageDialog(null, "Senha: ${senha.getSenhaDecriptografada(senhaMestre ?: "")}")

        open()
    }
}
