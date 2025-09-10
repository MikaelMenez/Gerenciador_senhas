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

    fun insertData(nome: String, senha: String,connection: Connection,iv: String,key: String) {
        val insertUsuarios = connection.prepareStatement(
            "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)"
        )
        insertUsuarios.setString(1, nome)
        insertUsuarios.setString(2, senha)
        insertUsuarios.executeUpdate()
    }

    fun showAll(connection: Connection) {
        val statement=connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM aplicativos")
        while (resultSet.next()) {
            println(
                "ID: ${resultSet.getInt("id")}, Nome: ${resultSet.getString("nome")}, Senha: ${
                    resultSet.getString(
                        "senha"
                    )
                }"
            )
        }
    }

    fun getByNome(nome: String,connection: Connection): Senhas

    fun modifyName(nome: String, id: Int,connection: Connection) {
        val sql = "UPDATE aplicativos SET nome = ? WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, nome)
            stmt.setInt(2, id)
            stmt.executeUpdate()
        }
    }

    fun modifyPassword(senha: String, id: Int,connection: Connection) {
        val sql = "UPDATE aplicativos SET senha = ? WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, senha)
            stmt.setInt(2, id)
            stmt.executeUpdate()
        }
    }
    fun deleteById(id: Int,connection: Connection) {
        val sql = "DELETE FROM aplicativos WHERE id = ?"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }
}