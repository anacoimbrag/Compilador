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
byte "digite seu nome$"
dseg ENDS

mov al, 0FFh ; const true
mov DS:[0], al
mov al, DS:[0]
mov DS:[16642], ax
mov al, 0 ; const 0
mov DS:[1], al
mov al, DS:[1]
mov ah, 0
mov DS:[16384], ax
R0:
mov ax, DS:[16642]
cmp ax, 0
je R1

dseg SEGMENT PUBLIC
byte "ola'$"
dseg ENDS

mov al, 1 ; const 1
mov DS:[2], al
mov ax, DS:[16384]
mov bx, DS:[2]
add ax, bx ; plus
cwd ; converter pra inteiro
mov DS:[2], ax
mov al, DS:[3]
mov DS:[16384], ax
mov ax, DS:[16384]
cwd
mov cx, ax
mov ax, DS:[16642]
cwd
mov bx, ax
mov ax, cx

cmp ax, bx
jl R2
mov AL, 0
jmp R3
R2:
mov AL, 0FFh
R3:
mov DS:[3], AL
mov al, DS:[3]
mov DS:[16642], ax
jmp R0R1:mov ah,4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa