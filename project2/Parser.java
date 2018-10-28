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
      // TODO implement <funcDefs>
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
      Node first = parseVar();
      Token token = lex.getNextToken();
      errorCheck( token, "def", "");
      token = lex.getNextToken();
      errorCheck( token, "var", "data");
      token = lex.getNextToken();
      errorCheck( token, "Single", "(" );
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
         // There are no parameters
         errorCheck( token, "Single", ")" );
         // Look ahead to see if there are any statements
         token = lex.getNextToken();
         if ( token.isKind("stmts") ) {
             // <funcDef> -> def <var> ( ) <statements> end
             lex.putBackToken( token );
             Node second = parseStatements();
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, second, null );
         }
         else {
             // <funcDef> -> def <var> ( ) end
             token = lex.getNextToken();
             errorCheck( token, "end", "" );
             lex.putBackToken( token );
             return new Node( "funcDef", first, null, null );
         }
      }
   } // funcDef

   private Node parseParams() {
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
 
      if ( token.isKind("eof") ) {
         return new Node( "stmts", first, null, null );
      }
      else {
         lex.putBackToken( token );
         Node second = parseStatements();
         return new Node( "stmts", first, second, null );
      }
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
            return new Node( "funcCall", first, second, null );
        }
        else {
            // <funcCall> -> <var> ( )
            token = lex.getNextToken();
            errorCheck( token, "Single", ")");
            return new Node( "funcCall", first, null, null );
        }
   } // funcCall

   private Node parseArgs() {
      // TODO implement <args>
      return new Node( "args", null, null, null );
   }

   private Node parseStatement() {
      System.out.println("-----> parsing <statement>:");
 
      Token token = lex.getNextToken();
 
      // ---------------->>>  print <string>  or   print <expr>
      if ( token.isKind("print") ) {
         token = lex.getNextToken();
 
         if ( token.isKind("string") ) {// print <string>
            return new Node( "prtstr", token.getDetails(),
                          null, null, null );
         }
         else {// must be first token in <expr>
            // put back the token we looked ahead at
            lex.putBackToken( token );
            Node first = parseExpr();
            return new Node( "prtexp", first, null, null );
         }
      // ---------------->>>  newline
      }
      else if ( token.isKind("newline") ) {
         return new Node( "nl", null, null, null );
      }
      // --------------->>>   <var> = <expr>
      else if ( token.isKind("var") ) {
         String varName = token.getDetails();
         token = lex.getNextToken();
         errorCheck( token, "single", "=" );
         Node first = parseExpr();
         return new Node( "sto", varName, first, null, null );
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
         return new Node( token.getDetails(), first, second, null );
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
         return new Node( token.getDetails(), first, second, null );
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
      if( !token.isKind("var") ) {
         System.out.println("Error: expected var, got " + token.getDetails());
         System.exit(1);
      }
      String varName = token.getDetails();
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
			  " it is actually " + token.getKind() + 
                          " and details=" + details );
      System.exit(1);
    }
  }

}
