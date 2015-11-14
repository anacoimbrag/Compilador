sseg SEGMENT STACK ;in�cio seg. pilha
byte 4000h DUP(?) ;dimensiona pilha
sseg ENDS ;fim seg. pilha

dseg SEGMENT PUBLIC ;in�cio seg. dados
byte 4000h DUP(?) ;tempor�rios
sword ? ;inteiro n
byte 100h DUP(?) ;string nome
byte ? ;logico naoterminou
byte 10; valor positivo maxiter
dseg ENDS ;fim seg. dados

cseg SEGMENT PUBLIC ;in�cio seg. c�digo
ASSUME CS:cseg, DS:dseg
strt:
mov ax, dseg
mov ds, ax
mov di, 0 ;end. string temp.
mov cx, 0 ;contador
cmp ax,0 ;verifica sinal
jge R0 ;salta se n�mero positivo
mov bl, 2Dh ;sen�o, escreve sinal �
mov ds:[di], bl
add di, 1 ;incrementa �ndice
neg ax ;toma m�dulo do n�mero
R0:
  mov bx, 10 ;divisor
R1:
  add cx, 1 ;incrementa contador
  mov dx, 0 ;estende 32bits p/ div.
  idiv bx ;divide DXAX por BX
  push dx ;empilha valor do resto
  cmp ax, 0 ;verifica se quoc. � 0
  jne R1 ;se n�o � 0, continua
  ;agora, desemp. os valores e escreve o string
R2:
  pop dx ;desempilha valor
  add dx, 30h ;transforma em caractere
  mov ds:[di],dl ;escreve caractere
  add di, 1 ;incrementa base
  add cx, -1 ;decrementa contador
  cmp cx, 0 ;verifica pilha vazia
  jne R2 ;se n�o pilha vazia, loop
  ;grava fim de string
  mov dl, 024h ;fim de string
  mov ds:[di], dl ;grava '$'
  ;exibe string
  mov dx, 0
  mov ah, 09h
  int 21h


  dseg SEGMENT PUBLIC
  byte "digite seu nome$"
  dseg ENDS

  mov dx, 0
  mov al, 0FFh
  mov ds:[0], al
  mov ah, 0Ah
  int 21h

  mov ah, 02h
  mov dl, 0Dh
  int 21h
  mov DL, 0Ah
  int 21h

  mov di, 02
  mov si, 16386
  mov ax, 0
  mov cx, 10
  mov dx, 1 ;valor sinal +
  mov bh, 0
  mov bl, ds:[di] ;caractere
  cmp bx, 2Dh ;verifica sinal
  jne R0 ;se n�o negativo
  mov dx, -1 ;valor sinal -
  add di, 1 ;incrementa base
  mov bl, ds:[di] ;pr�ximo caractere
R3:
  push dx ;empilha sinal
  mov dx, 0 ;reg. multiplica��o
R4:
  cmp bx, 0dh ;verifica fim string
  je R2 ;salta se fim string
  imul cx ;mult. 10
  add bx, -48 ;converte caractere
  add ax, bx ;soma valor caractere
  add di, 1 ;incrementa base
  mov bh, 0
  mov bl, ds:[di] ;pr�ximo caractere
  jmp R1 ;loop
R5:
  pop cx ;desempilha sinal
  imul cx ;mult. sinal

  mov al, 0FFh ; const true
  mov DS:[0], al
  mov al, DS:[0]
  mov DS:[16642], ax
  mov al, 0 ; const 0
  mov DS:[0], al
  mov al, DS:[0]
  mov ah, 0
  mov DS:[16384], ax
R6:
  mov ax, DS:[16642]
  cmp ax, 0
  je R1
  mov di, 0 ;end. string temp.
  mov cx, 0 ;contador
  cmp ax,0 ;verifica sinal
  jge R2 ;salta se n�mero positivo
  mov bl, 2Dh ;sen�o, escreve sinal �
  mov ds:[di], bl
  add di, 1 ;incrementa �ndice
  neg ax ;toma m�dulo do n�mero
R7:
  mov bx, 10 ;divisor
R8:
  add cx, 1 ;incrementa contador
  mov dx, 0 ;estende 32bits p/ div.
  idiv bx ;divide DXAX por BX
  push dx ;empilha valor do resto
  cmp ax, 0 ;verifica se quoc. � 0
  jne R3 ;se n�o � 0, continua
  ;agora, desemp. os valores e escreve o string
R9:
  pop dx ;desempilha valor
  add dx, 30h ;transforma em caractere
  mov ds:[di],dl ;escreve caractere
  add di, 1 ;incrementa base
  add cx, -1 ;decrementa contador
  cmp cx, 0 ;verifica pilha vazia
  jne R4 ;se n�o pilha vazia, loop
  ;grava fim de string
  mov dl, 024h ;fim de string
  mov ds:[di], dl ;grava '$'
  ;exibe string
  mov dx, 0
  mov ah, 09h
  int 21h

  mov ah, 02h
  mov dl, 0Dh
  int 21h
  mov DL, 0Ah
  int 21h


  dseg SEGMENT PUBLIC
  byte "ola'$"
  dseg ENDS

  mov al, 1 ; const 1
  mov DS:[0], al
  mov ax, DS:[16384]
  mov bx, DS:[0]
  add ax, bx ; plus
  cwd ; converter pra inteiro
  mov DS:[0], ax
  mov al, DS:[1]
  mov DS:[16384], ax
  mov ax, DS:[16384]
  cwd
  mov cx, ax
  mov ax, DS:[16642]
  cwd
  mov bx, ax
  mov ax, cx

  cmp ax, bx
  jl R0
  mov AL, 0
  jmp R1
R10:
  mov AL, 0FFh
R11:
  mov DS:[0], AL
  mov al, DS:[0]
  mov DS:[16642], ax
  jmp R0
R12:
  mov ah,4Ch
  int 21h
cseg ENDS ;fim seg. c�digo
END strt ;fim programa
