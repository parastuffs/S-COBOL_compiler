identification division.
       program-id. Algo-Euclide.
       author. Euclide.
       date-written. 300 BNC.
environment division.
       configuration section.
       source-computer. x8086.
       object-computer. LLVM.
data division.
      /we define 3 variables (a, b, c).
       working-storage section.
      /s for signed.
       77 a pic s9(5).
      /9 for digit (int).
       77 b pic s9(5).
      /(5) for 5 digits.
       77 c pic s9(5).
