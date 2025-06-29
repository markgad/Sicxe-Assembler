import java.io.*;

public class InputProcessor {
    public static void process(String inputFileName) throws IOException {
        File inputFile = new File(inputFileName);
        File outputFile = new File("intermediate.txt");

        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) continue;

                int commentIndex = line.indexOf(";");
                if (commentIndex != -1) line = line.substring(0, commentIndex).trim();

                line = line.replaceAll("^\\d+\\s+", "");
                if (line.isEmpty()) continue;

                String[] tokens = line.split("\\s+", 3);
                String label = "", opcode = "", operand = "";
                if (tokens.length == 3) {
                    label = tokens[0];
                    opcode = tokens[1];
                    operand = tokens[2];
                } else if (tokens.length == 2) {
                    opcode = tokens[0];
                    operand = tokens[1];
                } else if (tokens.length == 1) {
                    opcode = tokens[0];
                }

                writer.write(String.format("%-10s%-10s%-10s", label, opcode, operand).stripTrailing());
                writer.newLine();
            }
        }

        System.out.println("✅ Intermediate file generated from " + inputFileName);
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("in2.txt");
            File outputFile = new File("intermediate.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and full-line comments (e.g., starting with ';')
                if (line.isEmpty() || line.startsWith(";")) continue;

                // Remove inline comments starting with ';'
                int commentIndex = line.indexOf(";");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                // Remove leading line numbers (e.g., 1000 LABEL ...)
                line = line.replaceAll("^\\d+\\s+", "");

                if (line.isEmpty()) continue;

                // Tokenize into parts: label (optional), opcode, operand
                String[] tokens = line.trim().split("\\s+", 3);
                String label = "", opcode = "", operand = "";

                if (tokens.length == 3) {
                    label = tokens[0];
                    opcode = tokens[1];
                    operand = tokens[2];
                } else if (tokens.length == 2) {
                    opcode = tokens[0];
                    operand = tokens[1];
                } else if (tokens.length == 1) {
                    opcode = tokens[0];
                }

                // Write with alignment (tabs between fields)
                writer.write(String.format("%-10s%-10s%-10s", label, opcode, operand).stripTrailing());

                writer.newLine();
            }

            reader.close();
            writer.close();

            System.out.println("Intermediate file generated with formatting.");
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}
