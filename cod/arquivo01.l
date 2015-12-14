int n;
string nome;
boolean naoTerminou;
final MAXITER=10;
/*identificador nao declarado*/
begin
  write,"Digite seu nome ";
  readln,nome;
  nao=TRUE;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end