

class Validade {

    var ano=0
    var mes=0
    constructor(mes: Int,ano: Int){

         if (!(mes<=12&& mes>=1)){
            throw IllegalArgumentException("O mês deve estar entre 1 e 12")
        }
        else if (ano<0 ){
            throw IllegalArgumentException("não existe ano negativo")
        }
        else{
            this.mes=mes
            this.ano=ano

        }

    }
    companion object {
        fun toValidade(data: String):Validade{
        data.replace("/","")
        var date=Validade(data.substring(0,1).toInt(),data.removeRange(0,1).toInt())
        return date
    }
    }
    override fun toString(): String{
        return "$mes/$ano"
    }
}