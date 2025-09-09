import java.util.Date

class SenhaBanco(senha: String, nome: String) : Senhas(senha, nome) {
    val cvv=0;
    val validade= Date();
    override fun encrypt(plainText: String, key: String, iv: String): String {
        TODO("Not yet implemented")
    }

    override fun decrypt(encryptedText: String, key: String, iv: String): String {
        TODO("Not yet implemented")
    }
}