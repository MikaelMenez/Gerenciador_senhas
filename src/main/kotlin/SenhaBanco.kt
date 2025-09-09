import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.Locale
class SenhaBanco : Senhas {
    constructor(senha: String, nome: String,_cvv: Int,_validade: Validade):super(senha,nome){
        this._cvv=encrypt(_cvv.toString(),key,iv)
        this._validade=encrypt(_validade.toString(),key,iv)
    }
    private var _cvv=""
        val cvv
        get()=decrypt(_cvv,key,iv)
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var _validade=""
        val validade
        get() = decrypt(_validade,key,iv)

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
    override fun getById(id: Int, connection: Connection): Senhas {
            val statement=connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE id=$id")
            val senha = SenhaBanco(decrypt(resultSet.getString("senha"),key,iv),
                resultSet.getString("nome"),
                decrypt(resultSet.getString("cvv"),key,iv).toInt(),
                Validade.toData(resultSet.getString("validade")))
            return senha

    }






}