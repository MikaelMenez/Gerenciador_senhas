import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.use

abstract class Senhas() : DAO, Criptografy {
    protected var encryptedSenha: String = ""
    fun getSenhaCriptografada(): String{
        return this.encryptedSenha
    }
    protected open var _nome: String = ""
    fun getNome(): String {
        return this._nome
    }

    protected var salt: ByteArray = ByteArray(0)
    fun getSalt(): String{
        return this.salt.toString()
    }
        // Armazenar como bytes
    protected var iv: ByteArray = ByteArray(0)
    fun getIV(): String{
        return this.iv.toString()
    }
    var connection: Connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
    // Armazenar como bytes

    constructor(senha: String, nome: String,master_senha: String) : this() {
        this.salt = generateValidSalt()   // 16 bytes
        this.iv = generateValidIV()     // 16 bytes
        this.encryptedSenha = encrypt(senha, master_senha ,salt, iv)
        this._nome = nome
    }
        fun getSenhaDecriptografada(master_senha: String): String {
            return decrypt(encryptedSenha, master_senha ,salt, iv)
        }

    override fun generateValidSalt(): ByteArray {
        val random = SecureRandom()
        val saltBytes = ByteArray(16) // 16 bytes = 128 bits
        random.nextBytes(saltBytes)
        return saltBytes
    }
    override fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            100000,     // 100.000 iterações
            256         // 256 bits = 32 bytes
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }
    override fun generateValidIV(): ByteArray {
        val random = SecureRandom()
        val ivBytes = ByteArray(16) // 16 bytes = 128 bits (EXATAMENTE)
        random.nextBytes(ivBytes)
        return ivBytes
    }

    override fun encrypt(plainText: String, master_senha: String, salt: ByteArray, iv: ByteArray): String {
        val key=deriveKey(master_senha,salt)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    override fun decrypt(encryptedText: String,master_senha: String, salt: ByteArray, iv: ByteArray): String {
        val key=deriveKey(master_senha,salt)
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

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


        override abstract fun createTable(connection: Connection): java.lang.Exception?

        override abstract fun insertData(senhas: Senhas): Exception?



    override abstract fun getByNome(senhas: Senhas,master_senha: String): Senhas?;


        override abstract fun modify(id: Int,senhas: Senhas): Exception?


    override abstract fun deleteByNome(nome:String) : Exception?
}