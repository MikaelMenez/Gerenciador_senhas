import java.sql.DriverManager

class DbController {
    companion object {
        val key="1234567890123456"
        val iv="abcdefghijklmnop"
        val connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
        val statement = connection.createStatement()

        fun createDB(): java.lang.Exception? {
            try {
                this.statement.execute(
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

        fun insertData(nome: String, senha: String) {
            val insertUsuarios = connection.prepareStatement(
                "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)"
            )
            insertUsuarios.setString(1, nome)
            insertUsuarios.setString(2, CriptografyController.encrypt(senha,iv,key))
            insertUsuarios.executeUpdate()
        }

        fun showAll() {
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

        fun getOne(id: Int): Senhas {
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE id=" + id)
            val senha = Senhas(resultSet.getString("senha"), resultSet.getString("nome"))
            return senha
        }

        fun modifyName(nome: String, id: Int) {
            val sql = "UPDATE aplicativos SET nome = ? WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, nome)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }

        fun modifyPassword(senha: String, id: Int) {
            val sql = "UPDATE aplicativos SET senha = ? WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, senha)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }
        fun deleteById(id: Int) {
            val sql = "DELETE FROM aplicativos WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
}
}