import edu.princeton.cs.algs4.StdOut;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine
{
    private BufferedWriter writer;
    private JackTokenizer tokenizer;
    private int indent;


    CompilationEngine(String inFile, String outFile) throws IOException
    {
        tokenizer = new JackTokenizer(inFile);
        writer = new BufferedWriter(new FileWriter(outFile));
        indent = 0;
    }
    
    private void Write(String fmtString) throws IOException
    {
        StdOut.print(fmtString);
        writer.write(fmtString);
    }

    private String getIndent()
    {
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < indent; i++)
        {
            st.append("  ");
        }
        return st.toString();
    }

    void compileClass() throws IOException
    {
        Write("<class>\n");
        indent++;

        tokenizer.advance();
        if (!tokenizer.keyWord().equals("class"))
            throw new IllegalArgumentException();
        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        Write(String.format("%s<identifier> %s </identifier>\n",  getIndent(), tokenizer.identifier()));

        tokenizer.advance();
        if (!tokenizer.symbol().equals("{"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n",  getIndent(), tokenizer.symbol()));

        tokenizer.advance();
        while (tokenizer.keyWord().equals("static") || tokenizer.keyWord().equals("field"))
        {
            compileClassVarDec();
            tokenizer.advance();
        }

        while (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD &&(tokenizer.keyWord().equals("constructor")
                || tokenizer.keyWord().equals("function") || tokenizer.keyWord().equals("method")))
        {
            compileSubroutine();
            tokenizer.advance();
        }

        if (!tokenizer.symbol().equals("}"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n",  getIndent(), tokenizer.symbol()));

        indent--;
        Write("</class>\n");
        writer.close();
    }

    void compileClassVarDec() throws IOException
    {
        Write(String.format("%s<classVarDec>\n", getIndent()));
        indent++;
        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD)
        {
            if (tokenizer.keyWord().equals("int") ||  tokenizer.keyWord().equals("char")
                || tokenizer.keyWord().equals("boolean"))
                Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
        }
        else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER)
        {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
        }

        tokenizer.advance();
        Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));

        tokenizer.advance();
        while (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(","))
        {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
            tokenizer.advance();
        }

        if (!tokenizer.symbol().equals(";"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

        indent--;
        Write(String.format("%s</classVarDec>\n", getIndent()));

    }

    void compileSubroutine() throws IOException
    {
        // subroutineDec
        Write(String.format("%s<subroutineDec>\n", getIndent()));
        indent++;

        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD)
        {
            if (tokenizer.keyWord().equals("int") ||  tokenizer.keyWord().equals("char")
                    || tokenizer.keyWord().equals("boolean") || tokenizer.keyWord().equals("void"))
                Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
        }
        else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER)
        {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();
        Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));

        //parameter list
        tokenizer.advance();
        if (!tokenizer.symbol().equals("("))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));


        compileParameterList();

        if (!tokenizer.symbol().equals(")"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

        // body
        Write(String.format("%s<subroutineBody>\n", getIndent()));
        indent++;

        tokenizer.advance();
        if (!tokenizer.symbol().equals("{"))
            throw new IllegalArgumentException();

        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));


        tokenizer.advance();
        while (tokenizer.tokenType()== JackTokenizer.TOKENTYPE.KEYWORD && tokenizer.keyWord().equals("var"))
        {
            compileVarDec();
            tokenizer.advance();
        }

        if (tokenizer.tokenType()== JackTokenizer.TOKENTYPE.KEYWORD && (tokenizer.keyWord().equals("let")
        || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while")
         || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
        {
            compileStatements();
        }

        if (!tokenizer.symbol().equals("}"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

        indent--;
        Write(String.format("%s</subroutineBody>\n", getIndent()));

        indent--;
        Write(String.format("%s</subroutineDec>\n", getIndent()));
    }

    void compileParameterList() throws IOException
    {
        Write(String.format("%s<parameterList>\n", getIndent()));
        indent++;

        while(true)
        {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD) {
                if (tokenizer.keyWord().equals("int") || tokenizer.keyWord().equals("char")
                        || tokenizer.keyWord().equals("boolean") || tokenizer.keyWord().equals("void"))
                    Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
            } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER) {
                Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
            } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")"))
                break;
            else
                throw new IllegalArgumentException();

            tokenizer.advance();
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));

            tokenizer.advance();
            if (tokenizer.symbol().equals(","))
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            else if (tokenizer.symbol().equals(")"))
                break;
            else
                throw new IllegalArgumentException();
        }

        indent--;
        Write(String.format("%s</parameterList>\n", getIndent()));
    }

    void compileVarDec() throws IOException
    {
        Write(String.format("%s<varDec>\n", getIndent()));
        indent++;

        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD)
        {
            if (tokenizer.keyWord().equals("int") ||  tokenizer.keyWord().equals("char")
                    || tokenizer.keyWord().equals("boolean"))
                Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
        }
        else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER)
        {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
        }

        tokenizer.advance();
        Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));

        tokenizer.advance();
        while (tokenizer.symbol().equals(","))
        {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
            tokenizer.advance();
        }

        if (!tokenizer.symbol().equals(";"))
            throw new IllegalArgumentException();
        Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

        indent--;
        Write(String.format("%s</varDec>\n", getIndent()));
    }


    void compileStatements() throws IOException
    {
        Write(String.format("%s<statements>\n", getIndent()));
        indent++;

        while (tokenizer.tokenType()== JackTokenizer.TOKENTYPE.KEYWORD && (tokenizer.keyWord().equals("let")
                || tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("while")
                || tokenizer.keyWord().equals("do") || tokenizer.keyWord().equals("return")))
        {
            if (tokenizer.keyWord().equals("if") || tokenizer.keyWord().equals("else"))
            {
                compileIf();
                continue;
            }
            else if (tokenizer.keyWord().equals("let"))
                compileLet();
            else if (tokenizer.keyWord().equals("while"))
                compileWhile();
            else if (tokenizer.keyWord().equals("do"))
                compileDo();
            else if (tokenizer.keyWord().equals("return"))
                compileReturn();
            else
                throw new IllegalArgumentException();
            tokenizer.advance();
        }
        indent--;
        Write(String.format("%s</statements>\n", getIndent()));

    }


    void compileDo() throws IOException
    {
        Write(String.format("%s<doStatement>\n", getIndent()));
        indent++;

        // do
        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER) {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
        }

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(".")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
            tokenizer.advance();
        }

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("(")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        compileExpressionList();

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        indent--;
        Write(String.format("%s</doStatement>\n", getIndent()));
    }

    void compileLet() throws IOException
    {
        Write(String.format("%s<letStatement>\n", getIndent()));
        indent++;
        // let
        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER) {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("[")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

            tokenizer.advance();
            compileExpression();

            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("]"))
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            else
                throw new IllegalArgumentException();

            tokenizer.advance();
        }

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("=")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();
        compileExpression();

        if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";")))
            tokenizer.advance();

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        indent--;
        Write(String.format("%s</letStatement>\n", getIndent()));
    }

    void compileWhile() throws IOException
    {
        Write(String.format("%s<whileStatement>\n", getIndent()));
        indent++;

        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("(")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();
        compileExpression();


        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        tokenizer.advance();
        if (!tokenizer.symbol().equals("{"))
            throw new IllegalArgumentException();
        else
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));


        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.SYMBOL || tokenizer.symbol().equals("{"))
            compileStatements();
        else
            throw new IllegalArgumentException();

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("}")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else
            throw new IllegalArgumentException();

        indent--;
        Write(String.format("%s</whileStatement>\n", getIndent()));
    }

    void compileReturn() throws IOException
    {
        Write(String.format("%s<returnStatement>\n", getIndent()));
        indent++;

        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        }
        else {
            compileExpression();
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";"))
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            else
                throw new IllegalArgumentException();
        }

        indent--;
        Write(String.format("%s</returnStatement>\n", getIndent()));
    }

    void compileIf() throws IOException
    {
        Write(String.format("%s<ifStatement>\n", getIndent()));
        indent++;

        Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
        if (tokenizer.keyWord().equals("if")) {
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("(")) {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            } else
                throw new IllegalArgumentException();

            tokenizer.advance();
            compileExpression();

            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            } else
                throw new IllegalArgumentException();

            tokenizer.advance();
            if (!tokenizer.symbol().equals("{"))
                throw new IllegalArgumentException();
            else
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.SYMBOL || tokenizer.symbol().equals("}"))
                compileStatements();
            else
                throw new IllegalArgumentException();

            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("}")) {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            }
            else
                throw new IllegalArgumentException();
        }

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD && tokenizer.keyWord().equals("else")) {
            Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
            tokenizer.advance();
            if (!tokenizer.symbol().equals("{"))
                throw new IllegalArgumentException();
            else
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));

            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.SYMBOL || tokenizer.symbol().equals("}"))
                compileStatements();
            else
                throw new IllegalArgumentException();

            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("}")) {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            }
            else
                throw new IllegalArgumentException();
            tokenizer.advance();
        }

        indent--;
        Write(String.format("%s</ifStatement>\n", getIndent()));
    }

    void compileExpression() throws IOException
    {
        Write(String.format("%s<expression>\n", getIndent()));
        indent++;

        compileTerm();

        while (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && (tokenizer.symbol().equals("+") ||
                tokenizer.symbol().equals("+") || tokenizer.symbol().equals("-") || tokenizer.symbol().equals("*") ||
                tokenizer.symbol().equals("/") || tokenizer.symbol().equals("&amp;") || tokenizer.symbol().equals("|") ||
                tokenizer.symbol().equals("&lt;") || tokenizer.symbol().equals("&gt;") || tokenizer.symbol().equals("=")
        ))
        {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            compileTerm();
        }

        indent--;
        Write(String.format("%s</expression>\n", getIndent()));
    }

    void compileTerm() throws IOException {
        Write(String.format("%s<term>\n", getIndent()));
        indent++;

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.INI_CONST) {
            Write(String.format("%s<integerConstant> %s </integerConstant>\n", getIndent(), tokenizer.intVal()));
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.STRING_CONST) {
            Write(String.format("%s<stringConstant> %s </stringConstant>\n", getIndent(), tokenizer.stringVal()));
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD && (tokenizer.keyWord().equals("true") ||
                tokenizer.keyWord().equals("false") || tokenizer.keyWord().equals("null") ||
                tokenizer.keyWord().equals("this"))) {
            Write(String.format("%s<keyword> %s </keyword>\n", getIndent(), tokenizer.keyWord()));
            tokenizer.advance();
        }  else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("(")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            compileExpression();
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
        } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && (tokenizer.symbol().equals("~") ||
                tokenizer.symbol().equals("-"))) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            compileTerm();
        } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
        } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER) {
            Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
            tokenizer.advance();
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("("))
            {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
                compileExpressionList();
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            }
            else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("["))
            {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
                tokenizer.advance();
                compileExpression();
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            }
            else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("."))
            {
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
                tokenizer.advance();
                Write(String.format("%s<identifier> %s </identifier>\n", getIndent(), tokenizer.identifier()));
                tokenizer.advance();
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
                compileExpressionList();
                Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            }
        }
        else
            throw new IllegalArgumentException();

        indent--;
        Write(String.format("%s</term>\n", getIndent()));
    }

    void compileExpressionList() throws IOException
    {
        Write(String.format("%s<expressionList>\n", getIndent()));
        indent++;

        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")"))
        {
            indent--;
            Write(String.format("%s</expressionList>\n", getIndent()));
            return;
        }

        compileExpression();
        while (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(","))
        {
            Write(String.format("%s<symbol> %s </symbol>\n", getIndent(), tokenizer.symbol()));
            tokenizer.advance();
            compileExpression();
        }

        indent--;
        Write(String.format("%s</expressionList>\n", getIndent()));
    }


    public static void main(String[] args) throws IOException
    {
        String inFile = "/Users/soso/Desktop/nand2tetris/projects/10/ExpressionLessSquare/SquareGame.jack";
        String outFile = "/Users/soso/Desktop/nand2tetris/projects/10/ExpressionLessSquare/SquareGame_gen.xml";
        CompilationEngine engine = new CompilationEngine(inFile, outFile);
        engine.compileClass();
    }
}
