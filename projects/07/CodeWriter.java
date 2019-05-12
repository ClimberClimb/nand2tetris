import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;


public class CodeWriter
{
    private BufferedWriter writer;
    private int compareCount;
    private int callCount;
    private String className;

    CodeWriter (String filename) throws IOException
    {
        File f = new File(filename);
        writer = new BufferedWriter(new FileWriter(f));
        className = null;
        compareCount = 1;
        callCount = 1;
    }

    public CodeWriter (FileWriter fp) throws IOException
    {
        writer = new BufferedWriter(fp);
    }

    void close () throws IOException
    {
        writer.close();
    }

    void setFileName(String filename)
    {
        className = filename;
    }

    private void writeStackAddressToA(int index) throws IOException
    {
        writer.write("@SP\n");
        writer.write("A=M-1\n");
        for (int i = 0; i < index-1;i++)
            writer.write("A=A-1\n");
    }

    private void writeDValueToShrinkStack() throws IOException
    {
        writer.write("@SP\n");
        writer.write("M=M-1\n");
        writer.write("A=M-1\n");
        writer.write("M=D\n");
    }

    void writeDToExpandStack() throws IOException
    {
        writer.write("@SP\n");
        writer.write("A=M\n");
        writer.write("M=D\n");
        writer.write("@SP\n");
        writer.write("M=M+1\n");
    }

    public void writeLabel(String label) throws IOException
    {
        writer.write(String.format("(%s)\n", label));
    }

    public void writeGoto(String label) throws IOException
    {
        writer.write(String.format("@%s\n", label));
        writer.write(String.format("0;JMP\n", label));
    }

