import nl.saxion.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSuite {
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    private TestParser testParser;

    @Before
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @Before
    public void resetOutputStrings() {
        testParser = new TestParser();
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void StartExitProgram() {
        final String input = "0";
        provideInput(input);

        Main.main(new String[0]);

        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());

    }

    @Test
    public void DisplayPrints() {
        final String input = "6\n0";
        provideInput(input);

        Main.main(new String[0]);

        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    @Test
    public void DisplayPrinters() {
        final String input = "7\n0";
        provideInput(input);

        Main.main(new String[0]);

        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    @Test
    public void DisplaySpools() {
        final String input = "8\n0";
        provideInput(input);

        Main.main(new String[0]);

        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    @Test
    public void AddNewPrintTask() {
        // First obtain list of prints, we assume this will remain the same for the next execution.
        String input = "6\n0";
        provideInput(input);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        int printNr = testParser.getPrintNr("Acoustic Guitar Cooky Cutter");
        int colorNr = testParser.getColorNr("Blue", "PLA");

        setUpOutput();
        resetOutputStrings();

        // 1 PLA 2 PETG and 3 ABS are expected to have the same order.
        // Colors are assumed to have the order of the spools in the document.
        String newInput = "1\n" + printNr + "\n1\n"+colorNr+"\n9\n0";

        testParser.addExpectedPrintTask("Acoustic Guitar Cooky Cutter", "Blue", "PLA");

        provideInput(newInput);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    @Test
    public void AddNewPrintTaskAndStartQueue() {
        // First obtain list of prints, we assume this will remain the same for the next execution.
        String input = "6\n0";
        provideInput(input);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        int printNr = testParser.getPrintNr("Acoustic Guitar Cooky Cutter");
        int colorNr = testParser.getColorNr("Blue", "PLA");

        setUpOutput();
        resetOutputStrings();

        // 1 PLA 2 PETG and 3 ABS are expected to have the same order.
        // Colors are assumed to have the order of the spools in the document.
        String newInput = "1\n" + printNr + "\n1\n"+ colorNr +"\n5\n0";

        testParser.addExpectedPrintTask("Acoustic Guitar Cooky Cutter", "Blue", "PLA");

        provideInput(newInput);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    @Test
    public void AddNewMultiplePrintTasksAndStartQueue() {
        // First obtain list of prints, we assume this will remain the same for the next execution.
        String input = "6\n0";
        provideInput(input);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        int printNr = testParser.getPrintNr("Acoustic Guitar Cooky Cutter");
        int colorNr = testParser.getColorNr("Blue", "PLA");

        setUpOutput();
        resetOutputStrings();

        // 1 PLA 2 PETG and 3 ABS are expected to have the same order.
        // Colors are assumed to have the order of the spools in the document.
        String newInput = "1\n" + printNr + "\n1\n"+ colorNr +"\n5\n0";

        testParser.addExpectedPrintTask("Acoustic Guitar Cooky Cutter", "Blue", "PLA");

        provideInput(newInput);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }

    /**
     * This test does not work because it needs to know what printer it is assigned to, so it can complete the print.
     */
    @Test
    public void AddNewPrintTaskAndStartQueueAndCompletePrint() {
        // First obtain list of prints, we assume this will remain the same for the next execution.
        String input = "6\n0";
        provideInput(input);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        int printNr = testParser.getPrintNr("Acoustic Guitar Cooky Cutter");
        int colorNr = testParser.getColorNr("Blue", "PLA");

        setUpOutput();
        resetOutputStrings();

        // 1 PLA 2 PETG and 3 ABS are expected to have the same order.
        // Colors are assumed to have the order of the spools in the document.
        String newInput = "1\n" + printNr + "\n1\n"+ colorNr +"\n5\n0";

        testParser.addExpectedPrintTask("Acoustic Guitar Cooky Cutter", "Blue", "PLA");

        provideInput(newInput);
        Main.main(new String[0]);

        int printerId = testParser.getPrinterIDWithTask("Acoustic Guitar Cooky Cutter PLA [Blue]");
        if(printerId == -1) {
            System.err.println("Printer was not found");
        }

        setUpOutput();
        resetOutputStrings();

        // TODO: SEND HELP! I've written myself into a corner and I can't get out.
        newInput = "1\n" + printNr + "\n1\n"+ colorNr +"\n5\n2"+printerId+"\n0";

        testParser.addExpectedPrintTask("Acoustic Guitar Cooky Cutter", "Blue", "PLA");

        provideInput(newInput);
        Main.main(new String[0]);
        testParser.parse(getOutput());

        assertTrue(testParser.noErrors());
    }
}
