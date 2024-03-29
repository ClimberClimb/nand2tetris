// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.


(LOOP)    
    @24576
    D=M
    @16383
    M=0

    @CLEAR
    D;JEQ
    @8192
    M=A

(SETWORK)
    @16383
    M=M+1
    D=M
    A=A+D
    M=-1    
    @8192
    M=M-1
    D=M
    @SETWORK
    D;JGT

	@LOOP
	0;JMP

(CLEAR)
    @8192
    M=A
(CLEARWORK) 
    @16383
    M=M+1
    D=M
    A=A+D
    M=0	   

    @8192
    M=M-1
    D=M
    @CLEARWORK
    D;JGT

	@LOOP
	0;JMP


