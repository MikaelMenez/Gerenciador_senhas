import javax.swing.JOptionPane

fun main(){
   JOptionPane.showMessageDialog(null, "Bem Vindo ao Gerensenhador")
   var senhaMestra: String

       senhaMestra= JOptionPane.showInputDialog("Digite a senha mestre")


   val front=Front(senhaMestra)
   front.start()
}
