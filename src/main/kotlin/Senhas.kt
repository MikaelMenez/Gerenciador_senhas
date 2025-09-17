import java.security.SecureRandom
import java.sql.Connection
import java.util.Base64
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import kotlin.use

abstract class Senhas() : DAO, Criptografy {
    protected var encryptedSenha: String = ""
    protected var _nome: String = ""
        val name: String
            get() =this._nome
    protected var key: ByteArray = ByteArray(0)
        // Armazenar como bytes
    protected var iv: ByteArray = ByteArray(0)    // Armazenar como bytes

    constructor(senha: String, nome: String) : this() {
        this.key = generateValidKey()   // 16 bytes
        this.iv = generateValidIV()     // 16 bytes
        this.encryptedSenha = encrypt(senha, key, iv)
        this._nome = nome
    }
    val senhaDescriptografada: String
        get() = decrypt(encryptedSenha, key, iv)

    fun getNome(): String = _nome

    override fun generateValidKey(): ByteArray {
        val random = SecureRandom()
        val keyBytes = ByteArray(16) // 16 bytes = 128 bits
        random.nextBytes(keyBytes)
        return keyBytes
    }

    override fun generateValidIV(): ByteArray {
        val random = SecureRandom()
        val ivBytes = ByteArray(16) // 16 bytes = 128 bits (EXATAMENTE)
        random.nextBytes(ivBytes)
        return ivBytes
    }

    override fun encrypt(plainText: String, key: ByteArray, iv: ByteArray): String {
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    override fun decrypt(encryptedText: String, key: ByteArray, iv: ByteArray): String {
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun getKeyAsString(): String {
        return Base64.getEncoder().encodeToString(key)
    }

    fun getIVAsString(): String {
        return Base64.getEncoder().encodeToString(iv)
    }


        override fun createTable(connection: Connection): java.lang.Exception? {
            return TODO("Provide the return value")
        }

        override fun insertData(nome: String, senha: String, connection: Connection, iv: String, key: String) {
            val insertUsuarios = connection.prepareStatement(
                "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)"
            )
            insertUsuarios.setString(1, nome)
            insertUsuarios.setString(2, encrypt(senha,iv.toByteArray(),key.toByteArray()))
            insertUsuarios.executeUpdate()
        }

        override fun showAll(connection: Connection) {
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

        override fun getByNome(nome: String, connection: Connection): Senhas {
            val statement=connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE nome=$nome")

            return TODO("Provide the return value")
        }

        override fun modifyName(nome: String, id: Int, connection: Connection) {
            val sql = "UPDATE aplicativos SET nome = ? WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, nome)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }

        override fun modifyPassword(senha: String, id: Int, connection: Connection) {
            val sql = "UPDATE aplicativos SET senha = ? WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, senha)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }
        override fun deleteById(id: Int, connection: Connection) {
            val sql = "DELETE FROM aplicativos WHERE id = ?"
            connection.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
    }
}