/*
    This class provides a recursive descent parser 
    for Corgi (a simple calculator language),
    creating a parse tree which can be interpreted
    to simulate execution of a Corgi program
*/

import java.util.*;
import java.io.*;

public class Parser {

   private Lexer lex;

   public Parser( Lexer lexer ) {
      lex = lexer;
   }

   public Node parseProgram() {
      System.out.println("-----> parsing <program>");
      Node first = parseFuncCall();

      // look ahead to see if there are funcDefs
      Token token = lex.getNextToken();

      if ( token.isKind("eof") ) {
         // <program> -> <funcCall>
         lex.putBackToken( token );
         return new Node("funcCall", first, null, null );    
      }
      else {
         // <program> -> <funcCall> <funcDefs>
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "funcDefs", first, second, null );
      }
   }

   private Node parseFuncDefs() {
      System.out.println("-----> parsing <funcDefs>");
      Node first = parseFuncDef();
      // Check if there is another funcDef
      Token token = lex.getNextToken();
      if( token.isKind( "funcDef" ) ) {
         // <funcDefs> -> <funcDef> <funcDefs>
         Node second = parseFuncDef();
         return new Node( "funcDefs", first, second, null );
      }
      else {
         // <funcDefs> -> <funcDef>
         return new Node( "funcDefs", first, null, null );
      }
   } // <funcDefs>

   private Node parseFuncDef() {
      // A funcDef node will have three children, the first will be the
      // variable name, the second will be a <params> node, and the third
      // will be a <statemenets> node. If there are no <params>, then the
      // second child will be null, if there are no <statements>, the third
      // child will be null.k=
      System.out.println("-----> parsing <funcDef>:");
      Token token = lex.getNextToken();
      errorCheck( token, "def", "");
      Node first = parseVar();
      token = lex.getNextToken();
      errorCheck( token, "Single", "(");
      // Look ahead to see if there are any parameters
      token = lex.getNextToken();
      if( token.isKind("param") ) {
         // There are params    
         Node second = parseParams();
         errorCheck( token, "Single", ")" );
         // Look ahead again to see if there are any statements
         token = lex.getNextToken();
         if ( token.isKind("stmts") ) {
             // <funcDef> -> def <var> ( <params> ) <statements> end
             lex.putBackToken( token );
             Node third = parseStatements();
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, second, third );
         }
         else {
             // <funcDef> -> def <var> ( <params> ) end
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, second, null );
         }
      }
      else {
         System.out.println("There are no parameters");
         // There are no parameters
         errorCheck( token, "Single", ")" );
         // Look ahead to see if there are any statements
         token = lex.getNextToken();
         System.out.println(token.getKind());
         if ( token.isKind("end") ) {
             System.out.println("There are no more statements");
             // <funcDef> -> def <var> ( ) end
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, null, null );
         }
         else {
             // <funcDef> -> def <var> ( ) <statements> end
             lex.putBackToken( token );
             Node second = parseStatements();
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, second, null );
         }
      }
   } // funcDef

   private Node parseParams() {
      System.out.println("-----> parsing <params>");
      Token token = lex.getNextToken();
      errorCheck( token, "var", "data" );
      Node first = parseVar();
      // Look ahead for more vars
      token = lex.getNextToken();
      if( token.isKind( "var" ) ) {
         // <params> -> <var>, <params>    
         lex.putBackToken( token );
         Node second = parseParams();
         return new Node("params", first, second, null);
      }
      else {
         // <params> -> <var>
         lex.putBackToken( token );
         return new Node("params", first, null, null);
      }
   } // <params>

   private Node parseStatements() {
      System.out.println("-----> parsing <statements>:");
 
      Node first = parseStatement();
 
      // look ahead to see if there are more statement's
      Token token = lex.getNextToken();
      if ( token.isKind("stmts") ) {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      } else {
         return new Node( "stmts", first, null, null );   
      }
 
      /*
      if ( token.isKind("eof") ) {
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
      */
   } // <statements>

   private Node parseFuncCall() {
        System.out.println("-----> parsing <funcCall>:");
        Node first = parseVar();
        Token token = lex.getNextToken();
        errorCheck( token, "Single", "(");
        // Look to see if there are any arguments
        token = lex.getNextToken();
        if ( token.isKind("expr") ) {
            // <funcCall> -> <var> ( <args> )
            lex.putBackToken( token );
            Node second = parseArgs();
            System.out.println("Finished parsing <funcCall> -> <var> ( <args> )");
            return new Node( "funcCall", first, second, null );
        }
        else {
            // <funcCall> -> <var> ( )
            errorCheck( token, "Single", ")");
            System.out.println("Finished parsing <funcCall> -> <var> ( )");
            return new Node( "funcCall", first, null, null );
        }
   } // funcCall

   private Node parseArgs() {
      // TODO implement <args>
      System.out.println("-----> parsing <args> NIY");
      return new Node( "args", null, null, null );
   }

   private Node parseStatement() {
      System.out.println("-----> parsing <statement>:");
 
      Token token = lex.getNextToken();
 
      if ( token.isKind("string") ) {// print <string>
         return new Node( "prtstr", token.getDetails(),
                       null, null, null );
      }
      else if ( token.isKind("var") ) {
         String varName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "=" );
         Node first = parseExpr();
         return new Node( "sto", varName, first, null, null );
      }
      else if ( token.isKind("funcCall") ) {
         first = parseFuncCall();
         return new Node( "funcCall", first, null, null);
      }
      else if ( token.isKind("if") ) {
         // Return a cond node, A cond node will have three children,
         // The first child will always be an expr, which represent
         // the condition of the if statement, the second child
         // will be the statement(s) to execute if the condition is
         // true, it may be null if there are no statements. The third
         // child is the statements to execute if the condition is false,
         // it may also be null if there are no statements.
         token = lex.getNextToken();
         errorcheck(token, "expr");
         lex.putBackToken();
         first = parseExpr();
         // Check for else
         token = lex.getNextToken();
         if ( token.isKind( "else" ) ) {
            // Check for end
            token = lex.getNextToken();
            if( token.isKind( " end " ) ) {
                // <statement> -> if <expr> else end
                return new Node("cond", first, null, null);
            }
            else {
                third = parseStatements();
                // Check for end
                token = lex.getNextToken();
                errorCheck(token, "end");
                // <statement> -> if <expr> else <statments> end
                return new Node("cond", first, null, third);
            }
         }
         else {
            lex.putBackToken();
            second = parseStatements();
            //TODO more work here
         }
      }
      else {
         System.out.println("Token " + token + 
                             " can't begin a statement");
         System.exit(1);
         return null;
      }
 
   }// <statement>

   private Node parseExpr() {
      System.out.println("-----> parsing <expr>");

      Node first = parseTerm();

      // look ahead to see if there's an addop
      Token token = lex.getNextToken();
 
      if ( token.matches("single", "+") ||
           token.matches("single", "-") 
         ) {
         Node second = parseExpr();
         return new Node( "expr", token.getDetails(), first, second, null );
      }
      else {// is just one term
         lex.putBackToken( token );
         return first;
      }

   }// <expr>

   private Node parseTerm() {
      System.out.println("-----> parsing <term>");

      Node first = parseFactor();

      // look ahead to see if there's a multop
      Token token = lex.getNextToken();
 
      if ( token.matches("single", "*") ||
           token.matches("single", "/") 
         ) {
         Node second = parseTerm();
         return new Node( "term", token.getDetails(), first, second, null );
      }
      else {// is just one factor
         lex.putBackToken( token );
         return first;
      }
      
   }// <term>

   private Node parseFactor() {
      System.out.println("-----> parsing <factor>");

      Token token = lex.getNextToken();

      if ( token.isKind("num") ) {
         return new Node("num", token.getDetails(), null, null, null );
      }
      //TODO: check to see if this is necessary
      else if ( token.isKind("var") ) {
         return new Node("var", token.getDetails(), null, null, null );
      }
      else if ( token.matches("single","(") ) {
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         return first;
      }
      else if ( token.isKind("bif0") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         
         return new Node( bifName, null, null, null );
      }
      else if ( token.isKind("bif1") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         
         return new Node( bifName, first, null, null );
      }
      else if ( token.isKind("bif2") ) {
         String bifName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "(" );
         Node first = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", "," );
         Node second = parseExpr();
         token = lex.getNextToken();
         errorCheck( token, "single", ")" );
         
         return new Node( bifName, first, second, null );
      }
      else if ( token.matches("single","-") ) {
         Node first = parseFactor();
         return new Node("opp", first, null, null );
      }
      else {
         System.out.println("Can't have factor starting with " + token );
         System.exit(1);
         return null;
      }
      
   }// <factor>

   private Node parseVar() {
      //TODO implement parseVar
      // Also, figure out whether this should actually be here, it isn't in
      // the CFG, but it seems like we have to have this here.
      System.out.println("-----> parsing <var>:");
      Token token = lex.getNextToken();
      if( ! token.isKind( "var" ) ) {
         System.out.println("Error: expected var, got " + token.getDetails());
         System.exit(1);
      }
      String varName = token.getDetails();
      System.out.println("Finished parsing <var>");
      return new Node (varName, null, null, null, null);
   } // <var>

  // check whether token is correct kind
  private void errorCheck( Token token, String kind ) {
    if( ! token.isKind( kind ) ) {
      System.out.println("Error:  expected " + token + 
                         " to be of kind " + kind );
      System.exit(1);
    }
  }

  // check whether token is correct kind and details
  private void errorCheck( Token token, String kind, String details ) {
    if( ! token.isKind( kind ) || 
        ! token.getDetails().equals( details ) ) {
      System.out.println("Error:  expected " + token + 
                          " to be kind=" + kind + 
                          " and to be details=" + details +
			              " it is actually kind=" + token.getKind() + 
                          " and details=" + details );
      System.exit(1);
    }
  }

}
