import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Table-driven Lexer
public class Lexer {

    //Variable Declaration
    int count;
    String line;
    int transitionCharacter;
    Stack<Object> stack = new Stack<>();
    ArrayList<String> lexemesToRollbackTo = new ArrayList<>();
    ArrayList<Integer> StatesToRollbackTo = new ArrayList<>();
    HashMap<Integer, String> finalStates = new HashMap<>();
    HashMap<Character, Integer> classifierTable = new HashMap<>();
    int[][] transitionTable = new int[83][35];
    ArrayList<String> words = new ArrayList<>();
    int numberOfWords;
    int lineCounter = 1;

    Lexer(String path) throws IOException {
        BufferedReader f = new BufferedReader(new FileReader(new File(path)));
        finalStates.put(0, "<Identifier>");
        finalStates.put(5, "<Type>");             //float
        finalStates.put(8, "<Type>");             //int
        finalStates.put(12, "<Type>");            //bool
        finalStates.put(16, "<Auto>");            //auto
        finalStates.put(20, "<BooleanLiteral>");  //true
        finalStates.put(24, "<BooleanLiteral>");  //false
        finalStates.put(25, "<MultiplicativeOp>");//*
        finalStates.put(26, "<MultiplicativeOp>");///
        finalStates.put(28, "<MultiplicativeOp>");//and
        finalStates.put(29, "<AdditiveOp>");      //+
        finalStates.put(30, "<AdditiveOp>");      //-
        finalStates.put(32, "<AdditiveOp>");      //or
        finalStates.put(33, "<RelationalOp>");    //<
        finalStates.put(34, "<RelationalOp>");    //>
        finalStates.put(35, "=");                 //=
        finalStates.put(36, "<RelationalOp>");    //==
        finalStates.put(37, "<RelationalOp>");    //<>
        finalStates.put(38, "<RelationalOp>");     //<=
        finalStates.put(39, "<RelationalOp>");     //>=
        finalStates.put(42, "<Unary>");            //not
        finalStates.put(45, "let");
        finalStates.put(50, "print");
        finalStates.put(56, "return");
        finalStates.put(57, "if");
        finalStates.put(59, "for");
        finalStates.put(64, "while");
        finalStates.put(65, ":");
        finalStates.put(66, ",");
        finalStates.put(67, "ff");
        finalStates.put(68, ";");
        //finalStates.put(71, "<Block>");            //{{<Statement>}} (Not needed)
        finalStates.put(75, "else");
        finalStates.put(76, "<IntegerLiteral>");
        finalStates.put(78, "<FloatLiteral>");
        finalStates.put(79, "(");
        finalStates.put(80, ")");
        finalStates.put(81, "{");
        finalStates.put(82, "}");

        classifierTable.put('*', 1);
        classifierTable.put('+', 2);
        classifierTable.put('-', 3);
        classifierTable.put('/', 4);
        classifierTable.put(':', 5);
        classifierTable.put('<', 6);
        classifierTable.put('=', 7);
        classifierTable.put('>', 8);
        classifierTable.put('a', 9);
        classifierTable.put('b', 10);
        classifierTable.put('e', 11);
        classifierTable.put('f', 12);
        classifierTable.put('h', 13);
        classifierTable.put('i', 14);
        classifierTable.put('j', 15);
        classifierTable.put('l', 16);
        classifierTable.put('n', 17);
        classifierTable.put('o', 18);
        classifierTable.put('p', 19);
        classifierTable.put('r', 20);
        classifierTable.put('s', 21);
        classifierTable.put('t', 22);
        classifierTable.put('u', 23);
        classifierTable.put('w', 24);
        classifierTable.put('y', 25);
        classifierTable.put('{', 26);
        classifierTable.put('}', 27);
        classifierTable.put(';', 28);

        classifierTable.put('d', 29);
        classifierTable.put(',', 30);
        for (int i = 48; i < 58; i++) {
            classifierTable.put((char) (i), 31);
        }
        classifierTable.put('.', 32);
        classifierTable.put('(', 33);
        classifierTable.put(')', 34);


        // Handling the '*' operator
        transitionTable[0][1] = 25;

        // Handling the '+' operator
        transitionTable[0][2] = 29;

        // Handling the ',' operator
        transitionTable[0][30] = 66;

        // Handling the '-' operator
        transitionTable[0][3] = 30;

        // Handling the '/' operator
        transitionTable[0][4] = 26;

        // Handling the [0-9] characters
        transitionTable[0][31] = 76;
        transitionTable[76][31] = 76;
        transitionTable[76][32] = 77;
        transitionTable[77][31] = 78;
        transitionTable[78][31] = 78;

        // Handling the ':' operator
        transitionTable[0][5] = 65;

        // Handling the '=' operator
        transitionTable[0][7] = 35;

        // Handling the "==" operator
        transitionTable[35][7] = 36;

        // Handling the ';' operator
        for (int i = 0; i < 74; i++) {
            transitionTable[i][28] = 68;
        }

        // Handling the '<' operator
        transitionTable[0][6] = 33;

        // Handling the "<=" operator
        transitionTable[33][7] = 38;

        // Handling the "<>" operator
        transitionTable[33][8] = 37;

        // Handling the '>' operator
        transitionTable[0][8] = 34;

        // Handling the ">=" operator
        transitionTable[34][7] = 39;

        // Handling the '(' token
        transitionTable[0][33] = 79;

        // Handling the ')' token
        transitionTable[0][34] = 80;

        // Handling and
        transitionTable[0][9] = 13;
        transitionTable[13][17] = 27;
        transitionTable[27][29] = 28;

        // Handling auto
        transitionTable[65][9] = 13;
        transitionTable[13][23] = 14;
        transitionTable[14][22] = 15;
        transitionTable[15][18] = 16;

        // Handling bool
        transitionTable[0][10] = 9;
        transitionTable[65][10] = 9;
        transitionTable[9][18] = 10;
        transitionTable[10][18] = 11;
        transitionTable[11][16] = 12;

        // Handling else
        transitionTable[0][11] = 72;
        transitionTable[72][16] = 73;
        transitionTable[73][21] = 74;
        transitionTable[74][11] = 75;

        // Handling false
        transitionTable[0][12] = 1;
        transitionTable[1][9] = 21;
        transitionTable[21][16] = 22;
        transitionTable[22][21] = 23;
        transitionTable[23][11] = 24;

        // Handling ff
        transitionTable[1][12] = 67;

        // Handling float
        transitionTable[65][12] = 1;
        transitionTable[1][16] = 2;
        transitionTable[2][18] = 3;
        transitionTable[3][9] = 4;
        transitionTable[4][22] = 5;

        // Handling for
        transitionTable[1][18] = 58;
        transitionTable[58][20] = 59;

        // Handling if
        transitionTable[6][12] = 57;

        // Handling int
        transitionTable[0][14] = 6;
        transitionTable[65][14] = 6;
        transitionTable[6][17] = 7;
        transitionTable[7][22] = 8;

        // Handling let
        transitionTable[0][16] = 43;
        transitionTable[43][11] = 44;
        transitionTable[44][22] = 45;

        // Handling not
        transitionTable[0][17] = 40;
        transitionTable[40][18] = 41;
        transitionTable[41][22] = 42;

        // Handling or
        transitionTable[0][18] = 31;
        transitionTable[31][20] = 32;

        // Handling print
        transitionTable[0][19] = 46;
        transitionTable[46][20] = 47;
        transitionTable[47][14] = 48;
        transitionTable[48][17] = 49;
        transitionTable[49][22] = 50;

        // Handling return
        transitionTable[0][20] = 51;
        transitionTable[51][11] = 52;
        transitionTable[52][22] = 53;
        transitionTable[53][23] = 54;
        transitionTable[54][20] = 55;
        transitionTable[55][17] = 56;

        // Handling true
        transitionTable[0][22] = 17;
        transitionTable[17][20] = 18;
        transitionTable[18][23] = 19;
        transitionTable[19][11] = 20;

        // Handling while
        transitionTable[0][24] = 60;
        transitionTable[60][13] = 61;
        transitionTable[61][14] = 62;
        transitionTable[62][16] = 63;
        transitionTable[63][11] = 64;

        // Handling the '{' token
        transitionTable[0][26] = 81;

        // Handling the '}' token;
        transitionTable[0][27] = 82;

        //Get the individual words
        List<Character> specialCharacter = Arrays.asList(';', '(', ':', ',', ')', '+', '-', '{', '}', '=', '<', '>');
        List<Character> operators = Arrays.asList('=', '<', '>');
        ArrayList<Integer> addSpace = new ArrayList<>();
        while((line = f.readLine()) != null){
            line = line.replace("\t", "");
            StringBuilder myLine = new StringBuilder(line);
            for(int i = 0; i < line.length(); i++){
                 if(specialCharacter.contains(line.charAt(i))){
                    if(operators.contains(line.charAt(i))){
                        if(!(i+1 <= line.length() && operators.contains(line.charAt(i+1)))){
                            addSpace.add(i);
                        } else {
                            i++;
                        }
                    } else {
                        addSpace.add(i);
                    }

                }
            }
            int count = 0;
            for (Integer i : addSpace) {
                myLine.insert(i + count, " ");
                count++;
                myLine.insert(i + count + 1, " ");
                count++;
            }
            addSpace.clear();

            words.addAll(Arrays.asList(myLine.toString().split(" ")));
            //Add new line token
            words.add("<NL>");
        }

        words.removeAll(Collections.singletonList(""));
        numberOfWords = words.size();

        StatesToRollbackTo.add(0);
    }

