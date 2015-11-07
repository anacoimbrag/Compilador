/* Exemplo de Programa na linguagem L */

int n;
string nome, nome1 = "Eduardo", nome2 = "Renato", nome3;
string nome4 nome5, nome6 = "Paulo";
boolean naoTerminou;
final MAXITER=10;

begin
  write,"Digite seu nome: ";
  readln,nome;
  naoTerminou=TRUE;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end