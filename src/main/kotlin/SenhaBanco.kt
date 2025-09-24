import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64

class SenhaBanco(
    senha: String,
    override var _nome: String,
    masterSenha: String
) : Senhas(senha, _nome, masterSenha) {

    private var _cvv = ""
    private var _validade = ""

    constructor(
        senha: String,
        _nome: String,
        _cvv: Int,
        _validade: String,
        masterSenha: String
    ) : this(senha, _nome, masterSenha) {
        this._cvv = encrypt(_cvv.toString(), masterSenha, salt, iv)
        this._validade = encrypt(_validade.toString(), masterSenha, salt, iv)
    }

    fun getCVV(): String {
        return this._cvv
    }

    fun getValidade(): String {
        return this._validade
    }

    override fun getByNome(senhaObj: Senhas, masterSenha: String): Senhas? {
        try {
            val statement = connection.prepareStatement("SELECT * FROM aplicativos_banco WHERE nome = ?")
            statement.setString(1, senhaObj.getNome())
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val dbSalt = Base64.getDecoder().decode(resultSet.getString("salt"))
                val dbIv = Base64.getDecoder().decode(resultSet.getString("iv"))
                val senhaDecrypted = decrypt(resultSet.getString("senha"), masterSenha, dbSalt, dbIv)
                val cvvDecrypted = decrypt(resultSet.getString("cvv"), masterSenha, dbSalt, dbIv)
                val validadeDecrypted = decrypt(resultSet.getString("validade"), masterSenha, dbSalt, dbIv)



                return SenhaBanco(senhaDecrypted, resultSet.getString("nome"), cvvDecrypted.toInt(), validadeDecrypted, masterSenha)
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun modify(id: Int, senhas: Senhas): Exception? {
        if (senhas is SenhaBanco) {
            try {
                val updateStatement = connection.prepareStatement(
                    "UPDATE aplicativos_banco SET nome = ?, senha = ?, cvv = ?, validade = ?, salt = ?, iv = ? WHERE id = ?"
                )
                updateStatement.setString(1, senhas.getNome())
                updateStatement.setString(2, senhas.getSenhaCriptografada())
                updateStatement.setString(3, senhas.getCVV())
                updateStatement.setString(4, senhas.getValidade())
                updateStatement.setString(5, senhas.getSaltBase64())
                updateStatement.setString(6, senhas.getIVBase64())
                updateStatement.setInt(7, id)
                updateStatement.executeUpdate()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return e
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
            try {
                val insertStatement = connection.prepareStatement(
                    "INSERT INTO aplicativos_banco (nome, senha, cvv, validade, salt, iv) VALUES (?, ?, ?, ?, ?, ?)"
                )
                insertStatement.setString(1, senhas.getNome())
                insertStatement.setString(2, senhas.getSenhaCriptografada())
                insertStatement.setString(3, senhas.getCVV())
                insertStatement.setString(4, senhas.getValidade())
                insertStatement.setString(5, senhas.getSaltBase64())
                insertStatement.setString(6, senhas.getIVBase64())
                insertStatement.executeUpdate()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return e
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
                    val salt = Base64.getDecoder().decode(resultSet.getString("salt"))
                    val iv = Base64.getDecoder().decode(resultSet.getString("iv"))
                    val senhaDecrypted = decrypt(resultSet.getString("senha"), masterSenha, salt, iv)
                    val nome = resultSet.getString("nome")
                    val cvvDecrypted = decrypt(resultSet.getString("cvv"), masterSenha, salt, iv).toInt()
                    val validadeDecrypted = decrypt(resultSet.getString("validade"), masterSenha, salt, iv)

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
