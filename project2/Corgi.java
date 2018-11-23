//https://github.com/jamesahhh/Corgi
import java.util.Scanner;

public class Corgi {

  public static void main(String[] args) throws Exception {

    /*
    System.out.print("Enter name of Corgi program file: ");
    Scanner keys = new Scanner( System.in );
    String name = keys.nextLine();
    */
    String name = args[0];

    Lexer lex = new Lexer( name );
    Parser parser = new Parser( lex );

    // start with <program>
    Node root = parser.parseProgram();

    // display parse tree for debugging/testing:
    TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root );

    // execute the parse tree
    // root.execute();

  }// main

}
