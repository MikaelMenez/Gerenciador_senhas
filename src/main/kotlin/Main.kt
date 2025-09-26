import java.sql.Connection
import java.sql.DriverManager
import javax.swing.JOptionPane

fun main(){
    try {


        var connection: Connection= DriverManager.getConnection("jdbc:sqlite:gerenciador.db")
    SenhaGeral.createTable(connection)
    SenhaBanco.createTable(connection)
    val senha= JOptionPane.showInputDialog(null,"digite sua senha mestre:")
        val front=Front(senha)
    front.open()
    }
    catch (e: Exception){
        JOptionPane.showMessageDialog(null,e.toString())
    }
}