    Object[] getNextToken() {
        if(Main.nextToken != null){
            Object[] token = Main.nextToken;
            Main.nextToken = null;
            return token;
        }

        if(count >= numberOfWords){
            // Return end of file token
            return new String[]{"<EOF>", "<EOF>"};
        }

        String word = words.get(count);

        //Handling comments
        while(word.equals("//") || word.startsWith("//") || word.equals("/*") || word.startsWith("/*") || word.equals("<NL>")) {
            if (word.equals("//") || word.startsWith("//")) {
                while (true) {
                    count++;
                    word = words.get(count);
                    if (word.equals("<NL>")) {
                        count++;
                        lineCounter++;
                        try {
                            word = words.get(count);
                        } catch (IndexOutOfBoundsException e){
                            return new String[]{"<EOF>", "EOF"};
                        }
                        break;
                    }
                }
            }

            if (word.equals("/*") || word.startsWith("/*")) {
                while (true) {
                    count++;
                    word = words.get(count);
                    if(word.equals("<NL>")){
                        lineCounter++;
                    }
                    if (word.equals("*/") || word.endsWith("*/")) {
                        count++;
                        word = words.get(count);
                        break;
                    }
                }
            }

            while(word.equals("<NL>")){
                lineCounter++;
                count ++;
                if(count >= numberOfWords){
                    return new String[]{"<EOF>", "EOF"};
                }
                word = words.get(count);

            }
        }

        boolean first = true;
        int state = 0;
        StringBuilder lexeme = new StringBuilder();
        stack.clear();
        stack.push(-2);
        lexemesToRollbackTo.add(String.valueOf(word.charAt(0)));

        // Determine word
        int j = 0;
        while ((state != 0 || first) && j < word.length()) {
            char character = word.charAt(j);
            lexeme.append(character);
            if (finalStates.containsKey(state) && !first) {
                StatesToRollbackTo.add((Integer) stack.pop());
                lexemesToRollbackTo.add((lexeme).toString());
                stack.clear();
            }
            stack.push(state);
            if (classifierTable.get(character) != null) {
                transitionCharacter = classifierTable.get(character);
            } else {
                transitionCharacter = 0;
            }
            state = transitionTable[state][transitionCharacter];
            j++;
            first = false;

        }

        // Rollback section
        while (!finalStates.containsKey(state) && state != -2) {
            lexeme.setLength(0);
            state = StatesToRollbackTo.get(StatesToRollbackTo.size() - 1);
            // Truncate lexeme
            lexeme.append(lexemesToRollbackTo.get(lexemesToRollbackTo.size() - 1));
        }

        // Store result
        if (finalStates.containsKey(state)) {
            count++;
            return new String[]{finalStates.get(state), word};
        } else {
            System.out.println("Error: The lexer could not successfully read the input");
            System.exit(-99);
            return null;
        }
    }
}