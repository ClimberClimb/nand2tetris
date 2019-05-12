public class Code
{
    public static String dest(String mnemoic)
    {
        if (mnemoic == null)
            return  "000";
        else if (mnemoic.equals("M"))
            return "001";
        else if (mnemoic.equals("D"))
            return "010";
        else if (mnemoic.equals("MD"))
            return "011";
        else if (mnemoic.equals("A"))
            return "100";
        else if (mnemoic.equals("AM"))
            return "101";
        else if (mnemoic.equals("AD"))
            return "110";
        else if (mnemoic.equals("AMD"))
            return "111";
        else
            throw new IllegalArgumentException();

    }

    public static String comp(String mnemoic)
    {
        if (mnemoic.equals("0"))
            return "0101010";
        else if (mnemoic.equals("1"))
            return "0111111";
        else if (mnemoic.equals("-1"))
            return "0111010";
        else if (mnemoic.equals("D"))
            return "0001100";
        else if (mnemoic.equals("A"))
            return "0110000";
        else if (mnemoic.equals("!D"))
            return "0001101";
        else if (mnemoic.equals("!A"))
            return "0110001";
        else if (mnemoic.equals("-D"))
            return "0001111";
        else if (mnemoic.equals("-A"))
            return "0110011";
        else if (mnemoic.equals("D+1"))
            return "0011111";
        else if (mnemoic.equals("A+1"))
            return "0110111";
        else if (mnemoic.equals("D-1"))
            return "0001110";
        else if (mnemoic.equals("A-1"))
            return "0110010";
        else if (mnemoic.equals("D+A"))
            return "0000010";
        else if (mnemoic.equals("D-A"))
            return "0010011";
        else if (mnemoic.equals("A-D"))
            return "0000111";
        else if (mnemoic.equals("D&A"))
            return "0000000";
        else if (mnemoic.equals("D|A"))
            return "0010101";
        else if (mnemoic.equals("M"))
            return "1110000";
        else if (mnemoic.equals("!M"))
            return "1110001";
        else if (mnemoic.equals("-M"))
            return "1110011";
        else if (mnemoic.equals("M+1"))
            return "1110111";
        else if (mnemoic.equals("M-1"))
            return "1110010";
        else if (mnemoic.equals("D+M"))
            return "1000010";
        else if (mnemoic.equals("D-M"))
            return "1010011";
        else if (mnemoic.equals("M-D"))
            return "1000111";
        else if (mnemoic.equals("D&M"))
            return "1000000";
        else if (mnemoic.equals("D|M"))
            return "1010101";
        else
            throw new IllegalArgumentException();
    }

    public static String jump(String mnemoic)
    {
        if (mnemoic == null)
            return  "000";
        else if (mnemoic.equals("JGT"))
            return "001";
        else if (mnemoic.equals("JEQ"))
            return "010";
        else if (mnemoic.equals("JGE"))
            return "011";
        else if (mnemoic.equals("JLT"))
            return "100";
        else if (mnemoic.equals("JNE"))
            return "101";
        else if (mnemoic.equals("JLE"))
            return "110";
        else if (mnemoic.equals("JMP"))
            return "111";
        else
            throw new IllegalArgumentException();
    }

    public static void main(String[] args)
    {
        String s = Code.dest("AM");
        System.out.println(s);
    }
}
