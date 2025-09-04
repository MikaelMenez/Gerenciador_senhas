import java.sql.DriverManager
class DbController {
    companion object {

        val connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
        val statement= connection.createStatement()

        fun createDB(): java.lang.Exception? {
            try {
                Class.forName("org.sqlite.JDBC")
                this.statement.execute("""
                    CREATE TABLE IF NOT EXISTS aplicativos(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    senha TEXT UNIQUE NOT NULL
                    )
                   """.trimIndent())
                return null
            }
            catch (e: Exception){
                return e
            }
        }

        fun insertData(nome: String,senha: String){
            Class.forName("org.sqlite.JDBC")
            val insertUsuarios=connection.prepareStatement(
                "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)")
            insertUsuarios.setString(1,nome)
            insertUsuarios.setString(2,senha)
            insertUsuarios.executeUpdate()
        }
        fun showAll(){
            Class.forName("org.sqlite.JDBC")
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos")
            while (resultSet.next()) {
                println("ID: ${resultSet.getInt("id")}, Nome: ${resultSet.getString("nome")}, Senha: ${resultSet.getString("senha")}")
            }
        }
        fun getOne(id: Int):Senhas{
            Class.forName("org.sqlite.JDBC")
            val resultSet = statement.executeQuery("SELECT * FROM aplicativos WHERE id="+id)
            val senha=Senhas(resultSet.getString("senha"),resultSet.getString("nome"))
            return senha
        }

    }
}