@ARG
A=M
A=A+1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@THIS
A=A+1
M=D
@SP
A=M
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@THAT
A=M
M=D
@SP
A=M
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@THAT
A=M
A=A+1
M=D
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M
@2
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M-1
D=M
@SP
A=M-1
A=A-1
D=M-D
@SP
M=M-1
A=M-1
M=D
@SP
M=M-1
A=M
D=M
@ARG
A=M
M=D
(MAIN_LOOP_START)
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@COMPUTE_ELEMENT
D;JNE
@END_PROGRAM
0;JMP
(COMPUTE_ELEMENT)
@THAT
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
A=M
A=A+1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M-1
D=M
@SP
A=M-1
A=A-1
D=M+D
@SP
M=M-1
A=M-1
M=D
@SP
M=M-1
A=M
D=M
@THAT
A=M
A=A+1
A=A+1
M=D
@THIS
A=A+1
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M-1
D=M
@SP
A=M-1
A=A-1
D=M+D
@SP
M=M-1
A=M-1
M=D
@SP
M=M-1
A=M
D=M
@THIS
A=A+1
M=D
@ARG
A=M
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
A=M-1
D=M
@SP
A=M-1
A=A-1
D=M-D
@SP
M=M-1
A=M-1
M=D
@SP
M=M-1
A=M
D=M
@ARG
A=M
M=D
@MAIN_LOOP_START
0;JMP
(END_PROGRAM)