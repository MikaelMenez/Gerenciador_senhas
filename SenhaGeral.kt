import java.sql.Connection

class SenhaGeral : Senhas{
    constructor(senha: String, nome: String,_cvv: Int):super(senha,nome){
        this._cvv=encrypt(_cvv.toString(),key,iv)
    }
    private var _cvv=""
    val cvv
        get()=decrypt(_cvv,key,iv)

    override fun createTable(connection: Connection): java.lang.Exception? {
        try {
            connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS aplicativos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    senha TEXT UNIQUE NOT NULL,
                    cvv TEXT UNIQUE NOT NULL,
                    validade TEXT UNIQUE NOT NULL
                    )
                   """.trimIndent()
            )
            return null
        } catch (e: Exception) {
            return e
        }
    }
    override fun getByNome(nome: String, connection: Connection): Senhas {
        val statement=connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE nome=$_nome")
        val senha = SenhaBanco(decrypt(resultSet.getString("senha"),key,iv),
            resultSet.getString("nome"),
            decrypt(resultSet.getString("cvv"),key,iv).toInt(),
            Validade.toValidade(resultSet.getString("validade"))) //precisa ser ajustado, está retornando senhaBanco
        return senha

    }
}