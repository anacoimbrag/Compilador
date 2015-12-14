# Compiladores
Compilador implementado em Java para disciplina de Compiladores do curso de Ciência da Computação para a linguagem fictícia L.

##Ambiente de Desenvolvimento
Após clonar o projeto, exporte para o Workspace pré-definido do **Eclipse**.

*Simbolo.java* define a entidade símbolo que utilizamos a todo momento durante o processo de compilação.
Seus atributos são: lexema, token, tipo, endereco e classe.

* Lexema: valor escrito no programa que equile ao símbolo.
* Token: o que representa o lexema encontrado na gramática.
* Tipo: apenas para constantes e variáveis, indica se é do tipo inteiro, string, byte, logico, etc.
* Endereco: apenas para constantes e variáveis, armazena o endereço de memória que foi guardado para o símbolo.
* Classe: indica se um símbolo cujo token seja identificador, se este é da classe variavel ou constante.

*AnalisadorLexico.java* faz a implementação do autômato da linguagem L. Realizando a transição dos estados, conforme o caractere lido e armazenado no buffer.

*Parse.java* realiza toda a análise sintática e semântica da linguagem, além de fazer a parte de geração de código. Ao finalizar leitura e tradução de todo o código, a classe *Buffer* é chamada para realizar a otimização do código assembly e gravá-lo no arquivo codigo.asm.

Os arquivos *Rotulo.java* e *Memoria.java* fazem o gerenciamento de rotulos e memória respectivamente. Incrementando os respectivos contadores para que a próxima utilização ocorra sem conflitos.
