// Original code written by Dr. Jerry Shultz
// Additions made by Eric Kearney
import java.io.*;
import java.util.*;

public class VPL
{
  static String fileName;
  static Scanner keys;

  static int max;
  static int[] mem;
  static int ip, bp, sp, rv, hp, numPassed, gp;
  static int step;

  public static void main(String[] args) throws Exception {

    keys = new Scanner( System.in );

    if( args.length != 2 ) {
      System.out.println("Usage: java VPL <vpl program> <memory size>" );
      System.exit(1);
    }

    fileName = args[0];

    max = Integer.parseInt( args[1] );
    mem = new int[max];

    // load the program into the front part of
    // memory
    Scanner input = new Scanner( new File( fileName ) );
    String line;
    StringTokenizer st;
    int opcode;

    ArrayList<IntPair> labels, holes;
    labels = new ArrayList<IntPair>();
    holes = new ArrayList<IntPair>();
    int label;

    // load the code

    int k=0;
    while ( input.hasNextLine() ) {
      line = input.nextLine();
      System.out.println("parsing line [" + line + "]");
      if( line != null )
      {// extract any tokens
        st = new StringTokenizer( line );
        if( st.countTokens() > 0 )
        {// have a token, so must be an instruction (as opposed to empty line)

          opcode = Integer.parseInt(st.nextToken());

          // load the instruction into memory:

          if( opcode == labelCode )
          {// note index that comes where label would go
            label = Integer.parseInt(st.nextToken());
            labels.add( new IntPair( label, k ) );
          }
          else if( opcode == noopCode ){
          }
          else
          {// opcode actually gets stored
            mem[k] = opcode;  k++;
 
            if( opcode == callCode || opcode == jumpCode ||
                opcode == condJumpCode )
            {// note the hole immediately after the opcode to be filled in later
              label = Integer.parseInt( st.nextToken() );
              mem[k] = label;  holes.add( new IntPair( k, label ) );
              ++k;
            }

            // load correct number of arguments (following label, if any):
            for( int j=0; j<numArgs(opcode); ++j )
            {
              mem[k] = Integer.parseInt(st.nextToken());
              ++k;
            }

          }// not a label

        }// have a token, so must be an instruction
      }// have a line
    }// loop to load code
    
    //System.out.println("after first scan:");
    //showMem( 0, k-1 );

    // fill in all the holes:
    int index;
    for( int m=0; m<holes.size(); ++m )
    {
      label = holes.get(m).second; // iterate on every second element
      index = -1;
      for( int n=0; n<labels.size(); ++n )
        if( labels.get(n).first == label )
          index = labels.get(n).second;
      mem[ holes.get(m).first ] = index;
    }

    System.out.println("after replacing labels:");
    showMem( 0, k-1 );

    // initialize registers:
    bp = k;  sp = k+2;  ip = 0;  rv = -1;  hp = max;
    numPassed = 0;
    
    int codeEnd = bp-1;

    System.out.println("Code is " );
    showMem( 0, codeEnd );

    gp = codeEnd + 1;

    // start execution:
    boolean done = false;
    int op, a=0, b=0, c=0;
    int actualNumArgs;

    int step = 0;

    int oldIp = 0;

    // repeatedly execute a single operation
    // *****************************************************************

    do {

/*    // show details of current step
      System.out.println("--------------------------");
      System.out.println("Step of execution with IP = " + ip + " opcode: " +
          mem[ip] + 
         " bp = " + bp + " sp = " + sp + " hp = " + hp + " rv = " + rv );
      System.out.println(" chunk of code: " +  mem[ip] + " " +
                            mem[ip+1] + " " + mem[ip+2] + " " + mem[ip+3] );
      System.out.println("--------------------------");
      System.out.println( " memory from " + (codeEnd+1) + " up: " );
      showMem( codeEnd+1, sp+3 );
      System.out.println("hit <enter> to go on" );
      keys.nextLine();
*/

      oldIp = ip;

      op = mem[ ip ];  ip++;
      // extract the args into a, b, c for convenience:
      a = -1;  b = -2;  c = -3;

      // numArgs is wrong for these guys, need one more!
      if( op == callCode || op == jumpCode ||
                op == condJumpCode )
      {
        actualNumArgs = numArgs( op ) + 1;
      }
      else
        actualNumArgs = numArgs( op );

      if( actualNumArgs == 1 )
      {  a = mem[ ip ];  ip++;  }
      else if( actualNumArgs == 2 )
      {  a = mem[ ip ];  ip++;  b = mem[ ip ]; ip++; }
      else if( actualNumArgs == 3 )
      {  a = mem[ ip ];  ip++;  b = mem[ ip ]; ip++; c = mem[ ip ]; ip++; }
 
      // implement all operations here:
      // ********************************************

      // put your work right here!
      switch(op) {
        case noopCode: // 0
            // Do nothing
            break;
        case labelCode: // 1
            /* During program loading this instruction disappears, and all occurences of L
            ** are replaced by the actualy index in mem wheree the opcode 1 would have
            ** been stored. 
            */
            // TODO implement 1, label. 
            System.out.println("1, label is not implemented yet.");
            System.exit(1);
            break;
        case callCode: // 2
            /* Do all thee steps necessary to set up for execution of the subprogram that
            ** begins at L. 
            */
            // TODO implement 2, call.
            System.out.println("2, call is not implemented yet.");
            System.exit(1);
            break;
        case passCode: // 3
            // Push the contents of cell a on the stack. 
            // TODO implement 3, pass.
            System.out.println("3, push is not implemented yet.");
            System.exit(1);
            break;
        case allocCode: // 4
            // Increase sp by n to make space for local variables in the current stack frame.
            sp += a;
            break;
        case returnCode: // 5
            /* Do all the steps necessary to return from the current subprogram, including
            ** putting the value stored in cell a in rv. 
            */
            // TODO implement 5, return, then get rid of the exit in 6, get retval.
            System.out.println("5, return is not implemented yet.");
            System.exit(1);
            break;
        case getRetvalCode: // 6
            // Copy the value stored in rv into cell a.
            // Get rid of the next two lines if you implement 5, return.
            System.out.println("We can't use 6, get retval yet, because return isn't implemented.");
            System.exit(1);
            mem[a] = rv;
            break;
        case jumpCode: // 7
            // Change ip to L.
            // TODO implement 7, jump.
            System.out.println("7, jump is not implemented yet.");
            System.exit(1);
            break;
        case condJumpCode: // 8
            /* If the value stored in cell a is non-zero, change ip to L, otherwise
            ** move ip to the next instruction.
            */
            // TODO implement 8, cond.
            System.out.println("8, cond is not implemented yet.");
            System.exit(1);
            break;
        case addCode: // 9
            // Add the values in cell b and cell c and store the result in cell a.
            mem[a] = mem[b] + mem[c];
            break;
        case subCode: // 10
            // Do cell b - cell c and store the result in cell a.
            mem[a] = mem[b] - mem[c];
            break;
        case multCode: // 11
            // Do cell b * cell c and store the result in call a.
            mem[a] = mem[b] * mem[c];
            break;
        case divCode: // 12
            // Do cell b / cell c and store the result in cell a.
            mem[a] = mem[b] / mem[c];
            break;
        case remCode: // 13
            // Do cell b % cell c and store the result in cell a.
            mem[a] = mem[b] % mem[c];
            break;
        case equalCode: // 14
            // Do cell b == cell c and store the result in cell a.
            if (mem[b] == mem[c]) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;    
            }
            break;
        case notEqualCode: // 15
            // Do cell b != cell c and store the result in cell a.
            if (mem[b] != mem[c]) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;    
            }
            break;
        case lessCode: // 16
            // Do cell b < cell c, and store the result in cell a.
            if (mem[b] < mem[c]) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;    
            }
            break;
        case lessEqualCode: // 17
            // Do cell b <= cell c, and store the result in cell a.
            if (mem[a] <= mem[c]) {
                mem[a] = 1;    
            } else {
                mem[a] = 0; 
            }
            break;
        case andCode: // 18
            // Do cell b && cell c and store the result in cell a.
            // TODO I'm assuming any value > 0 is 'True', find out if this is correct, if it is not, fix 18, and and 19, or.
            if (mem[b] > 0 && mem[c] > 0) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;    
            }
            break;
        case orCode: // 19
            // Do cell b || cell c and store the result in cell a.
            if (mem[b] > 0 || mem[c] > 0) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;    
            }
            break;
        case notCode: // 20
            // If cell b == 0, put 1 in cell a, otherwise, put 0 in cell a.
            if (mem[b] == 0) {
                mem[a] = 1;    
            } else {
                mem[a] = 0;
            }
            break;
        case oppCode: // 21
            // Put the opposite of the contents of cell b in cell a.
            mem[ bp+2 + a ] = - mem[ bp+2 + b]; 
            break;
        case litCode: // 22
            // Put n in cell a.
            mem[a] = b;
            break;
        case copyCode: // 23
            // Copy the value in cell b into cell a.
            mem[a] = mem[b];
            break;
        case getCode: // 24
            /* Get the value stored in the heap at the index obtained by adding the value of
            ** cell b and the value of cell c and copy it into cell a.
            */
            // TODO implement 24, get
            System.out.println("24, get is not implemented yet.");
            System.exit(1);
            break;
        case putCode: // 25
            /* Take the value from cell c and store it in the heap at the location with index
            ** computed as the value in cell a plus the value in cell b.
            */
            // TODO implement 25, put
            System.out.println("25, put is not implemented yet.");
            System.exit(1);
            break;
        case haltCode: // 26
            // Halt execution.
            System.exit(0);
        case inputCode: // 27
            // Print a ? and a space in the console and wait for an integer value to be typed 
            // by the user, and then store it in cell a.
            Scanner userIn = new Scanner(System.in);
            System.out.print("? ");
            try {
                mem[a] = userIn.nextInt();
                /* TODO When the user hits ENTER, it will create a new line, even though a new line
                ** command was never passed, find out if this is a problem.
                */
                userIn.close();
            } catch (java.util.InputMismatchException e1) {
               System.out.print("You must input an integer");
               System.exit(1);
            }
            break;
        case outputCode: // 28
            // Display the value stored in call a in the console
            System.out.println(mem[a]);
            break;
        case newlineCode: // 29
            // Move the console cursor to the beginning of the next line
            System.out.println();
            break;
        case symbolCode: // 30
            /* If the value stored in cell a is between 32 and 126, display the corresponding symbol
            ** at the console cursor, otherwise do nothing.
            */
            // TODO implement 30, symbol
            System.out.println("30, symbol is not implemented yet.");
            System.exit(1);
            break;
        case newCode: // 31
            /* Let the value stored in cell b be denoted by m. Decrease hp by m and put the new value
            ** of hp in cell a
            */
            // TODO implement 31, new
            System.out.println("31, new is not implemented yet.");
            System.exit(1);
            break;
        case allocGlobalCode: // 32
            /* This instruction must occur first in any program that uses it. It simply sets the initial
            ** value of sp to n cells beyond the end of stored program memory, and sets gp to the end of
            ** stored program memory.
            */
            // TODO implement 32, allocate global space
            System.out.println("32, allocate global space is not implemented yet.");
            System.exit(1);
            break;
        case toGlobalCode: // 33
            // Copy the contents of cell a to the global memory area at index gp+n.
            // TODO implement 33, Copy to global
            System.out.println("33, Copy to global is not implemented yet.");
            System.exit(1);
            break;
        case fromGlobalCode: // 34
            // Copy the contents of the global memory cell at index gp+n into cell a.
            // TODO implement 34, Copy from global
            System.out.println("34, Copy from global is not implementeed yet.");
            System.exit(1);
            break;
        default: 
            System.out.println( "Fatal error: unknown opcode [" + op + "]" );
            System.exit(1);
      }

      /*
      if ( op == oppCode ) {
         mem[ bp+2 + a ] = - mem[ bp+2 + b ];
      }


      else
      {
        System.out.println("Fatal error: unknown opcode [" + op + "]" );
        System.exit(1);
      }
      */
       
      step++;

    }while( !done );
    

  }// main

  // use symbolic names for all opcodes:

  // op to produce comment
  private static final int noopCode = 0;

  // ops involved with registers
  private static final int labelCode = 1;
  private static final int callCode = 2;
  private static final int passCode = 3;
  private static final int allocCode = 4;
  private static final int returnCode = 5;  // return a means "return and put
           // copy of value stored in cell a in register rv
  private static final int getRetvalCode = 6;//op a means "copy rv into cell a"
  private static final int jumpCode = 7;
  private static final int condJumpCode = 8;

  // arithmetic ops
  private static final int addCode = 9;
  private static final int subCode = 10;
  private static final int multCode = 11;
  private static final int divCode = 12;
  private static final int remCode = 13;
  private static final int equalCode = 14;
  private static final int notEqualCode = 15;
  private static final int lessCode = 16;
  private static final int lessEqualCode = 17;
  private static final int andCode = 18;
  private static final int orCode = 19;
  private static final int notCode = 20;
  private static final int oppCode = 21;
  
  // ops involving transfer of data
  private static final int litCode = 22;  // litCode a b means "cell a gets b"
  private static final int copyCode = 23;// copy a b means "cell a gets cell b"
  private static final int getCode = 24; // op a b means "cell a gets
                                                // contents of cell whose 
                                                // index is stored in b"
  private static final int putCode = 25;  // op a b means "put contents
     // of cell b in cell whose offset is stored in cell a"

  // system-level ops:
  private static final int haltCode = 26;
  private static final int inputCode = 27;
  private static final int outputCode = 28;
  private static final int newlineCode = 29;
  private static final int symbolCode = 30;
  private static final int newCode = 31;
  
  // global variable ops:
  private static final int allocGlobalCode = 32;
  private static final int toGlobalCode = 33;
  private static final int fromGlobalCode = 34;

  // debug ops:
  private static final int debugCode = 35;

  // return the number of arguments after the opcode,
  // except ops that have a label return number of arguments
  // after the label, which always comes immediately after 
  // the opcode
  private static int numArgs( int opcode )
  {
    // highlight specially behaving operations
    if( opcode == labelCode ) return 1;  // not used
    else if( opcode == jumpCode ) return 0;  // jump label
    else if( opcode == condJumpCode ) return 1;  // condJump label expr
    else if( opcode == callCode ) return 0;  // call label

    // for all other ops, lump by count:

    else if( opcode==noopCode ||
             opcode==haltCode ||
             opcode==newlineCode ||
             opcode==debugCode
           ) 
      return 0;  // op

    else if( opcode==passCode || opcode==allocCode || 
             opcode==returnCode || opcode==getRetvalCode || 
             opcode==inputCode || 
             opcode==outputCode || opcode==symbolCode ||
             opcode==allocGlobalCode
           )  
      return 1;  // op arg1

    else if( opcode==notCode || opcode==oppCode || 
             opcode==litCode || opcode==copyCode || opcode==newCode ||
             opcode==toGlobalCode || opcode==fromGlobalCode

           ) 
      return 2;  // op arg1 arg2

    else if( opcode==addCode ||  opcode==subCode || opcode==multCode ||
             opcode==divCode ||  opcode==remCode || opcode==equalCode ||
             opcode==notEqualCode ||  opcode==lessCode || 
             opcode==lessEqualCode || opcode==andCode ||
             opcode==orCode || opcode==getCode || opcode==putCode
           )
      return 3;
   
    else
    {
      System.out.println("Fatal error: unknown opcode [" + opcode + "]" );
      System.exit(1);
      return -1;
    }

  }// numArgs

  private static void showMem( int a, int b )
  {
    for( int k=a; k<=b; ++k )
    {
      System.out.println( k + ": " + mem[k] );
    }
  }// showMem

}// VPL
