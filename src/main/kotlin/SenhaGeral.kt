import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Base64

class SenhaGeral(senha: String, override var _nome: String,master_senha: String) : Senhas(senha, _nome,master_senha) {


    override fun createTable(connection: Connection): Exception? {
        try {
            val statement = connection.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS aplicativos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    senha TEXT NOT NULL,
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
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE _nome='${senhaObj.getNome()}'")

            if (resultSet.next()) {
                // Recupera salt e iv do banco
                val dbSalt = Base64.getDecoder().decode(resultSet.getString("salt"))
                val dbIv = Base64.getDecoder().decode(resultSet.getString("iv"))

                return SenhaGeral(
                    decrypt(resultSet.getString("senha"), master_senha,dbSalt, dbIv),
                    resultSet.getString("_nome"),master_senha
                )
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun modify(id: Int, senhas: Senhas): Exception? {
        if (senhas is SenhaGeral) {
            try {


                val insertStatement = connection.prepareStatement(
                    "INSERT INTO aplicativos (_nome, senha, salt, iv) VALUES (?, ?, ?, ?)"
                )
                insertStatement.setString(1, senhas.getSenhaCriptografada())
                insertStatement.setString(2, senhas.getNome())
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
                "DELETE FROM aplicativos WHERE nome=?"
            )
            insertStatement.setString(1,nome)
            insertStatement.execute()

            return null
        }catch (e: Exception){
            return e
        }
    }

    override fun insertData(senhas: Senhas) : Exception?{
        if (senhas is SenhaGeral) {
            try {


                val insertStatement = connection.prepareStatement(
                    "'UPDATE aplicativos SET senha=?, nome=?, salt=?, iv=? WHERE id=?'"
                )
                insertStatement.setString(1, senhas.getSenhaCriptografada())
                insertStatement.setString(2, senhas.getNome())
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
      fun getAll(master_senha: String): HashMap<String, SenhaGeral>?{
        var senhas= HashMap<String,SenhaGeral>()
          var connection: Connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")

          try {


            val insertStatement = connection.createStatement()
            val resultSet: ResultSet =insertStatement.executeQuery(
                "SELECT * FROM aplicativos_banco"
            )

            while (resultSet.next()){
                "_nome, senha, salt, iv"
                var iv=resultSet.getString("iv")
                var salt=resultSet.getString("salt")
                var senhatemp=SenhaGeral(decrypt(resultSet.getString("senha"),master_senha,salt.toByteArray(),iv.toByteArray()),resultSet.getString("_nome"),master_senha)
                senhas.put(resultSet.getString("_nome"),senhatemp)
            }


        }catch (e: Exception){
            return null
        }
        return senhas
    }}


}