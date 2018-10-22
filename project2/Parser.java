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
      return parseStatements();

      /* Commented for for now because I'm afraid
      Node first = parseFuncCall();

      // look ahead to see if there are funcDefs
      Token token = lex.getNextToke();

      if ( token.isKind("eof") ) {
         return new Node("funcCall", first, null, null );    
      }
      else {
         lex.putBackToken( token );
         Node second = parseFuncDefs();
         return new Node( "funcDefs", first, second, null );
      }
      */
   }

   private Node parseFuncDef() {
      // TODO, actually make this work
      System.out.println("-----> parsing <funcDef>:");
      Node first = parseVar();
      Token token = lex.getNextToken();
      errorCheck( Token, "def", "");
      token = lex.getNextToken();
      if ( token.isKind("var") {
         token = lex.getNextToken();
         errorCheck( token, "Single", "(" );
         // Look ahead to see if there are any <params>
         token = lex.getNextToken();
         if( token.isKind("param") ) {
            // There are params    
            Node second = parseParams();
            errorCheck( token, "Single", ")" );
            // Look ahead again
            token = lex.getNextToken();
            if ( token.isKind("stmts") ) {
                lex.putBackToken( token );
                Node third = parseStatements();
                token = lex.getNextToken();
                errorCheck( token, "Single", "end" );
                lex.putBackToken( token );
                return new Node( "funcDef", first, second, third );
            }
            else {
                token = lex.getNextToken();
                errorCheck( token, "Single", "end" );
                lex.putBackToken( token );
                return new Node( "funcDef", first, second, null );
            }
         }
         else {
            // There are no parameters
            errorCheck( token, "Single", ")" );
            // Look ahead again
            token = lex.getNextToken();
            if ( token.isKind("stmts") ) {
                lex.putBackToken( token );
                Node second = parseStatements();
                token = lex.getNextToken();
                errorCheck( token, "Single", "end" );
                lex.putBackToken( token );
                return new Node( "funcDef", first, second, null );
            }
            else {
                token = lex.getNextToken();
                errorCheck( token, "Single", "end" );
                lex.putBackToken( token );
                return new Node( "funcDef", first, null, null );
            }
         }
      }
      else {
         // If we get here, wrong token
         // TODO improve this error message by stating which function name was bad
         System.out.format("Syntax error: Bad function name.\n");
         System.exit(1);
      }
   }

   private Node parseFuncCall() {
        System.out.println("-----> parsing <funcCall>:");
        Node first = parseVar();
        Token token = lex.getNextToken();
        errorCheck( Token, "Single", "(");
        // Look to see if there are any arguments
        token = lex.getNextToken();
        if ( token.isKind("expr") ) {
            // There are arguments
            lex.putBackToken( token );
            Node second = parseArgs();
            return new Node( "funcCall", first, second, null );
        }
        else {
            // There are no arguments
            lex.packBackToken( token );
            errorCheck( Token, "Single", ")");
            return new Node( "funcCall", first, null, null );
        }
   }

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
   }// <statements>

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
