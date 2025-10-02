import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

abstract class Senhas() : DAO {
    protected var encryptedSenha: String = ""
    private var _nome: String = ""
    protected var salt: ByteArray = ByteArray(0)
    protected var iv: ByteArray = ByteArray(0)
    var connection: Connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")

    constructor(senha: String, nome: String, masterSenha: String) : this() {
        this.salt = generateByteArray()
        this.iv = generateByteArray()
        this.encryptedSenha = encrypt(senha, masterSenha, salt, iv)
        this._nome = nome
    }

    fun getSenhaCriptografada(): String {
        return this.encryptedSenha
    }

    fun getNome(): String {
        return this._nome
    }

    fun getSaltBase64(): String {
        return Base64.getEncoder().encodeToString(this.salt)
    }

    fun getIVBase64(): String {
        return Base64.getEncoder().encodeToString(this.iv)
    }

    /** Retorna a senha descriptografada usando a senha mestre fornecida. */
    fun getSenhaDecriptografada(masterSenha: String): String {
        return decrypt(this.encryptedSenha, masterSenha, this.salt, this.iv)
    }

    companion object {

        private const val ITERATION_COUNT = 65536
        private const val KEY_LENGTH = 256
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"

        fun encrypt(text: String, password: String, salt: ByteArray, iv: ByteArray): String {
            val cipher = generateCipher(password, salt, iv)
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
            val cipher = generateCipher(password, salt, iv)
            val decrypted = cipher.doFinal(decodedBytes)
            return String(decrypted, Charsets.UTF_8)
        }

        fun generateCipher(password: String, salt: ByteArray, iv: ByteArray): Cipher {
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val secretKey = factory.generateSecret(spec)
            val secret = SecretKeySpec(secretKey.encoded, "AES")

            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(iv))

            return cipher
        }

        fun generateByteArray(): ByteArray {
            val array = ByteArray(16)
            SecureRandom().nextBytes(array)
            return array
        }
    }

    // Métodos DAO abstratos para ser implementados nas subclasses
}
