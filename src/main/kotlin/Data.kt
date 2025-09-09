

class Data {
    var dia=0
    var ano=0
    var mes=0
    constructor(dia: Int,mes: Int,ano: Int){
        if (!(dia<=31&& dia>=1)){
            throw IllegalArgumentException("O dia deve estar entre 1 e 31")

        }
        else if (!(mes<=12&& mes>=1)){
            throw IllegalArgumentException("O mês deve estar entre 1 e 12")
        }
        else if (ano<0 ){
            throw IllegalArgumentException("não existe ano negativo")
        }
        else{
            this.mes=mes
            this.ano=ano
            this.dia=dia
        }

    }
    companion object {
        fun toData(data: String):Data{
        data.replace("/","")
        var date=Data(data.substring(0,1).toInt(),data.substring(2,3).toInt(),data.removeRange(0,3).toInt())
        return date
    }
    }
    override fun toString(): String{
        return "$dia/$mes/$ano"
    }
}