
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import javax.crypto.spec.IvParameterSpec

interface Criptografy {
    companion object {
        fun encrypt(plainText: String, key: String, iv: String): String {
            val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)

            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            return Base64.getEncoder().encodeToString(encryptedBytes)
        }
        fun decrypt(encryptedText: String, key: String, iv: String): String {
            val keySpec = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)

            // Decodifique de Base64 antes de descriptografar
            val encryptedBytes = Base64.getDecoder().decode(encryptedText)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        }

    }
}