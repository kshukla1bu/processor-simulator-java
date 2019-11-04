# processor-simulator-java

Java application to simulate out of order processor  
Supports:
- All pipeline stages (Fetch, Decode, Execute, Memory, Writeback)
  - 3 Seperate execution paths for Multiplication, Division and Other instructions. 
- Issue Queue
- Load Store Queue
- Reorder Buffer
- Register Rename Table
- Commit Table

## Following is the sample output for this application:  

Enter your choice in integer '1)initialize','2)simulate','3)display' or '4)exit'  
1  
Enter the filename:  
test1.txt  
1:4004:MOVC,R0,#0  
2:4008:MOVC,R1,#1  
3:4012:MOVC,R2,#2  
4:4016:MOVC,R3,#4  
5:4020:ADD,R4,R0,R1  
6:4024:DIV,R5,R3,R2  
7:4028:MUL,R6,R5,R2  
8:4032:ADD,R7,R1,R1  
9:4036:DIV,R11,R3,R2  
10:4040:ADD,R12,R6,R11  
11:4044:SUB,R13,R6,R11  
12:4048:MUL,R14,R6,R11  
12 lines of file  
4048  
4004  
R[0] = 0, Status= false  
R[1] = 0, Status= false  
R[2] = 0, Status= false  
R[3] = 0, Status= false  
R[4] = 0, Status= false  
R[5] = 0, Status= false  
R[6] = 0, Status= false  
R[7] = 0, Status= false  
R[8] = 0, Status= false  
R[9] = 0, Status= false  
R[10] = 0, Status= false  
R[11] = 0, Status= false  
R[12] = 0, Status= false  
R[13] = 0, Status= false  
R[14] = 0, Status= false  
R[15] = 0, Status= false  
Code Memory: MOVC,R0,#0  
Code Memory: MOVC,R1,#1  
Code Memory: MOVC,R2,#2  
Code Memory: MOVC,R3,#4  
Code Memory: ADD,R4,R0,R1  
Code Memory: DIV,R5,R3,R2  
Code Memory: MUL,R6,R5,R2  
Code Memory: ADD,R7,R1,R1  
Code Memory: DIV,R11,R3,R2  
Code Memory: ADD,R12,R6,R11  
Code Memory: SUB,R13,R6,R11  
Code Memory: MUL,R14,R6,R11  
Enter your choice in integer '1)initialize','2)simulate','3)display' or '4)exit'  
2  
How many cycles to simulate?  
19  
Enter your choice in integer '1)initialize','2)simulate','3)display' or '4)exit'  
3  
DISPLAY FUNCTION  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB: <EMPTY>  
ISSUE Queue: <EMPTY>  
RENAME Table: <EMPTY>  
No Instruction in DECODE  
FETCH Stage :I0: MOVC,R0,#0  
**End of Cycle: 1**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB: <EMPTY>  
ISSUE Queue: <EMPTY>  
RENAME Table: <EMPTY>  
DECODE Stage :I0: MOVC,R0,#0  
FETCH Stage :I1: MOVC,R1,#1  
**End of Cycle: 2**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB:   
		I0: MOVC,P0,#0  
ISSUE Queue :  
		I0: MOVC,P0,#0  
RENAME Table:   
		R0:P0  
DECODE Stage :I1: MOVC,R1,#1  
FETCH Stage :I2: MOVC,R2,#2  
**End of Cycle: 3**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I0: MOVC,P0,#0  
LSQ: <EMPTY>  
Commit:  
ROB:  
		I0: MOVC,P0,#0  
		I1: MOVC,P1,#1  
ISSUE Queue :  
		I1: MOVC,P1,#1  
RENAME Table:   
		R0:P0  
		R1:P1  
DECODE Stage :I2: MOVC,R2,#2  
FETCH Stage :I3: MOVC,R3,#4  
**End of Cycle: 4**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I1: MOVC,P1,#1  
LSQ: <EMPTY>  
Commit:  
		I0: MOVC,P0,#0  
ROB:   
		I1: MOVC,P1,#1  
		I2: MOVC,P2,#2  
ISSUE Queue :  
		I2: MOVC,P2,#2  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
DECODE Stage :I3: MOVC,R3,#4  
FETCH Stage :I4: ADD,R4,R0,R1  
**End of Cycle: 5**  

No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I2: MOVC,P2,#2  
LSQ: <EMPTY>  
Commit:  
		I1: MOVC,P1,#1  
ROB:   
		I2: MOVC,P2,#2  
		I3: MOVC,P3,#4  
ISSUE Queue :  
		I3: MOVC,P3,#4  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
DECODE Stage :I4: ADD,R4,R0,R1  
FETCH Stage :I5: DIV,R5,R3,R2  
**End of Cycle: 6**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I3: MOVC,P3,#4  
LSQ: <EMPTY>  
Commit:  
		I2: MOVC,P2,#2  
ROB:   
		I3: MOVC,P3,#4  
		I4: ADD,P4,P0,P1  
ISSUE Queue :  
		I4: ADD,P4,P0,P1  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
DECODE Stage :I5: DIV,R5,R3,R2  
FETCH Stage :I6: MUL,R6,R5,R2  
**End of Cycle: 7**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I4: ADD,P4,P0,P1  
LSQ: <EMPTY>  
Commit:  
		I3: MOVC,P3,#4  
