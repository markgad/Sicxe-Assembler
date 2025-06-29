# SIC/XE Assembler

A two-pass assembler for the SIC/XE architecture, developed for a systems programming course.

## Features

- Two-pass assembly (symbol table generation → object code generation)
- Supports all SIC/XE instruction formats (1, 2, 3, 4)
- Multiple addressing modes: Immediate (#), Indirect (@), Indexed (,X)
- PC-relative and Base-relative addressing
- HTME object program generation

## Files

- `Main.java` - Entry point
- `InputProcessor.java` - Preprocesses source code
- `Pass1.java` - Builds symbol table and assigns locations
- `Pass2.java` - Generates object code and HTME records

## Usage

```bash
javac *.java
java Main
```

## Input/Output

**Input:** Multiple test files (`in1.txt`, `in2.txt`, `in3.txt`, `in4.txt`, etc.)

**Output:** 
- `intermediate.txt` - Preprocessed code
- `out_pass1.txt` - Location counters
- `symbTable.txt` - Symbol table
- `out_pass2.txt` - Object codes
- `HTME.txt` - Final object program


---
*Systems Programming Project*
