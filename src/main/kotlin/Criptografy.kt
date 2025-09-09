
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.crypto.spec.IvParameterSpec

interface Criptografy {
        fun encrypt(plainText: String, key: ByteArray, iv: ByteArray): String
        fun decrypt(encryptedText: String, key: ByteArray, iv: ByteArray): String
        fun generateValidKey(): ByteArray
        fun generateValidIV(): ByteArray

    }