    public void writeIf(String label) throws IOException
    {
        writer.write("@SP\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
        writer.write(String.format("@%s\n", label));
        writer.write("D;JNE\n");
    }

    public void writeInit() throws IOException
    {
        writer.write("@256\n");
        writer.write("D=A\n");
        writer.write("@0\n");
        writer.write("M=D\n");

        writer.write("@END\n");
        writer.write("D=A\n");
        writeDToExpandStack();

        writer.write("@LCL\n");
        writer.write("D=0\n");
        writeDToExpandStack();

        writer.write("@ARG\n");
        writer.write("D=0\n");
        writeDToExpandStack();

        writer.write("@THIS\n");
        writer.write("D=0\n");
        writeDToExpandStack();

        writer.write("@THAT\n");
        writer.write("D=0\n");
        writeDToExpandStack();

        writer.write("@SP\n");
        writer.write("D=M\n");

        writer.write("@LCL\n");
        writer.write("M=D\n");

        writer.write(String.format("@%s\n", 5));
        writer.write("D=D-A\n");

        writer.write("@ARG\n");
        writer.write("M=D\n");

        writer.write("@" + "Sys.init" + "\n");
        writer.write("0;JMP\n");
        writer.write(String.format("(call.%s)\n", Integer.toString(callCount)));
    }


    public void writeEnd() throws IOException
    {
        writer.write("(END)\n");
    }


    public void writeFunction(String funcName, int numlocals) throws IOException
    {
        writer.write(String.format("(%s)\n", funcName));
        for (int i = 0; i < numlocals; i++)
            writePushPop(Parser.COMMAND.C_PUSH, "constant", 0);
    }

    public void writeCall(String funcName, int numArgs) throws IOException
    {
        writer.write(String.format("@call.%s //call\n", Integer.toString(callCount)));
        writer.write("D=A\n");
        writeDToExpandStack();

        writer.write("@LCL\n");
        writer.write("D=M\n");
        writeDToExpandStack();

        writer.write("@ARG\n");
        writer.write("D=M\n");
        writeDToExpandStack();

        writer.write("@THIS\n");
        writer.write("D=M\n");
        writeDToExpandStack();

        writer.write("@THAT\n");
        writer.write("D=M\n");
        writeDToExpandStack();

        writer.write("@SP\n");
        writer.write("D=M\n");

        writer.write("@LCL\n");
        writer.write("M=D\n");

        writer.write(String.format("@%s\n", numArgs + 5));
        writer.write("D=D-A\n");

        writer.write("@ARG\n");
        writer.write("M=D\n");

        writer.write("@" + funcName + "\n");
        writer.write("0;JMP\n");
        writer.write(String.format("(call.%s)\n", Integer.toString(callCount)));
        callCount++;

    }

    public void writeReturn() throws IOException
    {
        writer.write("@LCL //return\n");
        writer.write("D=M\n");
        writer.write("@R13\n");
        writer.write("M=D\n");

        // put the ret address in the
        writer.write("@5\n");
        writer.write("A=D-A\n");
        writer.write("D=M\n");
        writer.write("@R14\n");
        writer.write("M=D\n");

        writePushPop(Parser.COMMAND.C_POP, "argument", 0);

        writer.write("@ARG\n");
        writer.write("D=M+1\n");
        writer.write("@SP\n");
        writer.write("M=D\n");


        writer.write("@R13\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
        writer.write("@THAT\n");
        writer.write("M=D\n");

        writer.write("@R13\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
        writer.write("@THIS\n");
        writer.write("M=D\n");

        writer.write("@R13\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
        writer.write("@ARG\n");
        writer.write("M=D\n");

        writer.write("@R13\n");
        writer.write("M=M-1\n");
        writer.write("A=M\n");
        writer.write("D=M\n");
        writer.write("@LCL\n");
        writer.write("M=D\n");

        writer.write("@R14\n");
        writer.write("A=M\n");
        writer.write("0;JMP\n");
    }


    public void writeArithmetic(String command) throws IOException
    {

        if (command.equals("add"))
        {
            writeStackAddressToA(1);
            writer.write("D=M\n");
            writeStackAddressToA(2);
            writer.write("D=M+D\n");
            writeDValueToShrinkStack();
        }
        else if (command.equals("sub"))
        {
            writeStackAddressToA(1);
            writer.write("D=M\n");
            writeStackAddressToA(2);
            writer.write("D=M-D\n");
            writeDValueToShrinkStack();
        }
        else if (command.equals("neg"))
        {
            writeStackAddressToA(1);
            writer.write("D=-M\n");
            writer.write("@SP\n");
            writer.write("A=M-1\n");
            writer.write("M=D\n");
        }
        else if (command.equals("and"))
        {
            writeStackAddressToA(1);
            writer.write("D=M\n");
            writeStackAddressToA(2);
            writer.write("D=D&M\n");
            writeDValueToShrinkStack();
        }
        else if (command.equals("or"))
        {
            writeStackAddressToA(1);
            writer.write("D=M\n");
            writeStackAddressToA(2);
            writer.write("D=D|M\n");
            writeDValueToShrinkStack();
        }
        else if (command.equals("not"))
        {
            writeStackAddressToA(1);
            writer.write("D=!M\n");
            writer.write("@SP\n");
            writer.write("A=M-1\n");
            writer.write("M=D\n");
        }
        else if (command.equals("eq") || command.equals("gt") ||  command.equals("lt"))
        {
            String jump;
            if (command.equals("eq"))  jump = "JEQ";
            else if (command.equals("gt"))  jump = "JGT";
            else jump = "JLT";

            writeStackAddressToA(1);
            writer.write("D=M\n");
            writeStackAddressToA(2);
            writer.write("D=M-D\n");
            writer.write("@COMPARE." + compareCount +"\n");
            writer.write(String.format("D;%s\n", jump));
            writer.write("D=0\n");
            writeDValueToShrinkStack();
            writer.write("@COMPARE." + compareCount +".end\n");
            writer.write("0;JMP\n");
            writer.write(String.format("(COMPARE.%s)\n", Integer.toString(compareCount)));
            writer.write("D=-1\n");
            writeDValueToShrinkStack();
            writer.write(String.format("(COMPARE.%s.end)\n", Integer.toString(compareCount)));
            compareCount++;
        }
        else
            throw new IllegalStateException();
    }



    public void writePushPop (Parser.COMMAND type, String segment, int index) throws IOException
    {
        if (type == Parser.COMMAND.C_PUSH && segment.equals("constant"))
        {
            writer.write("@" + Integer.toString(index) + "\n");
            writer.write("D=A\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("local"))
        {
            writer.write("@LCL\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("local"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@LCL\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("argument"))
        {
            writer.write("@ARG\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("argument"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@ARG\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("this"))
        {
            writer.write("@THIS\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("this"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@THIS\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("that"))
        {
            writer.write("@THAT\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("that"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@THAT\n");
            writer.write("A=M\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("temp"))
        {
            writer.write("@R5\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("temp"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@R5\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("pointer"))
        {
            writer.write("@THIS\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("pointer"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write("@THIS\n");
            for (int i = 0; i < index; i++)
                writer.write("A=A+1\n");
            writer.write("M=D\n");
        }
        else if (type == Parser.COMMAND.C_PUSH && segment.equals("static"))
        {
            writer.write(String.format("@%s.%d\n", className, index));
            writer.write("D=M\n");
            writeDToExpandStack();
        }
        else if (type == Parser.COMMAND.C_POP && segment.equals("static"))
        {
            writer.write("@SP\n");
            writer.write("M=M-1\n");
            writer.write("A=M\n");
            writer.write("D=M\n");
            writer.write(String.format("@%s.%d\n", className, index));
            writer.write("M=D\n");
        }
    }



}
