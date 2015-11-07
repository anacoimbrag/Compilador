/* Exemplo de Programa na linguagem L */

int n;
string nome;
boolean naoTerminou, terminou = TRUE, terminou2 = FALSE, terminou3;
boolean terminou4 terminou5, terminou6 = TRUE
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