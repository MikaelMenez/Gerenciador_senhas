import java.sql.Connection
import kotlin.use

interface DAO {
    fun createTable(connection: Connection): java.lang.Exception?

    fun insertData(senhas: Senhas): Exception?

    fun getByNome(senha:Senhas,master_senha: String): Senhas?

    fun modify( id: Int,senhas: Senhas): Exception?


    fun deleteByNome(nome: String): Exception?
}