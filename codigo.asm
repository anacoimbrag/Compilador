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

mov al, ffh ; const true
mov DS:[0], al
mov al, 0 ; const 0
mov DS:[1], al

dseg SEGMENT PUBLIC
byte "ola'$"
dseg ENDS

mov al, 1 ; const 1
mov DS:[2], al
cseg ENDS ;fim seg. código
END strt ;fim programa