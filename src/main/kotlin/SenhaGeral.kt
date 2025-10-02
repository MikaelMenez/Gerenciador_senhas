import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64

class SenhaGeral(senha: String, override var nome: String, masterSenha: String) : Senhas(senha, nome, masterSenha) {

    override fun getByNome(senhaObj: Senhas, masterSenha: String): Senhas? {
        try {
            val statement = connection.prepareStatement("SELECT * FROM aplicativos WHERE nome = ?")
            statement.setString(1, senhaObj.getNome())
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val dbSalt = Base64.getDecoder().decode(resultSet.getString("salt"))
                val dbIv = Base64.getDecoder().decode(resultSet.getString("iv"))
                val decryptedSenha = decrypt(resultSet.getString("senha"), masterSenha, dbSalt, dbIv)

                return SenhaGeral(
                    decryptedSenha,
                    resultSet.getString("nome"),
                    masterSenha
                )
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun modify(id: Int, senhas: Senhas): Exception? {
        if (senhas is SenhaGeral) {
            try {
                val updateStatement = connection.prepareStatement(
                    "UPDATE aplicativos SET nome = ?, senha = ?, salt = ?, iv = ? WHERE id = ?"
                )
                updateStatement.setString(1, senhas.getNome())
                updateStatement.setString(2, senhas.getSenhaCriptografada())
                updateStatement.setString(3, senhas.getSaltBase64())
                updateStatement.setString(4, senhas.getIVBase64())
                updateStatement.setInt(5, id)
                updateStatement.executeUpdate()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return e
            }
        }
        return Exception("Objeto não é do tipo SenhaGeral")
    }

    override fun deleteByNome(nome: String): Exception? {
        try {
            val deleteStatement = connection.prepareStatement(
                "DELETE FROM aplicativos WHERE nome = ?"
            )
            deleteStatement.setString(1, nome)
            deleteStatement.execute()
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return e
        }
    }

    override fun insertData(senhas: Senhas): Exception? {
        if (senhas is SenhaGeral) {
            try {
                val insertStatement = connection.prepareStatement(
                    "INSERT INTO aplicativos (nome, senha, salt, iv) VALUES (?, ?, ?, ?)"
                )
                insertStatement.setString(1, senhas.getNome())
                insertStatement.setString(2, senhas.getSenhaCriptografada())
                insertStatement.setString(3, senhas.getSaltBase64())
                insertStatement.setString(4, senhas.getIVBase64())
                insertStatement.executeUpdate()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return e
            }
        }
        return Exception("Objeto não é do tipo SenhaGeral")
    }

    companion object {
        fun createTable(connection: Connection): Exception? {
            return try {
                val statement = connection.prepareStatement(
                    """
                    CREATE TABLE IF NOT EXISTS aplicativos(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        senha TEXT NOT NULL,
                        salt TEXT NOT NULL,
                        iv TEXT NOT NULL
                    )
                """.trimIndent()
                )
                statement.execute()
                null
            } catch (e: Exception) {
                e.printStackTrace()
                e
            }
        }

        fun getAll(masterSenha: String): HashMap<String, SenhaGeral>? {
            val senhas = HashMap<String, SenhaGeral>()
            val connection: Connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
            try {
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM aplicativos")
                while (resultSet.next()) {
                    val salt = Base64.getDecoder().decode(resultSet.getString("salt"))
                    val iv = Base64.getDecoder().decode(resultSet.getString("iv"))
                    val senhaDecrypted = decrypt(resultSet.getString("senha"), masterSenha, salt, iv)
                    val nome = resultSet.getString("nome")

                    val senhaGeral = SenhaGeral(senhaDecrypted, nome, masterSenha)
                    senhas.put(nome,senhaGeral)
                }
                return senhas
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}
