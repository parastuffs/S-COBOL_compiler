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
       77 a pic s9(5) value 0.
      /9 for digit (int).
       77 b pic s9(5).
      /(5) for 5 digits.
       77 c pic s9(6).
procedure division.
	main section.
	* Euclide’s Algorithm.
	/The first label is the start point.
	start.
	/read int from stdin and put it into a.
	accept a.
	/read int from stdin and put it into b.
	accept b.
	/call find label until b equals 0.
	perform find until b = 0.
	/write ’valeur:’ on stdout.
	display 'valeur:'.
	/write the content of a on stdout.
	display a.
	/stop the program.
	stop run.
	/create a label called ’find’.
	find.
	move c to b.
	/call diff label until a is less than b.
	perform diff until a < b.
	/put the value of a into b.
	move a to b.
	/put the value of c into a.
	move c to a.
	/create a label called ’diff’.
	diff.
	* Compute a modulo b.
	/a becomes a-b.
	substract b from a.
end program Algo-Euclide.
