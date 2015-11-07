/* Exemplo de Programa na linguagem L */

int n;
string nome;
boolean naoTerminou;
byte a = 1, b = 2, c, d = 0xAA;
byte e f, g = 0x11;
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