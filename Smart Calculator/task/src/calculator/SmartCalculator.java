package calculator;

import java.math.BigInteger;
import java.util.*;

public class SmartCalculator {

    public static final Map<String, String> variable = new HashMap<>();
    public static final Deque<String> stack = new ArrayDeque<>();
    public static Deque<String> stacky1 = new ArrayDeque<>();
    public static Deque<String> postyfix = new ArrayDeque<>();
    public static Deque<String> sum = new ArrayDeque<>();
    public static StringBuilder sb = new StringBuilder();
    public static String result;

    public void start() {
        Scanner input = new Scanner(System.in);
        String num;
        String numFix;


        while (true) {                                                          // Needs to loop until user wants it to end
            num = input.nextLine();                                             // Take input
            numFix = num.replaceAll("\\++", "+")                // Correct input in case it's not given in proper format
                    .replaceAll("\\s+-{2}\\s+", " + ")
                    .replaceAll("\\s+-{4}\\s+", " + ")
                    .replaceAll("-+", "-")
                    .replaceAll("\\(", "( ")
                    .replaceAll("\\)", " )")
                    .trim();
            if (variable.containsKey(numFix)) {                                 // Check if input is in hashmap
                System.out.println(variable.get(numFix));                       // Display value if successful
                continue;
            }

            if (numFix.matches("^.*\\*{2,}.*$") || numFix.matches("^.*\\/{2,}.*$")) {
                System.out.println("Invalid expression");
                continue;
            }
            if (numFix.contains("(") && !numFix.contains(")") ||
                    numFix.contains(")") && !numFix.contains("(")) {
                System.out.println("Invalid expression");
                continue;
            }

            if (!numFix.equals("/exit") && !numFix.equals("/help") && numFix.startsWith("/")) {         // Verify command
                System.out.println("Unknown command");
            } else {
                switch (numFix) {
                    case "/exit":                   // Ends program
                        System.out.println("Bye!");
                        System.exit(0);
                        break;
                    case "/help":                   // Displays info about the project
                        System.out.println("The program calculates the addition, subtraction, multiplication and division of numbers.");
                        break;
                    case "":                        // continue the loop if nothing is given
                        break;
                    default:
                        try {
                            if (!numFix.contains(" ") && numFix.contains("+") ||
                                    !numFix.contains(" ") && numFix.contains("-") ||
                                    !numFix.contains(" ") && numFix.contains("/") ||
                                    !numFix.contains(" ") && numFix.contains("*")) {                                      // Check if the text contains any spaces
                                convertTextWithNoSpaces(numFix);                                // Convert text with no spaces to text with space
                                numFix = result;
                            }
                            String[] variables = numFix.split("\\s*=\\s*");

                            if (numFix.contains("=") && numFix.matches("[a-zA-Z]*\\s*=\\s*-?\\w*\\s*")          // Assign a variable using a variable
                                    && variable.containsKey(variables[1].trim())) {
                                assignVariable(variables[0], variable.get(variables[1].trim()));
                            } else if (numFix.contains("=") && numFix.matches("[a-zA-Z]*\\s*=\\s*-?\\d+\\s*")) {    //Assign a variable using numbers
                                try {
                                    assignVariable(variables[0], variables[1]);
                                } catch (NumberFormatException e) {
//                                    System.out.println(variables[1]);
                                }
                            } else if (numFix.contains("(") || numFix.contains(")")) {            // if parenthesis are used use postfix
                                convertToPostFix(numFix);
                                calculateBigIntPostFix();
//                                calculatePostFix();
                            } else if (numFix.contains("=") && num.matches("[a-zA-Z]\\s*=\\s*\\D*\\d*\\D*\\s*")) {
                                System.out.println("Invalid assignment");

                            } else if (numFix.contains("=") && !num.matches("[a-zA-Z]\\s*=\\s*\\d+\\s*")) {
                                System.out.println("Invalid identifier");
                            } else if (numFix.matches("[a-zA-Z]*") && variable.get(num) == null) {
                                System.out.println("Unknown variable");
                            } else if (numFix.matches("\\*{2,}")) {
                                System.out.println("Invalid expression");
                            } else {
//                                calculate(numFix);
                                convertToPostFix(numFix);
                                calculateBigIntPostFix();
                                postyfix.clear();
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid expression");
                        }
                }
            }
        } // End of while loop

    }


    public static void convertTextWithNoSpaces(String numFix) {
        char[] noSpaceUser = numFix.toCharArray();
        String userInput = "";
        String fixedInput = "";
        for (int i = 0; i < noSpaceUser.length; i++) {
            userInput = String.valueOf(noSpaceUser[i]);     // single value
            if (userInput.matches("[a-zA-Z]")) {       // making sure it's a letter
                fixedInput = fixedInput.concat(userInput);      // building up the word
            } else if (userInput.matches("[-+*/()^]")) {       // making sure it's an operator
                sb.append(fixedInput);                      // add final value of word to the string builder
                fixedInput = "";                            // clear the value of fixed input so it can be used again
                sb.append(" ");     // adding in a space to insure its read properly
                sb.append(noSpaceUser[i]);          // Add in the operator to the String builder;
                sb.append(" ");
            } else if (userInput.matches("\\d")) {      // Making sure it's a number
                fixedInput = fixedInput.concat(userInput);     // Building up the number
            }

        }
        sb.deleteCharAt(sb.length() - 1);               // Must delete additional space created in for loop at the last value
        result = sb.toString();
        System.out.println(result);

    }


    public static void convertToPostFix(String numFix) {
        String[] numbers = numFix.split("\\s+");               // Take in the input from the user and store it in a string array
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i].matches("[a-zA-Z]*")) {               // If current number matches a letter
                postyfix.offer(String.valueOf(variable.get(numbers[i])));       // Since we are using letters we confirmed its a variable and grab it's value
            } else if (numbers[i].matches("\\d+")) {              // If value matches a number
                postyfix.offerLast(numbers[i]);                        // Add the number to the the postfix
            } else if (numbers[i].matches("[-+*/()^]")) {         // If number matches an operator
                if (stacky1.isEmpty() || stacky1.peekLast().equals("(")) {
                    stacky1.offer(numbers[i]);
                }   // '(' section
                else if (numbers[i].equals("(")) {
                    stacky1.offerLast(numbers[i]);
                }
                // ')' section
                else if (numbers[i].equals(")")) {
                    if (!stacky1.peekLast().equals("(")) {
                        postyfix.offerLast(stacky1.pollLast());
                    }
                    if (stacky1.peekLast().equals("(")) {
                        stacky1.pollLast();
                    }
                    // if the incoming operator has higher precedence than the top of the stack

                } else if (stacky1.peekLast().equals("+") && numbers[i].equals("*") || stacky1.peekLast().equals("+") && numbers[i].equals("/")
                        || stacky1.peekLast().equals("+") && numbers[i].equals("^")) {
                    stacky1.offerLast(numbers[i]);
                } else if (stacky1.peekLast().equals("-") && numbers[i].equals("*") || stacky1.peekLast().equals("-") && numbers[i].equals("/")
                        || stacky1.peekLast().equals("-") && numbers[i].equals("^")) {
                    stacky1.offerLast(numbers[i]);
                } else if (stacky1.peekLast().equals("*") && numbers[i].equals("^")) {
                    stacky1.offerLast(numbers[i]);
                } else if (stacky1.peekLast().equals("/") && numbers[i].equals("^")) {
                    stacky1.offerLast(numbers[i]);
                }

                // incoming operator has lower or equal precedence than the top of the operator stack
                // Pop the stack and add the operators to the result until you see an op that has a smaller precedence or a left parenthesis on top
                // Add everything popped to the postfix
                //^ section
                // Equal precedence
                else if (stacky1.peekLast().equals("^") && numbers[i].equals("^")) {
                    while (stacky1.peekLast().contains("^") || stacky1.peekLast().contains("(")) {
                        postyfix.offerLast(stacky1.pollLast());
                        stacky1.offerLast(numbers[i]);
                    }
                    // Lower precedence
                } else if (stacky1.peekLast().equals("^") && numbers[i].equals("*") || stacky1.peekLast().equals("^") && numbers[i].equals("/") ||
                        stacky1.peekLast().equals("^") && numbers[i].equals("+") || stacky1.peekLast().equals("^") && numbers[i].equals("-")) {
                    postyfix.offerLast(stacky1.pollLast());
                    stacky1.offerLast(numbers[i]);
                }
                // * section
                // Equal precedence

//                else if (stacky1.peekLast().equals("*") && numbers[i].equals("*") || stacky1.peekLast().equals("*") && numbers[i].equals("/")) {
//                    while (!stacky1.isEmpty() && stacky1.peekLast().equals("*") || !stacky1.isEmpty() && stacky1.peekLast().equals("/") ||
//                            !stacky1.peekLast().equals("(")) {
//                        postyfix.offerLast(stacky1.pollLast());
//                    }
//                        stacky1.offerLast(numbers[i]);
                else if (stacky1.peekLast().equals("*") && numbers[i].equals("*") || stacky1.peekLast().equals("*") && numbers[i].equals("/")) {
                    do {
                        postyfix.offerLast(stacky1.pollLast());
                    }
                    while (!stacky1.isEmpty());
                    stacky1.offerLast(numbers[i]);


                    // Lower precedence
                } else if (stacky1.peekLast().equals("*") && numbers[i].equals("+") || stacky1.peekLast().equals("*") && numbers[i].equals("-")) {
                    while (!stacky1.isEmpty() && !stacky1.peekLast().equals("(")) {
                        postyfix.offerLast(stacky1.pollLast());
                    }
                    stacky1.offerLast(numbers[i]);
                }
                // '/' section
                // Equal precedence
                else if (stacky1.peekLast().equals("/") && numbers[i].equals("/") || stacky1.peekLast().equals("/") && numbers[i].equals("*")) {
                    while (stacky1.peekLast().equals("/") || stacky1.peekLast().equals("*") || !stacky1.peekLast().equals("(")) {
                        postyfix.offerLast(stacky1.pollLast());
                        stacky1.offerLast(numbers[i]);
                    }
                    // Lower precedence
                } else if (stacky1.peekLast().equals("/") && numbers[i].equals("+") || stacky1.peekLast().equals("/") && numbers[i].equals("-")) {
                    while (!stacky1.isEmpty() && !stacky1.peekLast().equals("(")) {
                        postyfix.offerLast(stacky1.pollLast());
                    }
                    stacky1.offerLast(numbers[i]);
                }
                // + section
                // Equal precedence
                else if (stacky1.peekLast().equals("+") && numbers[i].contains("+") || stacky1.peekLast().equals("+") && numbers[i].contains("-")) {
//                    stacky1.pop();
                    postyfix.addLast(stacky1.pollLast());
                    stacky1.offerLast(numbers[i]);
                }
                // - section
                // Equal precedence
                else if (stacky1.peekLast().equals("-") && numbers[i].contains("+") || stacky1.peekLast().equals("-") && numbers[i].contains("-")) {
                    postyfix.offerLast(stacky1.pollLast());
                    stacky1.offerLast(numbers[i]);
                }
            }
        }
        while (!stacky1.isEmpty()) {
            postyfix.offer(stacky1.pollLast());
        }
    }

    public static void calculatePostFix() {                         // Method to be used when dealing with parenthesis
        Iterator<String> it = postyfix.iterator();                  // Iterator to go through the postfix

        int temp1;
        int temp2;
        String tempy = it.next();
        while (it.hasNext()) {                                      // Keeps checking while theres still unchecked values in the deque
            if (tempy.matches("\\d+")) {                        // if the value is a number
                sum.addLast(tempy);
                tempy = it.next();
            } else if (tempy.matches("[a-zA-Z]")) {             // if the value is a letter
                sum.addLast(String.valueOf(variable.get(it.next())));
            } else if (tempy.matches("\\+")) {                  // If the value is addition
                temp1 = Integer.parseInt(sum.pollLast());             // Pop the last two
                temp2 = Integer.parseInt(sum.pollLast());
                tempy = it.next();                                    // Continue the loop to the next value
                sum.addLast(String.valueOf(addition(temp1, temp2)));
            } else if (tempy.matches("\\-")) {                  // If the value is subtraction
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                tempy = it.next();
                sum.addLast(String.valueOf(subtraction(temp1, temp2)));
            } else if (tempy.matches("\\*")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                tempy = it.next();
                sum.addLast(String.valueOf(multiplication(temp1, temp2)));
            } else if (tempy.matches("\\/")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                tempy = it.next();
                sum.addLast(String.valueOf(division(temp1, temp2)));
            } else if (tempy.matches("\\^")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                tempy = it.next();
                sum.addLast(String.valueOf(power(temp1, temp2)));
            }
        }

        if (sum.size() > 1) {                                           // Created to deal with issue of last value
            if (tempy.matches("\\+")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                sum.addLast(String.valueOf(addition(temp1, temp2)));
            } else if (tempy.matches("\\-")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                sum.addLast(String.valueOf(subtraction(temp1, temp2)));
            } else if (tempy.matches("\\*")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                sum.addLast(String.valueOf(multiplication(temp1, temp2)));
            } else if (tempy.matches("\\/")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                sum.addLast(String.valueOf(division(temp1, temp2)));
            } else if (tempy.matches("\\^")) {
                temp1 = Integer.parseInt(sum.pollLast());
                temp2 = Integer.parseInt(sum.pollLast());
                sum.addLast(String.valueOf(power(temp1, temp2)));
            }
        }
        System.out.println(sum.pop());                          // The last value in sum is your answer
    }

    public static void calculateBigIntPostFix() {                         // Method to be used when dealing with parenthesis
        Iterator<String> it = postyfix.iterator();                  // Iterator to go through the postfix

        int temp1;
        int temp2;
        String temp0;
        String temp9;

        String tempy = it.next();
        while (it.hasNext()) {                                      // Keeps checking while theres still unchecked values in the deque
            if (tempy.matches("\\d+")) {                        // if the value is a number
                sum.addLast(tempy);
                tempy = it.next();
            } else if (tempy.matches("[a-zA-Z]")) {             // if the value is a letter
                sum.addLast(String.valueOf(variable.get(it.next())));
            } else if (tempy.matches("\\+")) {                  // If the value is addition
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                tempy = it.next();                                    // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi1.add(bi2);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\-")) {                  // If the value is subtraction
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                tempy = it.next();                                    // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.subtract(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\*")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                tempy = it.next();                                    // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi1.multiply(bi2);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\/")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                tempy = it.next();                                    // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.divide(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\^")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                tempy = it.next();                                    // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi1.add(bi2);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            }
        }

        if (sum.size() > 1) {                                           // Created to deal with issue of last value
            if (tempy.matches("\\+")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                if (it.hasNext()) {
                    tempy = it.next();    }                                // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi1.add(bi2);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\-")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                if (it.hasNext()) {
                    tempy = it.next(); }                                   // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.subtract(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\*")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                if (it.hasNext()) {
                    tempy = it.next();  }                                 // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.multiply(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\/")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                if (it.hasNext()) {
                    tempy = it.next();  }                               // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.divide(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            } else if (tempy.matches("\\^")) {
                temp0 = sum.pollLast();
                temp9 = sum.pollLast();
                if (it.hasNext()) {
                    tempy = it.next();     }                               // Continue the loop to the next value
                BigInteger bi1 = new BigInteger(temp0);
                BigInteger bi2 = new BigInteger(temp9);
                BigInteger sum2;
                sum2 = bi2.add(bi1);
                String newSum = sum2.toString();
                sum.addLast(newSum);
            }
        }
        System.out.println(sum.pop());                          // The last value in sum is your answer
    }


    public static void calculate(String numFix) {
        String[] numbers = numFix.split("\\s+");                    // Split the numbers by spaces
        int result = 0;                                                   // Created result to store answer
        if (numbers[0].matches("\\d+")) {                           // Check if the first value is a number
            result = Integer.parseInt(numbers[0]);
        } else if (numbers[0].matches("[a-zA-Z]*")) {               // Check if the first value is a letter
            numbers[0] = String.valueOf(variable.get(numbers[0]));        // Since it's a letter it must be a variable on the hashmap so store that value in index 0
            result = Integer.parseInt(numbers[0]);                        // Store index zero into the result
        }
        for (int i = 1; i < numbers.length; i += 2) {                     // Create a for loop that starts at 1 since the result is stored in 0. Incremented by 2 for the operators to be found
            int temp;
            try {
                // If the value is a minus
                if (numbers[i].contains("-") && numbers[i].length() % 2 != 0) {
                    if (variable.containsKey(numbers[i - 1]) && variable.containsKey(numbers[i + 1])) {
                        result = subtraction(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else if (variable.containsKey(numbers[i + 1])) {
                        result = subtraction(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else {
                        temp = Integer.parseInt(numbers[i + 1]);
                        result = subtraction(temp, result);
                    }
                    // If the value is a plus
                } else if (numbers[i].contains("+") && numbers[i].length() % 2 != 0) {
                    if (variable.containsKey(numbers[i - 1]) && variable.containsKey(numbers[i + 1])) {
                        result = addition(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else if (variable.containsKey(numbers[i + 1])) {
                        result = addition(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else {
                        temp = Integer.parseInt(numbers[i + 1]);
                        result = addition(temp, result);
                    }
                    // If the value is multiplication
                } else if (numbers[i].contains("*") && numbers[i].length() % 2 != 0) {
                    if (variable.containsKey(numbers[i - 1]) && variable.containsKey(numbers[i + 1])) {
                        result = multiplication(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else if (variable.containsKey(numbers[i + 1])) {
                        result = multiplication(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else {
                        temp = Integer.parseInt(numbers[i + 1]);
                        result = multiplication(temp, result);
                    }
                    // If the value is division
                } else if (numbers[i].contains("/") && numbers[i].length() % 2 != 0) {
                    if (variable.containsKey(numbers[i - 1]) && variable.containsKey(numbers[i + 1])) {
                        result = division(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else if (variable.containsKey(numbers[i + 1])) {
                        result = division(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else {
                        temp = Integer.parseInt(numbers[i + 1]);
                        result = division(temp, result);
                    }
                    // If the value is a power
                } else if (numbers[i].contains("^") && numbers[i].length() % 2 != 0) {
                    if (variable.containsKey(numbers[i - 1]) && variable.containsKey(numbers[i + 1])) {
                        result = power(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else if (variable.containsKey(numbers[i + 1])) {
                        result = power(Integer.parseInt(variable.get(numbers[i + 1])), result);
                    } else {
                        temp = Integer.parseInt(numbers[i + 1]);
                        result = power(temp, result);
                    }
                }
            } catch (Exception e) {
                System.out.println("Errors found");
            }
        }
        System.out.println(result);
    }

    public static int addition(int num, int total) {
        return total + num;
    }

    public static int subtraction(int num, int total) {
        return total - num;
    }

    public static int multiplication(int num, int total) {
        return total * num;
    }

    public static int division(int num, int total) {
        return total / num;
    }

    public static int power(int num, int total) {
        return (int) Math.pow(total, num);
    }

    public static void assignVariable(String input, String value) {
        variable.put(input, value);
    }
}
