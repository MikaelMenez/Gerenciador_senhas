import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

abstract class Senhas() : DAO {

    protected var encryptedSenha: String = ""

    fun getSenhaCriptografada(): String {
        return this.encryptedSenha
    }

    protected open var _nome: String = ""

    fun getNome(): String {
        return this._nome
    }

    protected var salt: ByteArray = ByteArray(0)

    fun getSaltBase64(): String {
        return Base64.getEncoder().encodeToString(this.salt)
    }

    protected var iv: ByteArray = ByteArray(0)

    fun getIVBase64(): String {
        return Base64.getEncoder().encodeToString(this.iv)
    }

    var connection: Connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")

    constructor(senha: String, nome: String, masterSenha: String) : this() {
        this.salt = generateValidSalt()
        this.iv = generateValidIV()
        this.encryptedSenha = encrypt(senha, masterSenha, salt, iv)
        this._nome = nome
    }

    /**
     * Retorna a senha descriptografada usando a senha mestre fornecida.
     */
    fun getSenhaDecriptografada(masterSenha: String): String {
        return decrypt(this.encryptedSenha, masterSenha, this.salt, this.iv)
    }

    companion object {

        private const val ITERATION_COUNT = 65536
        private const val KEY_LENGTH = 256

        fun encrypt(text: String, password: String, salt: ByteArray, iv: ByteArray): String {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val secretKey = factory.generateSecret(spec)
            val secret = SecretKeySpec(secretKey.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(iv))
            val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
            return Base64.getEncoder().encodeToString(encrypted)
        }

        fun decrypt(encryptedText: String, password: String, salt: ByteArray, iv: ByteArray): String {
            if (encryptedText.isBlank()) {
                throw IllegalArgumentException("Encrypted text is empty or blank")
            }
            val decodedBytes = Base64.getDecoder().decode(encryptedText)
            if (decodedBytes.size == 0 || decodedBytes.size % 16 != 0) {
                throw IllegalBlockSizeException("Invalid encrypted data length: ${decodedBytes.size}")
            }

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val secretKey = factory.generateSecret(spec)
            val secret = SecretKeySpec(secretKey.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(iv))

            val decrypted = cipher.doFinal(decodedBytes)
            return String(decrypted, Charsets.UTF_8)
        }

        fun generateValidSalt(): ByteArray {
            val salt = ByteArray(16)
            SecureRandom().nextBytes(salt)
            return salt
        }

        fun generateValidIV(): ByteArray {
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            return iv
        }
    }

    // Métodos DAO abstratos para ser implementados nas subclasses
}
