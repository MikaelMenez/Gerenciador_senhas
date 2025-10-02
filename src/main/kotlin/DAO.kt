import Senhas.Companion.decrypt
import java.sql.ResultSet
import java.util.*

interface DAO {
    fun insertData(senhas: Senhas): Exception?
    fun getByNome(senha: Senhas, masterSenha: String): Senhas?
    fun modify(id: Int, senhas: Senhas): Exception?
    fun deleteByNome(nome: String): Exception?
}

fun ResultSet.getDecryptedField(
    fieldName: String,
    masterSenha: String,
    saltField: String = "salt",
    ivField: String = "iv"
): String {
    val salt = Base64.getDecoder().decode(getString(saltField))
    val iv = Base64.getDecoder().decode(getString(ivField))
    return decrypt(getString(fieldName), masterSenha, salt, iv)
}
