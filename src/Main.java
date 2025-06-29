public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Starting SIC/XE Assembler ===");

            // Step 1: generate intermediate.txt
            InputProcessor.process("in4.txt");

            // Step 2: generate out_pass1.txt and symbTable.txt
            Pass1.main(new String[0]);

            // Step 3: generate out_pass2.txt and HTME.txt
            Pass2.main(new String[0]);

            System.out.println("✅ Assembly complete. output files created successfully.");
        } catch (Exception e) {
            System.err.println("🚨 Error during assembly: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