ROB:   
		I4: ADD,P4,P0,P1  
		I5: DIV,P5,P3,P2  
ISSUE Queue :  
		I5: DIV,P5,P3,P2  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
DECODE Stage :I6: MUL,R6,R5,R2  
FETCH Stage :I7: ADD,R7,R1,R1  
**End of Cycle: 8**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						DIVISION 1 Stage :DIV,P5,P3,P2  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
		I4: ADD,P4,P0,P1  
ROB:  
		I5: DIV,P5,P3,P2  
		I6: MUL,P6,P5,P2  
ISSUE Queue :  
		I6: MUL,P6,P5,P2  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
DECODE Stage :I7: ADD,R7,R1,R1  
FETCH Stage :I8: DIV,R11,R3,R2  
**End of Cycle: 9**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						DIVISION 2 Stage :I5: DIV,P5,P3,P2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB:   
		I5: DIV,P5,P3,P2  
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
ISSUE Queue :  
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
DECODE Stage :I8: DIV,R11,R3,R2  
FETCH Stage :I9: ADD,R12,R6,R11  
**End of Cycle: 10**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						DIVISION 3 Stage :I5: DIV,P5,P3,P2  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I7: ADD,P7,P1,P1  
LSQ: <EMPTY>  
Commit:  
ROB:   
		I5: DIV,P5,P3,P2  
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
		I8: DIV,P8,P3,P2  
ISSUE Queue :  
		I6: MUL,P6,P5,P2  
		I8: DIV,P8,P3,P2  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
DECODE Stage :I9: ADD,R12,R6,R11  
FETCH Stage :I10: SUB,R13,R6,R11  
**End of Cycle: 11**  
  
No Instruction in MEMORY  
						DIVISION 4 Stage :I5: DIV,P5,P3,P2  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						DIVISION 1 Stage :DIV,P8,P3,P2  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB:   
		I5: DIV,P5,P3,P2  
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
		I8: DIV,P8,P3,P2  
		I9: ADD,P9,P6,P8  
ISSUE Queue :  
		I6: MUL,P6,P5,P2  
		I9: ADD,P9,P6,P8  
RENAME Table:  
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
DECODE Stage :I10: SUB,R13,R6,R11  
FETCH Stage :I11: MUL,R14,R6,R11  
**End of Cycle: 12**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						DIVISION 2 Stage :I8: DIV,P8,P3,P2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			MULTIPLY 1 Stage :I6: MUL,P6,P5,P2  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
		I5: DIV,P5,P3,P2  
ROB:   
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
		I8: DIV,P8,P3,P2  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
ISSUE Queue :  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
DECODE Stage :I11: MUL,R14,R6,R11  
No Instruction in FETCH  
**End of Cycle: 13**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						DIVISION 3 Stage :I8: DIV,P8,P3,P2  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			MULTIPLY 2 Stage :I6: MUL,P6,P5,P2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB:   
		I6: MUL,P6,P5,P2  
		I7: ADD,P7,P1,P1  
		I8: DIV,P8,P3,P2  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
ISSUE Queue :  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 14**  
  
No Instruction in MEMORY  
						DIVISION 4 Stage :I8: DIV,P8,P3,P2  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
		I6: MUL,P6,P5,P2
		I7: ADD,P7,P1,P1  
ROB:   
		I8: DIV,P8,P3,P2  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
ISSUE Queue :  
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 15**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2    
			MULTIPLY 1 Stage :I11: MUL,P11,P6,P8  
EXECUTE Stage :I9: ADD,P9,P6,P8  
LSQ: <EMPTY>  
Commit:  
		I8: DIV,P8,P3,P2  
ROB:   
		I9: ADD,P9,P6,P8  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
ISSUE Queue :  
		I10: SUB,P10,P6,P8  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 16**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			MULTIPLY 2 Stage :I11: MUL,P11,P6,P8  
			No Instruction in MULTIPLY 1  
EXECUTE Stage :I10: SUB,P10,P6,P8  
LSQ: <EMPTY>  
Commit:  
		I9: ADD,P9,P6,P8  
ROB:   
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
ISSUE Queue: <EMPTY>  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 17**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
		I10: SUB,P10,P6,P8  
		I11: MUL,P11,P6,P8  
ROB: <EMPTY>  
ISSUE Queue: <EMPTY>  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 18**  
  
No Instruction in MEMORY  
						No Instruction in DIVISION 4  
						No Instruction in DIVISION 3  
						No Instruction in DIVISION 2  
						No Instruction in DIVISION 1  
			No Instruction in MULTIPLY 2  
			No Instruction in MULTIPLY 1  
No Instruction in EXECUTE  
LSQ: <EMPTY>  
Commit:  
ROB: <EMPTY>  
ISSUE Queue: <EMPTY>  
RENAME Table:   
		R0:P0  
		R1:P1  
		R2:P2  
		R3:P3  
		R4:P4  
		R5:P5  
		R6:P6  
		R7:P7  
		R11:P8  
		R12:P9  
		R13:P10  
		R14:P11  
No Instruction in DECODE  
No Instruction in FETCH  
**End of Cycle: 19**
