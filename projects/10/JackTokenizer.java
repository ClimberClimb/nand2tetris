import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import jdk.nashorn.internal.parser.TokenType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class JackTokenizer
{
    enum TOKENTYPE {
        KEYWORD, SYMBOL, IDENTIFIER, INI_CONST, STRING_CONST
    }

    enum KEYWORDS {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID,
        VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE,
        NULL, THIS
    }

    private In jackfile;
    private String curToken;
    private String curString;
    private String nextString;
    private int leftBracketRemain;
    private boolean inited;

    private static final String symbolString = "{}()[].,;+-*/&|<>=~";

    JackTokenizer(String filename)
    {
        jackfile = new In(filename);
        curToken = null;
        leftBracketRemain = 0;
        inited = false;
    }

    boolean hasMoreTokens()
    {
        return !inited || leftBracketRemain != 0;
    }

    void advance()
    {
        curToken = null;
        while (curToken == null || curToken.equals(""))
        {
            if (nextString == null) curString = jackfile.readString().trim();
            else curString = nextString;

            if (curString.startsWith("//"))
            {
                jackfile.readLine();
            }
            else if (curString.startsWith("/*") || curString.startsWith("/**"))
            {
                String curr = "";
                while (!curr.equals("*/"))
                    curr = jackfile.readString().trim();
            }
            else if (curString.startsWith("\""))
            {
                StringBuilder st = new StringBuilder();
                st.append(curString.substring(1));
                char c = jackfile.readChar();
                while (c != '\"')
                {
                    st.append(c);
                    c = jackfile.readChar();
                }
                curToken = "\"" + st.toString();
                nextString = null;
            } else {
                char[] chars = curString.toCharArray();

                boolean needRead = true;
                for (int i = 0; i < chars.length; i++) {
                    if (symbolString.contains(Character.toString(chars[i]))) {
                        if (i != 0) {
                            curToken = curString.substring(0, i);
                            nextString = curString.substring(i);
                            needRead = false;
                        } else {
                            curToken = Character.toString(chars[i]);
                            if (i + 1 <= chars.length - 1) {
                                nextString = curString.substring(i + 1);
                                needRead = false;
                            }
                        }
                        break;
                    }
                }

                if (needRead) {
                    curToken = curString;
                    nextString = null;
                }

                if (curToken.equals("{")) {
                    leftBracketRemain++;
                    inited = true;
                } else if (curToken.equals("}")) {
                    leftBracketRemain--;
                }
            }
        }
    }

    private boolean inKeyword(String str)
    {
        for (KEYWORDS me : KEYWORDS.values())
        {
            if (me.name().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

    TOKENTYPE tokenType()
    {
        if (curToken.length() == 1 && symbolString.contains(curToken))
        {
            return TOKENTYPE.SYMBOL;
        }
        else if (inKeyword(curToken))
        {
            return TOKENTYPE.KEYWORD;
        }
        else if (curToken.startsWith("\""))
        {
            return TOKENTYPE.STRING_CONST;
        }
        else if ('0' <= curToken.charAt(0) && curToken.charAt(0) <= '9')
        {
            return TOKENTYPE.INI_CONST;
        }
        else
            return TOKENTYPE.IDENTIFIER;
    }

    String keyWord()
    {
        if (tokenType() == TOKENTYPE.KEYWORD)
            return curToken.toLowerCase();
        else
            throw new IllegalStateException();
    }

    String symbol()
    {
        if (tokenType() == TOKENTYPE.SYMBOL)
        {
            String symbol;
            if (curToken.equals("<"))
                symbol = "&lt;";
            else if (curToken.equals(">"))
                symbol = "&gt;";
            else if (curToken.equals("&"))
                symbol = "&amp;";
            else
                symbol = curToken;
            return symbol;
        }
        else
            throw new IllegalStateException();
    }

    String identifier()
    {
        if (tokenType() == TOKENTYPE.IDENTIFIER)
            return curToken;
        else
            throw new IllegalStateException();
    }

    int intVal()
    {
        if (tokenType() == TOKENTYPE.INI_CONST)
            return Integer.parseInt(curToken);
        else
            throw new IllegalStateException();
    }

    String stringVal()
    {
        if (tokenType() == TOKENTYPE.STRING_CONST)
            return curToken.substring(1);
        else
            throw new IllegalStateException();
    }

    public static void main(String[] args) throws IOException
    {
        String filename = "/Users/soso/Desktop/nand2tetris/projects/10/ExpressionLessSquare/Square.jack";
        JackTokenizer tokenizer = new JackTokenizer(filename);
        String outFile = "/Users/soso/Desktop/nand2tetris/projects/10/ExpressionLessSquare/SquareT_gen.xml";

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        writer.write("<tokens>\n");
        while (tokenizer.hasMoreTokens())
        {
            tokenizer.advance();
            if (tokenizer.tokenType() == TOKENTYPE.KEYWORD)
            {
                writer.write(String.format("<keyword> %s </keyword>\n", tokenizer.keyWord()));
            }
            else if (tokenizer.tokenType() == TOKENTYPE.INI_CONST)
            {
                writer.write(String.format("<integerConstant> %s </integerConstant>\n", tokenizer.intVal()));
            }
            else if (tokenizer.tokenType() == TOKENTYPE.STRING_CONST)
            {
                writer.write(String.format("<stringConstant> %s </stringConstant>\n", tokenizer.stringVal()));
            }
            else if (tokenizer.tokenType() == TOKENTYPE.SYMBOL)
            {
                writer.write(String.format("<symbol> %s </symbol>\n", tokenizer.symbol()));
            }
            else if (tokenizer.tokenType() == TOKENTYPE.IDENTIFIER)
            {
                writer.write(String.format("<identifier> %s </identifier>\n", tokenizer.identifier()));
            }
            else
                throw new IllegalStateException();
        }
        writer.write("</tokens>\n");
        writer.close();
    }

}
