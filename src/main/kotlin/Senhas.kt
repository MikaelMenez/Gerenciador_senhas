import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import kotlin.use

abstract class Senhas() : DAO, Criptografy {
    abstract val nome: String
    protected var encryptedSenha: String = ""
    protected var _nome: String = ""
        val name: String
            get() =this._nome
    protected var salt: ByteArray = ByteArray(0)
        // Armazenar como bytes
    protected var iv: ByteArray = ByteArray(0)
    var connection: Connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
    // Armazenar como bytes

    constructor(senha: String, nome: String) : this() {
        this.salt = generateValidSalt()   // 16 bytes
        this.iv = generateValidIV()     // 16 bytes
        this.encryptedSenha = encrypt(senha, salt, iv)
        this._nome = nome
    }
    val senhaDescriptografada: String
        get() = decrypt(encryptedSenha, salt, iv)

    override fun generateValidSalt(): ByteArray {
        val random = SecureRandom()
        val saltBytes = ByteArray(16) // 16 bytes = 128 bits
        random.nextBytes(saltBytes)
        return saltBytes
    }

    override fun generateValidIV(): ByteArray {
        val random = SecureRandom()
        val ivBytes = ByteArray(16) // 16 bytes = 128 bits (EXATAMENTE)
        random.nextBytes(ivBytes)
        return ivBytes
    }

    override fun encrypt(plainText: String, salt: ByteArray, iv: ByteArray): String {
        val saltSpec = SecretKeySpec(salt, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, saltSpec, ivSpec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    override fun decrypt(encryptedText: String, salt: ByteArray, iv: ByteArray): String {
        val saltSpec = SecretKeySpec(salt, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, saltSpec, ivSpec)

        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun getsaltAsString(): String {
        return Base64.getEncoder().encodeToString(salt)
    }

    fun getIVAsString(): String {
        return Base64.getEncoder().encodeToString(iv)
    }


        override fun createTable(connection: Connection): java.lang.Exception? {
            return TODO("Provide the return value")
        }

        override fun insertData(nome: String, senha: String, iv: String, salt: String) {
            val insertUsuarios = this.connection.prepareStatement(
                "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)"
            )
            insertUsuarios.setString(1, nome)
            insertUsuarios.setString(2, encrypt(senha,iv.toByteArray(),salt.toByteArray()))
            insertUsuarios.executeUpdate()
        }



    override fun getByNome(senhas: Senhas): Senhas {
        val sql = "SELECT * FROM aplicativos WHERE nome = ?"
        val statement = this.connection.prepareStatement(sql)
        statement.setString(1, nome) // Usando PreparedStatement para evitar SQL injection

        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            TODO("polimorfism")
        } else {
            throw NoSuchElementException("Nenhum registro encontrado para o nome: $nome")
        }
    }

        override fun modifyName(nome: String, id: Int) {
            val sql = "UPDATE aplicativos SET nome = ? WHERE id = ?"
            this.connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, nome)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }

        override fun modifyPassword(senha: String, id: Int) {
            val sql = "UPDATE aplicativos SET senha = ? WHERE id = ?"
            this.connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, senha)
                stmt.setInt(2, id)
                stmt.executeUpdate()
            }
        }
    override fun deleteByNome(nome:String) {
        val sql = "DELETE FROM aplicativos WHERE nome = ?"
        this.connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, nome)
            stmt.executeUpdate()
        }

    }
}