int n;
string nome;
boolean naoTerminou;
final MAXITER=10;
/*classe de identificador incompatível*/
begin
  write,"Digite seu nome ";
  readln,nome;
  naoTerminou=TRUE;
  MAXITER = 5;
  n=0;
  while naoTerminou begin
    writeln,"Ola' ",nome;
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end