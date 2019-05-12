import edu.princeton.cs.algs4.In;
import java.io.File;
import java.util.Arrays;
import java.io.IOException;


public class Parser
{
    private In vmfile;
    private String currentLine;
    private static final String[] ops = new String[] {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

     enum COMMAND {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

    Parser(String name)
    {
        vmfile = new In(name);
        currentLine = null;
    }

    public Parser(File f)
    {
        vmfile = new In(f);
        currentLine = null;
    }

    public void close()
    {
        vmfile.close();
    }

    boolean hasMoreCommands()
    {
        return vmfile.hasNextLine();
    }

    COMMAND commandType()
    {   if (Arrays.asList(ops).indexOf(currentLine) != -1) return COMMAND.C_ARITHMETIC;
        else if (currentLine.startsWith("push"))  return COMMAND.C_PUSH;
        else if (currentLine.startsWith("pop"))  return COMMAND.C_POP;
        else if (currentLine.startsWith("label"))  return COMMAND.C_LABEL;
        else if (currentLine.startsWith("goto"))  return COMMAND.C_GOTO;
        else if (currentLine.startsWith("if-goto"))  return COMMAND.C_IF;
        else if (currentLine.startsWith("function"))  return COMMAND.C_FUNCTION;
        else if (currentLine.startsWith("call"))  return COMMAND.C_CALL;
        else if (currentLine.startsWith("return"))  return COMMAND.C_RETURN;
        else throw new IllegalStateException();
    }

    String arg1()
    {
        COMMAND type = commandType();
        if (type == COMMAND.C_ARITHMETIC)
            return currentLine;
        else if (type == COMMAND.C_RETURN)
            throw new IllegalStateException();
        else
            return currentLine.split("\\s+")[1].trim();
    }

    int arg2()
    {
        COMMAND type = commandType();
        if (type == COMMAND.C_PUSH || type == COMMAND.C_POP  || type == COMMAND.C_FUNCTION || type == COMMAND.C_CALL)
            return Integer.parseInt(currentLine.split("\\s+")[2].trim());
        else
            throw new IllegalStateException();
    }

    void advance()
    {
        String line = vmfile.readLine().trim();
        while (line.equals("") || line.startsWith("//"))
            line = vmfile.readLine().trim();
        int cutIndex = line.indexOf("//");
        if (cutIndex == -1)
            currentLine = line;
        else
            currentLine = line.substring(0, cutIndex).trim();
    }

    public static void main (String[] args) throws IOException
    {
        File[] files = new File("/Users/soso/Desktop/nand2tetris/projects/08/FunctionCalls/StaticsTest/").listFiles();

        CodeWriter w = new CodeWriter("/Users/soso/Desktop/nand2tetris/projects/08/FunctionCalls/StaticsTest/StaticsTest.asm");

        w.writeInit();
        for (File file : files)
        {
            if (file.isFile() && file.getName().endsWith("vm"))
            {
                Parser parse = new Parser(file.getAbsolutePath());
                String filename = file.getName();
                w.setFileName(filename.substring(0, filename.lastIndexOf(".")));
                while (parse.hasMoreCommands())
                {
                    parse.advance();
                    COMMAND type = parse.commandType();
                    if (type == COMMAND.C_ARITHMETIC)
                    {
                        w.writeArithmetic(parse.arg1());
                    }
                    else if (type == COMMAND.C_PUSH || type == COMMAND.C_POP)
                    {
                        w.writePushPop(type, parse.arg1(), parse.arg2());
                    }
                    else if (type == COMMAND.C_LABEL)
                    {
                        w.writeLabel(parse.arg1());
                    }
                    else if (type == COMMAND.C_GOTO)
                    {
                        w.writeGoto(parse.arg1());
                    }
                    else if (type == COMMAND.C_IF)
                    {
                        w.writeIf(parse.arg1());
                    }
                    else if (type == COMMAND.C_CALL)
                    {
                        w.writeCall(parse.arg1(), parse.arg2());
                    }
                    else if (type == COMMAND.C_RETURN)
                    {
                        w.writeReturn();
                    }
                    else if (type == COMMAND.C_FUNCTION)
                    {
                        w.writeFunction(parse.arg1(), parse.arg2());
                    }
                }
                parse.close();
            }
        }
        w.writeEnd();
        w.close();
    }
}
