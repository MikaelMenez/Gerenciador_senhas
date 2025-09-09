import java.sql.Connection
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
class SenhaBanco : Senhas {
    private var cvv=""
    private var dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var validade=""
    constructor(senha: String, nome: String,cvv: Int,validade: Date):super(senha,nome){
        this.cvv=encrypt(cvv.toString(),key,iv)
        this.validade=encrypt(validade.toString(),key,iv)
    }
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
                resultSet.getDate("validade"))
            return senha

    }






}