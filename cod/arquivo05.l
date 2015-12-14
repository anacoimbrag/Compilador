int n;
string nome;
boolean naoTerminou;
final MAXITER=10;
byte aux = 9;
/*teste int = int + byte*/
begin
  write,"Digite seu nome ";
  readln,nome;
  naoTerminou=TRUE;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1+aux;
    naoTerminou=n<MAXITER;
  end
end