# Testing

An attempt was made to write a reasonably comprehensive test suite to allow students to check if the program still
works correctly. However, it quickly became apparent that this manner of black box testing results in a lot of 
challenges to properly test it. The code is still here and some tests do in face function correctly but not everything
could be tested.

## What is tested

- StartExitProgram - Just start and exit the program
- DisplayPrints - Display the prints checks if all prints are present regardless of order.
- DisplayPrinters - Display the printers check if they are all present regardless of order.
- DisplaySpools - Display the spools check they are present regardless of order.
- AddNewPrintTask - Add a print and check it appears in the queue.
- AddNewPrintTaskAndStartQueue - Add a print and start the queue check if the selected print fits on the printer.
- AddMultipleNewPrintsAndStartQueue - Add multiple prints and check if the printer assignment is correct.

## What is not tested

- CompletePrint - Complete a print and check if it's properly remove.
- CompletePrintStartAnother - Complete a print and assign a new print to this printer.
- FailedPrintStartsAgain - A failed print is started again.
- FailedPrintSpoolEmptyDifferentPrint - A failed print is added to the queue but spool is too low and a different print is selected.

## Print and Printer fit Truth table

The following tables show which print fits on which printer.

| PLA and PETG                 | Enterprise | Serenity | Red Dwarf | Heart of Gold | Tardis | Rocinante | Bebop |
|------------------------------|------------|----------|-----------|---------------|--------|-----------|-------|
| Acoustic Guitar Cooky Cutter | V          | V        | V         | V             | V      | V         | V     |
| Stegosaurus Pickholder       | V          | V        | V         | V             | V      | V         | X     |
| Collapsing Jian              | V          | V        | V         | V             | X      | V         | X     |
| Earth Globe                  | X          | X        | X         | V             | X      | X         | X     |
| Moon Lamp                    | X          | X        | V         | X             | X      | X         | X     |
| Cathedral                    | X          | X        | X         | V             | X      | X         | X     |
| Fucktopus                    | V          | V        | V         | V             | V      | V         | V     |
| Lizard                       | X          | X        | X         | V             | X      | X         | X     |
| Tree Frog                    | X          | X        | X         | V             | X      | X         | X     |

| ABS                          | Enterprise | Serenity | Red Dwarf | Heart of Gold | Tardis | Rocinante | Bebop |
|------------------------------|------------|----------|-----------|---------------|--------|-----------|-------|
| Acoustic Guitar Cooky Cutter | X          | V        | X         | X             | X      | V         | V     |
| Stegosaurus Pickholder       | X          | V        | X         | X             | X      | V         | X     |
| Collapsing Jian              | X          | V        | X         | X             | X      | V         | X     |
| Earth Globe                  | X          | X        | X         | X             | X      | X         | X     |
| Moon Lamp                    | X          | X        | X         | X             | X      | X         | X     |
| Cathedral                    | X          | X        | X         | X             | X      | X         | X     |
| Fucktopus                    | X          | V        | X         | X             | X      | V         | V     |
| Lizard                       | X          | X        | X         | X             | X      | X         | X     |
| Tree Frog                    | X          | X        | X         | X             | X      | X         | X     |