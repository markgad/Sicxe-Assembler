import java.io.*;
import java.util.*;



public class Pass1 {
    static Map<String, Integer> instructionFormats = new HashMap<>();
    static Map<String, Integer> symbolTable = new LinkedHashMap<>();
    static String baseSymbol = "";  // For BASE directive
    static List<String> linesWithLoc = new ArrayList<>();

    public static void main(String[] args) {
        try {
            initInstructionFormats();

            File inputFile = new File("intermediate.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            int locCounter = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split line into tokens
                String[] tokens = line.trim().split("\\s+");
                String label = "", operation = "", operand = "";

                if (tokens.length == 3) {
                    label = tokens[0];
                    operation = tokens[1];
                    operand = tokens[2];
                } else if (tokens.length == 2) {
                    if (isOperation(tokens[0]) || tokens[0].startsWith("+")) {
                        operation = tokens[0];
                        operand = tokens[1];
                    } else {
                        label = tokens[0];
                        operation = tokens[1];
                    }
                } else if (tokens.length == 1) {
                    operation = tokens[0];
                }

                if (operation.equalsIgnoreCase("START")) {
                    locCounter = Integer.parseInt(operand);
                    linesWithLoc.add(String.format("%04X\t%-10s%-10s%s", locCounter, label, operation, operand).stripTrailing());
                    continue;
                }

                // Handle BASE directive
                if (operation.equalsIgnoreCase("BASE")) {
                    baseSymbol = operand;
                    linesWithLoc.add(String.format("%04X\t%-10s%-10s%s", locCounter, label, operation, operand).stripTrailing());
                    continue;
                }

                // Validate and record label
                if (!label.isEmpty()) {
                    if (!label.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                        System.err.println("Invalid label: " + label);
                        System.exit(1);
                    }
                    if (symbolTable.containsKey(label)) {
                        System.err.println("Duplicate label: " + label);
                        System.exit(1);
                    }
                    symbolTable.put(label, locCounter);
                }

                int increment = getInstructionSize(operation, operand);
                linesWithLoc.add(String.format("%04X\t%-10s%-10s%s", locCounter, label, operation, operand).stripTrailing());
                locCounter += increment;
            }

            reader.close();
            generateLocationCounter();
            generateSymbolTable();

            System.out.println("Pass 1 complete. Symbol table and location counter generated.");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static boolean isOperation(String token) {
        if (token == null || token.isEmpty()) return false;

        // Handle + and & prefix for format 4 and format 3F
        String normalized = (token.startsWith("+") || token.startsWith("&"))
                ? token.substring(1).toUpperCase()
                : token.toUpperCase();

        return instructionFormats.containsKey(normalized)
                || normalized.equals("START")
                || normalized.equals("END")
                || normalized.equals("BYTE")
                || normalized.equals("WORD")
                || normalized.equals("RESB")
                || normalized.equals("RESW")
                || normalized.equals("BASE");
    }

    private static int getInstructionSize(String operation, String operand) {
        if (operation.equalsIgnoreCase("RESW")) {
            return Integer.parseInt(operand) * 3;
        } else if (operation.equalsIgnoreCase("RESB")) {
            return Integer.parseInt(operand);
        } else if (operation.equalsIgnoreCase("WORD")) {
            return operand.split(",").length * 3;
        } else if (operation.equalsIgnoreCase("BYTE")) {
            if (operand.startsWith("C'")) {
                String literal = operand.replaceAll("^C'|'$", "");
                return literal.length();
            } else if (operand.startsWith("X'")) {
                String hex = operand.replaceAll("^X'|'$", "");
                return hex.length() / 2;
            }
            return 1;
        } else {
            boolean isFormat4 = operation.startsWith("+");
            boolean isFormat3F = operation.startsWith("&");

            String opcode = (isFormat4 || isFormat3F) ? operation.substring(1) : operation;

            Integer format = instructionFormats.get(opcode.toUpperCase());
            if (format == null) return 3;  // Default if not found

            if (isFormat4 && format == 3) return 4;
            if (isFormat3F && format == 3) return 3; // Treat format 3F same as format 3

            return format;
        }


}

    private static void generateLocationCounter() throws IOException {
        BufferedWriter locWriter = new BufferedWriter(new FileWriter("out_pass1.txt"));
        for (String line : linesWithLoc) {
            locWriter.write(line);
            locWriter.newLine();
        }
        locWriter.close();
    }

    private static void generateSymbolTable() throws IOException {
        BufferedWriter symbWriter = new BufferedWriter(new FileWriter("symbTable.txt"));
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            symbWriter.write(entry.getKey() + "\t" + String.format("%04X", entry.getValue()));
            symbWriter.newLine();
        }
        symbWriter.close();
    }

    private static void initInstructionFormats() {
        instructionFormats.put("FIX", 1);
        instructionFormats.put("FLOAT", 1);
        instructionFormats.put("HIO", 1);
        instructionFormats.put("NORM", 1);
        instructionFormats.put("SIO", 1);
        instructionFormats.put("TIO", 1);

        instructionFormats.put("ADDR", 2);
        instructionFormats.put("CLEAR", 2);
        instructionFormats.put("COMPR", 2);
        instructionFormats.put("DIVR", 2);
        instructionFormats.put("MULR", 2);
        instructionFormats.put("RMO", 2);
        instructionFormats.put("SHIFTL", 2);
        instructionFormats.put("SHIFTR", 2);
        instructionFormats.put("SUBR", 2);
        instructionFormats.put("SVC", 2);
        instructionFormats.put("TIXR", 2);

        String[] format3 = {
                "ADD", "ADDF", "AND", "COMP", "COMPF", "DIV", "DIVF", "J", "JEQ", "JGT", "JLT",
                "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS", "LDT", "LDX", "LPS", "MUL",
                "MULF", "OR", "RD", "RSUB", "SSK", "STA", "STB", "STCH", "STF", "STI", "STL",
                "STS", "STSW", "STT", "STX", "SUB", "SUBF", "TD", "TIX", "WD"
        };
        for (String op : format3) {
            instructionFormats.put(op, 3);
        }
    }
}


