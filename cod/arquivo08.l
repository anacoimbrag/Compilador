int n;
string nome;
boolean naoTerminou;
final MAXITER=10;
byte aux = 5;
/*int = byte * boolean*/
begin
  write,"Digite seu nome ";
  readln,nome;
  naoTerminou=TRUE;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1;
	n = aux * naoTerminou;
    naoTerminou=n<MAXITER;
  end
end