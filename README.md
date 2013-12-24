S-COBOL_compiler
================


Note from the UV
-----

I want you produce a similar work that the one during the exercise session about the semantic analysis.
That is all actions you add in order to 

1. resolve identifier

2. any update on the table of symbols

3. type checking

4. checking constant

You can see these actions as all actions that will report an error (or a warning if you provide a default behavior)


Thoughs
----

> Since the variables can only be declared in the DATA division, we don't need to consider local
variables; they will all be global. This particularity will allow us to use the table of symbols
in an easier way; we can simply find a variable in the table using its identifier (which
will thus be unique).

>clang cobol-source.c -S -emit-llvm -o cobol-source.ll
