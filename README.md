1. Introdução
O projeto consiste em um sistema de gerenciamento de senhas desenvolvido em Kotlin, com banco de dados local via SQLite e interface de usuário baseada em javax.swing.JOptionPane. O software foi desenvolvido com base nos conceitos fundamentais de Programação Orientada a Objetos, como Herança, Polimorfismo, Encapsulamento e Abstração. A arquitetura é baseada em uma classe abstrata Senhas e suas subclasses (SenhaGeral, SenhaBanco), seguindo o padrão de projeto DAO (Data Access Object) para separação de responsabilidades.

2. Análise da Arquitetura e Aplicação de Conceitos de POO
O projeto está estruturado com foco em seguir os conceitos da orientação a objetos.
2.1. Abstração e Herança
O principal ponto de destaque é a hierarquia de classes bem definida:
Senhas.kt (Classe Abstrata): Esta classe é o coração do projeto. Ela abstrai o conceito de uma "Senha", contendo os atributos e comportamentos comuns a todos os tipos de senha no sistema:
Atributos Protegidos: encryptedSenha, _nome, salt, iv. Esses atributos são a essência de qualquer senha no sistema. O uso do modificador protected (implícito para _nome) permite que apenas as classes filhas tenham acesso direto, respeitando o encapsulamento.
Comportamentos Comuns: A lógica de criptografia (encrypt), descriptografia (decrypt), e a geração de salt e iv estão definidas aqui. Isso evita a repetição de código e garante que a segurança seja aplicada de forma certa.
Contrutor: O construtor da classe Senhas realiza a criptografia da senha no momento da criação do objeto, garantindo que uma senha nunca exista em memória descriptografada por mais tempo que o necessário.
SenhaGeral.kt e SenhaBanco.kt (Sub Classes): 
Nós criamos estas classes para que herdassem de Senhas, especializando o conceito abstrato que havíamos definido:
SenhaGeral é uma implementação direta da classe Senhas.
SenhaBanco herda da classe abstrata e adiciona novos atributos (_cvv, _validade) e metodos para manipulá-los (getCVV, getValidade). Um uso eficaz da herança e encapsulamento para reutilização e extensão.
2.2. Polimorfismo
O polimorfismo é aplicado através da sobrescrita de métodos definidos na interface DAO.
A interface DAO.kt define um contrato com métodos como insertData, getByNome, modify, e deleteByNome.
Tanto SenhaGeral quanto SenhaBanco implementam de forma concreta e específica esses métodos. Por exemplo, o insertData em SenhaBanco lida com os campos cvv e validade, enquanto a mesma operação em SenhaGeral não faz isso.
Isso significa que seria possível ter uma coleção de objetos do tipo Senhas e chamar o método insertData em cada um, e o sistema executaria a versão correta do método de forma dinâmica, dependendo se o objeto é uma SenhaGeral ou uma SenhaBanco.
2.3. Encapsulamento
Tivemos um cuidado especial com o encapsulamento para proteger o estado interno dos nossos objetos. Por exemplo, os dados sensíveis como a senha criptografada, o salt e o IV foram mantidos como atributos da classe Senhas e o acesso a eles é feito somente através de métodos públicos:
Os dados sensíveis como a senha criptografada, o salt e o IV são mantidos como atributos da classe Senhas. O acesso a eles é feito através de métodos públicos (getters), como getSenhaCriptografada() e getSaltBase64().
A senha em texto plano nunca é armazenada no objeto. Ela é recebida no construtor, imediatamente criptografada e descartada. A única forma de recuperá-la é através do método getSenhaDecriptografada(masterSenha), que requer a senha mestre, mantendo a seguraça sobre o acesso.
Em SenhaBanco, os campos _cvv e _validade são privados, com acesso somente através dos métodos getCVV() e getValidade(), que retornam os valores já criptografados.
2.4. Padrão de Projeto: DAO (Data Access Object)
Ao planejar a arquitetura, decidimos usar a interface DAO.kt por dois motivos principais. Primeiro, para aplicar na prática o conceito de abstração e polimorfismo, que era um dos objetivos da disciplina. Segundo, e mais importante, percebemos que separar a lógica de acesso a dados (INSERT, SELECT, etc.) das nossas regras de negócio tornaria o projeto muito mais organizado e fácil de manter.
Separação de Responsabilidades: Ela retira as regras de negócio (as regras dentro das classes de senha) da lógica de persistência (como os dados são escritos e lidos do banco de dados SQL).
Flexibilidade: Se no futuro o sistema precisasse mudar de SQLite para outro banco de dados (como PostgreSQL ou um arquivo JSON), bastaria criar novas implementações da interface DAO sem precisar alterar as classes Front ou Main.
Modelagem do problema:
    <object type="image/svg+xml" data="./uml.svg">
      O seu SVG não foi carregado.
    </object>

4. Análise dos Componentes
Main.kt: Cumpre seu papel iniciando a aplicação de forma limpa. Ele inicializa o banco de dados e entrega o controle para a camada de apresentação, o que é uma boa prática.
Front.kt: É responsável por apresentar a interface gráfica para o usuário.


5. Conclusão
Ao desenvolver este projeto, nosso principal objetivo foi aplicar na prática os conceitos fundamentais da Programação Orientada a Objetos que aprendemos na disciplina. Buscamos estruturar o código de uma forma limpa e organizada, utilizando uma hierarquia de herança com a classe abstrata Senhas e suas subclasses, SenhaGeral e SenhaBanco. A implementação da interface DAO, foi uma decisão de design que consideramos muito acertada, pois nos permitiu separar de forma clara as regras de negócio do acesso ao banco de dados, além de permitir que outros conceitos de poo fossem aplicados na prática.


