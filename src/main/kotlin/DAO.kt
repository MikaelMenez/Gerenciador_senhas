import java.sql.Connection
import kotlin.use

interface DAO {
    fun createTable(connection: Connection): java.lang.Exception? {
        try {
            connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS aplicativos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    senha TEXT UNIQUE NOT NULL
                    )
                   """.trimIndent()
            )
            return null
        } catch (e: Exception) {
            return e
        }
    }

    fun insertData(nome: String, senha: String,iv: String,salt: String) {
        val insertUsuarios = connection.prepareStatement(
            "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)"
        )
        insertUsuarios.setString(1, nome)
        insertUsuarios.setString(2, senha)
        insertUsuarios.executeUpdate()
    }

    fun getByNome(senha:Senhas): Senhas

    fun modifyName(nome: String, id: Int)


    fun modifyPassword(senha: String, id: Int)

    fun deleteByNome(nome: String)
}