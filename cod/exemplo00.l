/* Exemplo de Programa na linguagem L */

int n = 10, c = 20, d, e = -10, f;
int m a = 10;
string nome;
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