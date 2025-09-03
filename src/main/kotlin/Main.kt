import java.sql.DriverManager
import java.sql.SQLException
fun main(){
    Class.forName("org.sqlite.JDBC")
    val connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
    val statement= connection.createStatement()
    statement.execute("""
        CREATE TABLE IF NOT EXISTS aplicativos(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            senha TEXT UNIQUE NOT NULL
        )
    """.trimIndent())

    val nome=readln()
    val senha=readln()
    val insertUsuarios=connection.prepareStatement(
            "INSERT INTO aplicativos (nome,senha) VALUES (?, ?)")
    insertUsuarios.setString(1,nome)
    insertUsuarios.setString(2,senha)
    insertUsuarios.executeUpdate()

    val resultSet = statement.executeQuery("SELECT * FROM aplicativos")
    while (resultSet.next()) {
        println("ID: ${resultSet.getInt("id")}, Nome: ${resultSet.getString("nome")}, Senha: ${resultSet.getString("senha")}")
    }

}
