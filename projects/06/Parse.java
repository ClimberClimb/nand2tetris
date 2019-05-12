import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import java.io.*;


public class Parse
{
    private In asmfile;
    private String currentLine;
    private LinearProbingHashST<String, String> symbols;
    private int count;
    private int address;


    enum COMMAND {
        A_COMMAND,
        C_COMMAND,
        L_COMMAND
    }

    Parse(String name)
    {
        symbols = new LinearProbingHashST<>();
        address = 16;
        // build the symbol
        asmfile = new In(name);
        currentLine = null;
        buildSymbol();
        close();
        // format the binary string
        asmfile = new In(name);
        currentLine = null;
    }

    private void buildSymbol()
    {
        symbols.put("SP", "0");
        symbols.put("LCL", "1");
        symbols.put("ARG", "2");
        symbols.put("THIS", "3");
        symbols.put("THAT", "4");
        symbols.put("SCREEN", "16384");
        symbols.put("KBD", "24576");
        for (int i = 0; i < 16; i++)
        {
            symbols.put("R" + Integer.toString(i), Integer.toString(i));
        }
        while (hasMoreCommands())
        {
            advance();
            if (commandType() == COMMAND.C_COMMAND || commandType() == COMMAND.A_COMMAND) {
                count++;
            }
            else if (commandType() == COMMAND.L_COMMAND) {
                symbols.put(currentLine.substring(1, currentLine.length() - 1), Integer.toString(count));
            }
            else
                ;
        }
    }

    boolean hasMoreCommands()
    {
        return asmfile.hasNextLine();
    }

    void advance()
    {
        String line = asmfile.readLine().trim();
        while (line.equals("") || line.startsWith("//"))
            line = asmfile.readLine();
        int cutIndex = line.indexOf("//");
        if (cutIndex == -1)
            currentLine = line.trim();
        else
            currentLine = line.substring(0, cutIndex).trim();
    }

    COMMAND commandType()
    {
        if (currentLine.startsWith("@"))
            return COMMAND.A_COMMAND;
        else if (currentLine.startsWith("("))
            return COMMAND.L_COMMAND;
        else
            return COMMAND.C_COMMAND;
    }

    String dest()
    {
        if (commandType() != COMMAND.C_COMMAND)
            throw new IllegalStateException();

        String dest = null;
        int cutIndex = currentLine.indexOf("=");
        if (cutIndex != -1)
            dest = currentLine.substring(0, cutIndex).trim();
        return Code.dest(dest);
    }

    String comp()
    {
        if (commandType() != COMMAND.C_COMMAND)
            throw new IllegalStateException();
        int cutIndex = currentLine.contains("=") ?  currentLine.indexOf("=") + 1 : 0;
        int cutJumpIndex = currentLine.contains(";") ? currentLine.indexOf(";") : currentLine.length();
        String comp = currentLine.substring(cutIndex, cutJumpIndex);
        return Code.comp(comp);
    }

    String jump()
    {
        if (commandType() != COMMAND.C_COMMAND)
            throw new IllegalStateException();

        String jump = null;
        int cutIndex = currentLine.indexOf(";");
        if (cutIndex != -1)
        {
            jump = currentLine.substring(cutIndex + 1).trim();
        }
        return Code.jump(jump);
    }

    String formatBinary()
    {
        String ret;
        if (commandType() == COMMAND.C_COMMAND)
            ret = "111" + comp() + dest() + jump();
        else if (commandType() == COMMAND.A_COMMAND)
        {
            String item = currentLine.substring(1);
            String constant;
            if (!item.matches("-?\\d+"))
            {
                if (!symbols.contains(item))
                {
                    symbols.put(item, Integer.toString(address++));
                }
                constant = symbols.get(item);
            }
            else
                constant = item;
            Integer address = Integer.parseInt(constant);
            String binaryStr = Integer.toBinaryString(address);
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < 16 - binaryStr.length();i++)
                prefix.append("0");
            ret = prefix.toString() + binaryStr;
        }
        else
            ret = "";
        return ret;
    }


    void close()
    {
        asmfile.close();
    }

    public static void main (String[] args) throws IOException
    {
        Parse parsing = new Parse("/Users/soso/Desktop/nand2tetris/projects/06/pong/Pong.asm");
        String outFile = "/Users/soso/Desktop/nand2tetris/projects/06/pong/Pong.hack";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        while (parsing.hasMoreCommands())
        {
            parsing.advance();
            String ret = parsing.formatBinary();
            if (!ret.equals(""))
            {
                writer.write(ret + "\n");
            }
        }
        parsing.close();
        writer.close();
    }
}
