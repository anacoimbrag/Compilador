int n;
string nome;
boolean naoTerminou;
final MAXITER=10;
byte aux = 4;
byte aux1 = 8;
/*inteiro = byte/byte*/
begin
  write,"Digite seu nome ";
  readln,nome;
  naoTerminou=TRUE;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1;
	n = aux1/aux;
    naoTerminou=n<MAXITER;
  end
end