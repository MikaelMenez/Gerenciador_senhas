import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.*

inline fun <T> Connection.usePrepared(
    sql: String,
    setParams: PreparedStatement.() -> Unit,
    exec: (PreparedStatement) -> T
): T {
    return this.prepareStatement(sql).use { stmt ->
        stmt.setParams()
        exec(stmt)
    }
}

class SenhaBanco(
    senha: String,
    nome: String,
    masterSenha: String
) : Senhas(senha, nome, masterSenha) {
    private var cvv = ""
    private var validade = ""

    constructor(
        senha: String,
        nome: String,
        cvv: Int,
        validade: String,
        masterSenha: String
    ) : this(senha, nome, masterSenha) {
        this.cvv = encrypt(cvv.toString(), masterSenha, salt, iv)
        this.validade = encrypt(validade.toString(), masterSenha, salt, iv)
    }

    fun getCVV(): String {
        return this.cvv
    }

    fun getValidade(): String {
        return this.validade
    }

    override fun getByNome(senhaObj: Senhas, masterSenha: String): Senhas? {
        return try {
            val statement = connection.prepareStatement("SELECT * FROM aplicativos_banco WHERE nome = ?")
            statement.setString(1, senhaObj.getNome())
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val dbSalt = Base64.getDecoder().decode(resultSet.getString("salt"))
                val dbIv = Base64.getDecoder().decode(resultSet.getString("iv"))
                val senhaDecrypted = decrypt(resultSet.getString("senha"), masterSenha, dbSalt, dbIv)
                val cvvDecrypted = decrypt(resultSet.getString("cvv"), masterSenha, dbSalt, dbIv)
                val validadeDecrypted = decrypt(resultSet.getString("validade"), masterSenha, dbSalt, dbIv)

                SenhaBanco(
                    senhaDecrypted,
                    resultSet.getString("nome"),
                    cvvDecrypted.toInt(),
                    validadeDecrypted,
                    masterSenha
                )
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun modify(id: Int, senhas: Senhas): Exception? {
        if (senhas is SenhaBanco) {
            return try {
                connection.usePrepared(
                    "UPDATE aplicativos_banco SET nome = ?, senha = ?, cvv = ?, validade = ?, salt = ?, iv = ? WHERE id = ?",
                    {
                        setString(1, senhas.getNome())
                        setString(2, senhas.getSenhaCriptografada())
                        setString(3, senhas.getCVV())
                        setString(4, senhas.getValidade())
                        setString(5, senhas.getSaltBase64())
                        setString(6, senhas.getIVBase64())
                        setInt(7, id)
                    },
                    { it.executeUpdate() }
                )
                null
            } catch (e: Exception) {
                e.printStackTrace()
                e
            }
        }

        return Exception("Objeto não é do tipo SenhaBanco")
    }

    override fun deleteByNome(nome: String): Exception? {
        try {
            val deleteStatement = connection.prepareStatement(
                "DELETE FROM aplicativos_banco WHERE nome = ?"
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
        if (senhas is SenhaBanco) {
            return try {
                connection.usePrepared(
                    "INSERT INTO aplicativos_banco (nome, senha, cvv, validade, salt, iv) VALUES (?, ?, ?, ?, ?, ?)",
                    {
                        setString(1, senhas.getNome())
                        setString(2, senhas.getSenhaCriptografada())
                        setString(3, senhas.getCVV())
                        setString(4, senhas.getValidade())
                        setString(5, senhas.getSaltBase64())
                        setString(6, senhas.getIVBase64())
                    },
                    { it.executeUpdate() }
                )
                null
            } catch (e: Exception) {
                e.printStackTrace()
                e
            }
        }

        return Exception("Objeto não é do tipo SenhaBanco")
    }

    companion object {
        fun createTable(connection: Connection): Exception? {
            return try {
                val statement = connection.prepareStatement(
                    """
                    CREATE TABLE IF NOT EXISTS aplicativos_banco(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        senha TEXT NOT NULL,
                        cvv TEXT NOT NULL,
                        validade TEXT NOT NULL,
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

        fun getAll(masterSenha: String): HashMap<String, SenhaBanco>? {
            val senhas = HashMap<String, SenhaBanco>()
            val connection: Connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
            try {
                val statement = connection.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM aplicativos_banco")
                while (resultSet.next()) {
                    val nome = resultSet.getDecryptedField("nome", masterSenha)
                    val senhaDecrypted = resultSet.getDecryptedField("senha", masterSenha)
                    val cvvDecrypted = resultSet.getDecryptedField("cvv", masterSenha).toInt()
                    val validadeDecrypted = resultSet.getDecryptedField("validade", masterSenha)

                    val senhaBanco = SenhaBanco(senhaDecrypted, nome, cvvDecrypted, validadeDecrypted, masterSenha)
                    senhas[nome] = senhaBanco
                }
                return senhas
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}
