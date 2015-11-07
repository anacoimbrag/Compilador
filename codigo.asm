sseg SEGMENT STACK ;início seg. pilha
byte 4000h DUP(?) ;dimensiona pilha
sseg ENDS ;fim seg. pilha

dseg SEGMENT PUBLIC ;início seg. dados
byte 4000h DUP(?) ;temporários
sword ? ;inteiro n
byte 100h DUP(?) ;string nome
byte ? ;logico naoterminou
byte 10; valor positivo maxiter
dseg ENDS ;fim seg. dados

cseg SEGMENT PUBLIC ;início seg. código
ASSUME CS:cseg, DS:dseg
strt:
mov ax, dseg
mov ds, ax
mov di, 0 ;end. string temp.
mov cx, 0 ;contador
cmp ax,0 ;verifica sinal
jge R0 ;salta se número positivo
mov bl, 2Dh ;senão, escreve sinal –
mov ds:[di], bl
add di, 1 ;incrementa índice
neg ax ;toma módulo do número
R0:
mov bx, 10 ;divisor
R1:
add cx, 1 ;incrementa contador
mov dx, 0 ;estende 32bits p/ div.
idiv bx ;divide DXAX por BX
push dx ;empilha valor do resto
cmp ax, 0 ;verifica se quoc. é 0
jne R1 ;se não é 0, continua
;agora, desemp. os valores e escreve o string
R2:
pop dx ;desempilha valor
add dx, 30h ;transforma em caractere
mov ds:[di],dl ;escreve caractere
add di, 1 ;incrementa base
add cx, -1 ;decrementa contador
cmp cx, 0 ;verifica pilha vazia
jne R2 ;se não pilha vazia, loop
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

mov di, 02;posição do string
mov si, 16386mov ax, 0 ;acumulador
mov cx, 10 ;base decimal
mov dx, 1 ;valor sinal +
mov bh, 0
mov bl, ds:[di] ;caractere
cmp bx, 2Dh ;verifica sinal
jne R0 ;se não negativo
mov dx, -1 ;valor sinal -
add di, 1 ;incrementa base
mov bl, ds:[di] ;próximo caractere
R0:
push dx ;empilha sinal
mov dx, 0 ;reg. multiplicação
R1:
cmp bx, 0dh ;verifica fim string
je R2 ;salta se fim string
imul cx ;mult. 10
add bx, -48 ;converte caractere
add ax, bx ;soma valor caractere
add di, 1 ;incrementa base
mov bh, 0
mov bl, ds:[di] ;próximo caractere
jmp R1 ;loop
R2:
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
R0:
mov ax, DS:[16642]
cmp ax, 0
je R1
mov di, 0 ;end. string temp.
mov cx, 0 ;contador
cmp ax,0 ;verifica sinal
jge R2 ;salta se número positivo
mov bl, 2Dh ;senão, escreve sinal –
mov ds:[di], bl
add di, 1 ;incrementa índice
neg ax ;toma módulo do número
R2:
mov bx, 10 ;divisor
R3:
add cx, 1 ;incrementa contador
mov dx, 0 ;estende 32bits p/ div.
idiv bx ;divide DXAX por BX
push dx ;empilha valor do resto
cmp ax, 0 ;verifica se quoc. é 0
jne R3 ;se não é 0, continua
;agora, desemp. os valores e escreve o string
R4:
pop dx ;desempilha valor
add dx, 30h ;transforma em caractere
mov ds:[di],dl ;escreve caractere
add di, 1 ;incrementa base
add cx, -1 ;decrementa contador
cmp cx, 0 ;verifica pilha vazia
jne R4 ;se não pilha vazia, loop
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
R0:
mov AL, 0FFh
R1:
mov DS:[0], AL
mov al, DS:[0]
mov DS:[16642], ax
jmp R0
R1:
mov ah,4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa