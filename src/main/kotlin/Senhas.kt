import java.sql.DriverManager

 open class Senhas(var senha: String, var nome: String):DAO,Criptografy{
  val key="1234567890123456"
  val iv="abcdefghijklmnop"
  val connection = DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
  val statement = connection.createStatement()
 }