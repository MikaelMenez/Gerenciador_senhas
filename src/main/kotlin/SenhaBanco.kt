import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Base64

class SenhaBanco(senha: String, override var _nome: String,master_senha: String) : Senhas(senha, _nome,master_senha) {

    // Construtor secundário
    constructor(senha: String, _nome: String, _cvv: Int, _validade: Validade,master_senha: String) : this(senha, _nome,master_senha) {
        this._cvv = encrypt(_cvv.toString(), master_senha ,salt, iv)
        this._validade = encrypt(_validade.toString(), master_senha,salt, iv)
    }

    private var _cvv = ""
    fun getCVV(): String{
        return this._cvv
    }


    private var _validade = ""
    fun getValidade(): String {
        return this._validade
    }
    override fun createTable(connection: Connection): Exception? {
        try {
            val statement = connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS aplicativos_banco(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    senha TEXT NOT NULL,
                    cvv TEXT NOT NULL,
                    validade TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    iv TEXT NOT NULL
                )
                """.trimIndent()
            )
            statement.execute()
            return null
        } catch (e: Exception) {
            return e
        }
    }

    override fun getByNome(senhaObj: Senhas,master_senha: String): Senhas? {
        try {
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos_banco WHERE _nome='${senhaObj.getNome()}'")

            if (resultSet.next()) {
                // Recupera salt e iv do banco
                val dbSalt = Base64.getDecoder().decode(resultSet.getString("salt"))
                val dbIv = Base64.getDecoder().decode(resultSet.getString("iv"))

                return SenhaBanco(
                    decrypt(resultSet.getString("senha"), master_senha,dbSalt, dbIv),
                    resultSet.getString("_nome"),
                    decrypt(resultSet.getString("cvv"), master_senha,dbSalt, dbIv).toInt(),
                    Validade.toValidade(decrypt(resultSet.getString("validade"), master_senha,dbSalt, dbIv))
                    ,master_senha
                )
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun modify(id: Int, senhas: Senhas): Exception? {
        if (senhas is SenhaBanco) {
        try {


            val insertStatement = connection.prepareStatement(
                "INSERT INTO aplicativos_banco (_nome, senha, cvv, validade, salt, iv) VALUES (?, ?, ?, ?, ?, ?)"
            )
            insertStatement.setString(1, senhas.getSenhaCriptografada())
            insertStatement.setString(2, senhas.getNome())
            insertStatement.setString(3, senhas.getCVV())
            insertStatement.setString(4, senhas.getValidade())
            insertStatement.setString(5, senhas.getsaltAsString())
            insertStatement.setString(6, senhas.getIVAsString())
            insertStatement.executeUpdate()
            return null
        }catch (e: Exception){
            return e
        }
        }
        else{
            return Exception()
        }
    }

    override fun deleteByNome(nome: String): Exception? {
        try {


            val insertStatement = connection.prepareStatement(
                "DELETE FROM aplicativos_banco WHERE nome=?"
            )
            insertStatement.setString(1,nome)
            insertStatement.execute()

            return null
        }catch (e: Exception){
            return e
        }
    }

    override fun insertData(senhas: Senhas) : Exception?{
        if (senhas is SenhaBanco) {
        try {


        val insertStatement = connection.prepareStatement(
            "'UPDATE aplicativos_banco SET senha=?, nome=?, cvv=?, validade=?, salt=?, iv=? WHERE id=?'"
        )
        insertStatement.setString(1, senhas.getSenhaCriptografada())
        insertStatement.setString(2, senhas.getNome())
        insertStatement.setString(3, senhas.getCVV())
        insertStatement.setString(4, senhas.getValidade())
        insertStatement.setString(5, senhas.getsaltAsString())
        insertStatement.setString(6, senhas.getIVAsString())
        insertStatement.executeUpdate()
            return null
    }catch (e: Exception){
        return e
    }

        }
        else{
            return Exception()
        }
}
    companion object{
      fun getAll(master_senha: String): HashMap<String, SenhaBanco>?{
        var senhas= HashMap<String,SenhaBanco>()
          var connection: Connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
        try {


            val insertStatement = connection.createStatement()
            val resultSet: ResultSet =insertStatement.executeQuery(
                "SELECT * FROM aplicativos_banco"
            )

            while (resultSet.next()){
                "_nome, senha, cvv, validade, salt, iv"
                var iv=resultSet.getString("iv")
                var salt=resultSet.getString("salt")
               var senhatemp=SenhaBanco(decrypt(resultSet.getString("senha"),master_senha,salt.toByteArray(),iv.toByteArray()),resultSet.getString("_nome"),decrypt(resultSet.getString("cvv"),master_senha,salt.toByteArray(),iv.toByteArray()).toInt(),Validade.toValidade(decrypt(resultSet.getString("cvv"),master_senha,salt.toByteArray(),iv.toByteArray())),master_senha)
                senhas.put(resultSet.getString("_nome"),senhatemp)
            }


        }catch (e: Exception){
            return null
        }
        return senhas
    }
    }



}