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

dseg SEGMENT PUBLIC
byte "digite seu nome: $"
dseg ENDS

mov dx, 16644
mov ah, 09h
int 21h

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
mov si, 16386

R0:
mov al, ds:[di]
cmp al, 0dh ;verifica fim string
je R1 ;salta se fim string
mov ds:[si], al ;próximo caractere
add di, 1 ;incrementa base
add si, 1
jmp R0 ;loop
R1:
mov al, 024h ;fim de string
mov ds:[si], al ;grava '$'

mov al, 0FFh ; const true
mov DS:[0], al
mov al, DS:[0]
mov DS:[16642], ax
mov al, 0 ; const 0
mov DS:[0], al
mov al, DS:[0]
mov ah, 0
mov DS:[16384], ax
R2:
mov ax, DS:[16642]
cmp ax, 0
je R3

dseg SEGMENT PUBLIC
byte "ola' $"
dseg ENDS

mov dx, 16662
mov ah, 09h
int 21h

mov dx, 16386
mov ah, 09h
int 21h

mov ah, 02h
mov dl, 0Dh
int 21h
mov DL, 0Ah
int 21h

mov al, 1 ; const 1
mov DS:[0], al
mov ax, DS:[16384]
mov bx, DS:[0]
add ax, bx ; plus
cwd ; converter pra inteiro
mov DS:[1], ax
mov al, DS:[1]
mov DS:[16384], ax
mov ax, DS:[16384]
mov cx, ax
mov bl, DS:[16642]
mov al, bl
mov ah, 0
mov bx, ax
mov ax, cx

cmp ax, bx
jl R4
mov AL, 0
jmp R5
R4:
mov AL, 0FFh
R5:
mov DS:[0], AL
mov al, DS:[0]
mov DS:[16642], ax
jmp R2
R3:
mov ah,4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa