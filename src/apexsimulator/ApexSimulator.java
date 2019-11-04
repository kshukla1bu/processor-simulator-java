package apexsimulator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Code_Line {
    int line_no;
    int address;
    String Inst_String;
}

public class ApexSimulator 
{
    StringBuffer sb = new StringBuffer("");
    StringBuffer sb1 = new StringBuffer("");
    Register FI_PSW = new Register();
    Register FM_PSW = new Register();
    Register FD_PSW = new Register();
    Register R_PSW = new Register();
    P_Register[] P = new P_Register[32];
    ArrayList<Instruction_info> isq = new ArrayList<>(16);
    ArrayList<Instruction_info> lsq = new ArrayList<>(32);
    ArrayList<Instruction_info> rob= new ArrayList<>(32);
    ArrayList<String> RAT = new ArrayList<>();
    ArrayList<Instruction_info> comm = new ArrayList<>();
    int i = 0;
    String temp;
    stage stg[];
    stage stgISQ;
    stage stgD1;
    stage stgD2;
    stage stgD3;
    stage stgD4;
    stage stgM1;
    stage stgM2;
    int baseAddress = 4000;
    int lnNumber = 1;
    public Code_Memory cme;
    public Register_File RFile;
    public Forward_Bus FBus;
    public Data_Memory dme;
    public Flags flag;
    public Stats stat;
    boolean EndOfCycle = false;
    boolean temp_status;
    boolean R1;
    boolean R2;
    boolean B1;
    boolean B2;
    boolean temp_Zero;
    int arithmetic_inst_cnt = 0;
    int temp_index;
    int temp_PC;
    int Temp_LineNo;
    int temp_Fsrc1;
    int temp_Fsrc2;
    boolean branch;
    boolean OneCycleWait;
    boolean halt;
    boolean exeWait;
    boolean mulWait;
    boolean EOF;
    boolean str_rmved;
    /**
    * @param args the command line arguments
    */
    ApexSimulator()
    {
        stg = new stage[5];
        stg[0] = new stage();
        stg[1] = new stage();
        stgISQ = new stage();
        stgD1 = new stage();
        stgD2 = new stage();
        stgD3 = new stage();
        stgD4 = new stage();
        stgM1 = new stage();
        stgM2 = new stage();
        stg[2] = new stage();
        stg[2] = new stage();
        stg[3] = new stage();
        stg[4] = new stage();
    }
        
    public static void main(String[] args) 
    {
        ApexSimulator ApSim = new ApexSimulator();
        Scanner scn = new Scanner(System.in);
            String FN;
            int k,g;
            while(true){
                System.out.println("Enter your choice in integer '1)initialize','2)simulate','3)display' or '4)exit'");
                k = scn.nextInt();
                switch(k){
                    case 1:
                        System.out.println("Enter the filename:");
                        FN = scn.next();
                        ApSim.initialize(FN);
                        break;
                    case 2:
                        System.out.println("How many cycles to simulate?");
                        g = scn.nextInt();
                        ApSim.simulate(g);
                        break;
                    case 3:
                        ApSim.display();
                        break;
                    case 4:
                        System.exit(-1);
                        break;
                    }
                if(k < 1 || k > 4)
                {
                    System.out.println(k);
                    break;
                }
            } 
    }
    int n  = 0;
    void fetch()
    {
        Instruction_info instructionInfo = new Instruction_info();
        if((branch && !OneCycleWait)) // || (branch && OneCycleWait))
        {
            if  ( n < lnNumber-1)
            {
                instructionInfo.inst_string = cme.c1[n].Inst_String;
                instructionInfo.PC = cme.c1[n].address;
                instructionInfo.lineNumber = cme.c1[n].line_no - 1;
                temp_PC = instructionInfo.PC;
                sb.append("FETCH Stage :I"+instructionInfo.lineNumber+" "+cme.c1[n].Inst_String +"\n");
                stg[0].op_instruction = instructionInfo;
                if(!stg[0].stalled && !stg[1].stalled)
                {
                    stg[0].op_instruction = instructionInfo;
                    stg[1].ip_instruction = stg[0].op_instruction;
                    stg[0].op_instruction = null; 
                    n = n + 1;
                }
            }
            else
            {
                sb.append("No Instruction in FETCH\n");
            }
            branch = false;
            return;
        }
    
        if(!halt)
        {
            if( n < lnNumber-1)
            {
                instructionInfo.inst_string = cme.c1[n].Inst_String;
                instructionInfo.PC = cme.c1[n].address;
                instructionInfo.lineNumber = cme.c1[n].line_no - 1;
                temp_PC = instructionInfo.PC;
                sb.append("FETCH Stage :I"+instructionInfo.lineNumber+": "+cme.c1[n].Inst_String +"\n");
                stg[0].op_instruction = instructionInfo;
                if((!stg[0].stalled && !stg[1].stalled))
                {
                    stg[0].op_instruction = instructionInfo;
                    stg[1].ip_instruction = stg[0].op_instruction;
                    stg[0].op_instruction = null; 
                    n = n + 1;
                }
            }
            else
            {
                sb.append("No Instruction in FETCH\n");
            }
        }
        if(branch && OneCycleWait)
        {
            n = temp_index;
            for(int i = 0;i<RFile.R.length;i++)
            RFile.R[i].status = false;
            OneCycleWait = false;
            arithmetic_inst_cnt = 0;
            stg[1].ip_instruction = null;
            stg[2].ip_instruction = null;
            stgM1.ip_instruction = null;
            stgD1.ip_instruction = null;
        }
        if(halt)
        {
            sb.append("No Instruction in FETCH\n");
            if(EOF)
            {
                System.exit(0);
            }
            return;
        }
    }
    
    void decode()
    {
        String temp;
        Instruction_info instructionInfo = stg[1].ip_instruction ;
        if(instructionInfo == null)
        {
            sb.append("No Instruction in DECODE\n");
            return;
        }
        temp_status = false;
        String Splitter[] = instructionInfo.inst_string.split(",");
        instructionInfo.opcode = Splitter[0];
        
        for(int i=0; i<Splitter.length; i++)
        {
            if(i==0)
                continue;
            
            if(Splitter[i].contains("R") || Splitter[i].contains("#"))
                Splitter[i] = Splitter[i].substring(1);
        
            switch(instructionInfo.opcode){
                case("MOVC"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("ADD"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("SUB"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("LOAD"):
                    if(i==1)
                    {
                        instructionInfo.addr_dest_reg = Integer.parseInt(Splitter[i]); 
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    }   
                    break;
                case("STORE"):
                    if(i==1)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("BZ"):
                    if(i==1)
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    break;
                case("BNZ"):
                    if(i==1)
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    break;
                case("JUMP"):
                    if(i==1)
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    if(i==2)
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    break;
                case("JAL"):
                    if(i==1)
                    {
                        instructionInfo.addr_dest_reg = Integer.parseInt(Splitter[i]); 
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.literal = Integer.parseInt(Splitter[i]);
                    }   
                    break;    
                case("HALT"):
                    halt = true;
                    break;
                case("AND"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("OR"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("EX-OR"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                case("MUL"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                    break;
                    case("DIV"):
                    if(i==1)
                    {
                        int regNo = Integer.parseInt(Splitter[i]);
                        instructionInfo.addr_dest_reg = regNo;
                        temp_status = true;
                    }
                    if(i==2)
                    {
                        instructionInfo.addr_src_reg1 = Integer.parseInt(Splitter[i]);
                    }
                    if(i==3)
                    {
                        instructionInfo.addr_src_reg2 = Integer.parseInt(Splitter[i]);
                    }
                }
            }
        switch(instructionInfo.opcode){
            case("MOVC"):
                instructionInfo.IQ_dest_tag = next_Avail_P_Reg();
                instructionInfo.IQ_status = true;
                RFile.R[instructionInfo.addr_dest_reg].renamed = true;
                RFile.R[instructionInfo.addr_dest_reg].renamedTo = instructionInfo.IQ_dest_tag;
                break;
            case("ADD"):
            case("SUB"):
            case("MUL"):
            case("DIV"):    
            case("AND"):    
            case("OR"):
            case("EX-OR"):
                instructionInfo.IQ_src1_tag =  RFile.R[instructionInfo.addr_src_reg1].renamedTo;
                instructionInfo.IQ_src2_tag =  RFile.R[instructionInfo.addr_src_reg2].renamedTo;
                
                /*Getting forwarded value from execution units*/
                if(P[instructionInfo.IQ_src1_tag].valid)
                {
                    instructionInfo.IQ_src1_value = P[instructionInfo.IQ_src1_tag].value;
                    instructionInfo.IQ_src1_ready = true;
                }
                if(P[instructionInfo.IQ_src2_tag].valid)
                {
                    instructionInfo.IQ_src2_value = P[instructionInfo.IQ_src2_tag].value;
                    instructionInfo.IQ_src2_ready = true;
                }
                if(instructionInfo.IQ_src1_ready && instructionInfo.IQ_src2_ready)
                {
                    instructionInfo.IQ_status = true;
                }
                /*Renaming Destination Register*/
                instructionInfo.IQ_dest_tag = next_Avail_P_Reg();
                RFile.R[instructionInfo.addr_dest_reg].renamed = true;
                RFile.R[instructionInfo.addr_dest_reg].renamedTo = instructionInfo.IQ_dest_tag;
                break;
            case("STORE"):
                /*Getting value for source register*/
                instructionInfo.IQ_src1_tag =  RFile.R[instructionInfo.addr_src_reg1].renamedTo;
                instructionInfo.IQ_src2_tag =  RFile.R[instructionInfo.addr_src_reg2].renamedTo;
                
                /*Getting forwarded value from execution units*/
                if(P[instructionInfo.IQ_src1_tag].valid)
                {
                    instructionInfo.IQ_src1_value = P[instructionInfo.IQ_src1_tag].value;
                    instructionInfo.IQ_src1_ready = true;
                }
                if(P[instructionInfo.IQ_src2_tag].valid)
                {
                    instructionInfo.IQ_src2_value = P[instructionInfo.IQ_src2_tag].value;
                    instructionInfo.IQ_src2_ready = true;
                }
                if(instructionInfo.IQ_src1_ready && instructionInfo.IQ_src2_ready)
                {
                    instructionInfo.IQ_status = true;
                }
                break;
            case("JUMP"):
                instructionInfo.IQ_src1_tag =  RFile.R[instructionInfo.addr_src_reg1].renamedTo;
                
                if(P[instructionInfo.IQ_src1_tag].valid)
                {
                    instructionInfo.IQ_src1_value = P[instructionInfo.IQ_src1_tag].value;
                    instructionInfo.IQ_src1_ready = true;
                }
                if(instructionInfo.IQ_src1_ready)
                {
                    instructionInfo.IQ_status = true;
                }
                break;
            case("LOAD"):
            case("JAL"):
                instructionInfo.IQ_src1_tag =  RFile.R[instructionInfo.addr_src_reg1].renamedTo;
                
                if(P[instructionInfo.IQ_src1_tag].valid)
                {
                    instructionInfo.IQ_src1_value = P[instructionInfo.IQ_src1_tag].value;
                    instructionInfo.IQ_src1_ready = true;
                }
                if(instructionInfo.IQ_src1_ready)
                {
                    instructionInfo.IQ_status = true;
                }
                
                instructionInfo.IQ_dest_tag = next_Avail_P_Reg();
                RFile.R[instructionInfo.addr_dest_reg].renamed = true;
                RFile.R[instructionInfo.addr_dest_reg].renamedTo = instructionInfo.IQ_dest_tag;
                break;
            case("BZ"):
            case("BNZ"):
                B1 = false;
                B2 = false;
                if(Temp_LineNo == FI_PSW.Line)
                {
                    temp_Zero = FI_PSW.isZero;
                    B1 = true;
                }
                else if(Temp_LineNo == FM_PSW.Line)
                {
                    temp_Zero = FM_PSW.isZero;
                    B1 = true;
                }
                else if(Temp_LineNo == FD_PSW.Line)
                {
                    temp_Zero = FD_PSW.isZero;
                    B1 = true;
                }
                if(!B1)
                {
                    if(Temp_LineNo == R_PSW.Line)
                    {
                        flag.zero = R_PSW.isZero;
                        B2 = true;
                    }
                }
                break;
                case("HALT"):
                    halt = true;
                    instructionInfo.ROB_status = true;
                    stg[0].ip_instruction = null;       
        }
        switch(instructionInfo.opcode){
            case("MOVC"):
            case("ADD"):
            case("SUB"):
            case("AND"):
            case("OR"):
            case("EX-OR"):
            case("BZ"):
            case("BNZ"):
            case("LOAD"):
            case("STORE"):
                instructionInfo.IQ_FU = "INT";
                break;
            case("MUL"):
                instructionInfo.IQ_FU = "MUL";
                break;
            case("DIV"):
                instructionInfo.IQ_FU = "DIV";
                break;
            
        }
        switch(instructionInfo.opcode){
            case("MOVC"):
                instructionInfo.renamed_inst_string = instructionInfo.opcode+",P"+instructionInfo.IQ_dest_tag+",#"+instructionInfo.literal;
                break;
            case("ADD"):
            case("SUB"):
            case("MUL"):
            case("DIV"):
            case("AND"):
            case("OR"):
            case("EX-OR"):
                instructionInfo.renamed_inst_string = instructionInfo.opcode+",P"+instructionInfo.IQ_dest_tag+",P"+instructionInfo.IQ_src1_tag+",P"+instructionInfo.IQ_src2_tag;               
                break;         
            case("LOAD"):
            case("JAL"):
                instructionInfo.renamed_inst_string = instructionInfo.opcode+",P"+instructionInfo.IQ_dest_tag+",P"+instructionInfo.IQ_src1_tag+",#"+instructionInfo.literal;
                break;
            case("STORE"):
                instructionInfo.renamed_inst_string = instructionInfo.opcode+",P"+instructionInfo.IQ_src1_tag+",P"+instructionInfo.IQ_src2_tag+",#"+instructionInfo.literal;
                break;
            case("JUMP"):
                instructionInfo.renamed_inst_string = instructionInfo.opcode+",P"+instructionInfo.IQ_src1_tag+",#"+instructionInfo.literal;
                break;
            case("BZ"):
            case("BNZ"):
            case("HALT"):
                instructionInfo.renamed_inst_string = instructionInfo.inst_string;
                break;
        }
        if(!instructionInfo.opcode.equalsIgnoreCase("STORE") && !instructionInfo.opcode.equalsIgnoreCase("BZ") && !instructionInfo.opcode.equalsIgnoreCase("BNZ"))
            RAT.add("R"+instructionInfo.addr_dest_reg+":P"+instructionInfo.IQ_dest_tag);
        
        if(instructionInfo.opcode.equalsIgnoreCase("LOAD")||instructionInfo.opcode.equalsIgnoreCase("STORE"))
        {
            if(isq.size()>16 && rob.size()>32 && lsq.size()>32)
            {
                stg[0].stalled = true;
                stg[1].stalled = true;
            }
        }
        else
        {
            if(isq.size()>16 && rob.size()>32)
            {
                stg[0].stalled = true;
                stg[1].stalled = true;
            }
        }
       
        if((!stg[0].stalled && !stg[1].stalled))
        {
            sb.append("DECODE Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.inst_string+"\n");
            stg[1].op_instruction = instructionInfo;
            if(!instructionInfo.opcode.equalsIgnoreCase("HALT"))
            {    
                stgISQ.ip_instruction = stg[1].op_instruction;
            }
            rob.add(instructionInfo);
            if(instructionInfo.opcode.equalsIgnoreCase("LOAD") || instructionInfo.opcode.equalsIgnoreCase("STORE"))
            {
                lsq.add(instructionInfo);
            }
            stg[1].ip_instruction = null;
        }
        else
        {
            sb.append("DECODE Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.inst_string+": Stalled\n");
        }
    }
    
    void isq()
    {
        Instruction_info instructionInfo = stgISQ.ip_instruction;
        if(instructionInfo == null && isq.isEmpty())
        {
            sb.append("ISSUE Queue: <EMPTY>\n");
            return;
        }
        if(instructionInfo !=null)
        {
            isq.add(instructionInfo);
        }
        sb.append("ISSUE Queue :\n");
        
        
        for(int i=0;i<isq.size();i++)
        {
            if(!isq.get(i).IQ_src1_ready)
            {
                if(P[isq.get(i).IQ_src1_tag].valid)
                {
                    isq.get(i).IQ_src1_value = P[isq.get(i).IQ_src1_tag].value;
                    isq.get(i).IQ_src1_ready = true;
                }
            }
            if(!isq.get(i).IQ_src2_ready && !isq.get(i).opcode.equalsIgnoreCase("LOAD"))
            {
                if(P[isq.get(i).IQ_src2_tag].valid)
                {
                    isq.get(i).IQ_src2_value = P[isq.get(i).IQ_src2_tag].value;
                    isq.get(i).IQ_src2_ready = true;
                }
            }
            if(isq.get(i).IQ_src1_ready && isq.get(i).IQ_src2_ready && !isq.get(i).opcode.equalsIgnoreCase("LOAD"))
            {
                isq.get(i).IQ_status = true;
            }
            if(isq.get(i).IQ_src1_ready && isq.get(i).opcode.equalsIgnoreCase("LOAD"))
            {
                isq.get(i).IQ_status = true;
            } 
        }
        /*Displaying ISSUE QUEUE Contents*/
        for(int i=0;i<isq.size();i++)
        {
            sb.append("\t\tI"+isq.get(i).lineNumber+": "+isq.get(i).renamed_inst_string+"\n");
        }
        
        /*Putting ISQ instruction in INTFU stage if instruction is ready*/
        for(int i=0;i<isq.size();i++)
        {
            if(stg[2].ip_instruction == null)
            {
                if(isq.get(i).IQ_status && isq.get(i).IQ_FU.equalsIgnoreCase("INT"))
                {
                    stg[2].ip_instruction = isq.get(i);
                    isq.remove(i);
                }
            }
        }
        /*Putting ISQ instruction in MULTIPLY1 stage if multiply instruction is ready*/
        for(int i=0;i<isq.size();i++)
        {
            if(stgM1.ip_instruction == null)
            {
                if(isq.get(i).IQ_status && isq.get(i).IQ_FU.equalsIgnoreCase("MUL"))
                {
                    stgM1.ip_instruction = isq.get(i);
                    isq.remove(i);
                }
            }
        }
        
        /*Putting ISQ instruction in DIVISION1 stage if DIVISION instruction is ready*/
        for(int i=0;i<isq.size();i++)
        {
            if(stgD1.ip_instruction == null)
            {
                if(isq.get(i).IQ_status && isq.get(i).IQ_FU.equalsIgnoreCase("DIV"))
                {
                    stgD1.ip_instruction = isq.get(i);
                    isq.remove(i);
                }
            }
        }
        stgISQ.ip_instruction = null;
    }
    
    void commit()
    {
        if(comm.isEmpty())
        {
            sb.append("Commit:\n");
            return;
        }
        sb.append("Commit:\n");
        
        for(int i=0; i<comm.size();i++)
        {
            sb.append("\t\tI"+comm.get(i).lineNumber+": "+comm.get(i).renamed_inst_string+"\n");
        }
        comm.remove(0);
        if(!comm.isEmpty())
        {
            comm.remove(0);
        }
        
    }
    
    void rob()
    {
        if(rob.isEmpty())
        {
            sb.append("ROB: <EMPTY>\n");
            return;
        }
        sb.append("ROB: \n");
        /*Displaying ROB Contents*/
        for(int i=0;i<rob.size();i++)
        {
            sb.append("\t\tI"+rob.get(i).lineNumber+": "+rob.get(i).renamed_inst_string+"\n");
        }
        
        if(rob.get(0).opcode.equalsIgnoreCase("STORE"))
        {
            if(stg[3].ip_instruction.PC == rob.get(0).PC)
            {
                rob.remove(0);
                str_rmved = true;
            }    
        }
        else
            str_rmved = false;
        if(!rob.isEmpty())
        {    
            if(rob.get(0).ROB_status)
            {
                if(rob.get(0).opcode.equalsIgnoreCase("HALT"))
                {
                    EOF = true;
                    free_P_Reg();
                }
                comm.add(rob.get(0));
                rob.remove(0);
                if(!rob.isEmpty())
                {
                    if(!str_rmved)
                    {    
                        if(rob.get(0).ROB_status)
                        {
                            comm.add(rob.get(0));
                            rob.remove(0);
                        }
                    }
                }
            }    
        }
    }
    
    void lsq()
    {
        if(lsq.isEmpty())
        {
            sb.append("LSQ: <EMPTY>\n");
            return;
        }
        sb.append("LSQ:\n");
        
        for(int i=0; i<lsq.size();i++)
        {
            sb.append("\t\tI"+lsq.get(i).lineNumber+": "+lsq.get(i).renamed_inst_string+"\n");
        }
        
        for(int i = 0; i<lsq.size();i++)
        {
            if(lsq.get(i).opcode.equalsIgnoreCase("LOAD"))
            {    
                if(stg[3].ip_instruction == null)
                {
                    if(lsq.get(i).IQ_status && lsq.get(i).LSQ_status)
                    {
                        stg[3].ip_instruction = lsq.get(i);
                        lsq.remove(i);
                    }
                }
            }
        }
        
        if(lsq.size() > 1)
        {
            if(lsq.get(1).opcode.equalsIgnoreCase("LOAD"))
            {
                if(lsq.get(0).opcode.equalsIgnoreCase("STORE"))
                {
                    if(lsq.get(1).LSQ_LD_mem_addr == lsq.get(0).LSQ_STR_mem_addr)
                    {
                        P[lsq.get(1).IQ_dest_tag].value = P[lsq.get(0).IQ_src1_tag].value;
                        lsq.get(1).ROB_status = true;
                        lsq.remove(1);
                    }
                }
            }
        }
        
        if(lsq.get(0).opcode.equalsIgnoreCase("STORE"))
        {
            if(lsq.get(0).PC == rob.get(0).PC)
            {
                if(stg[3].ip_instruction == null)
                {
                    if(lsq.get(0).IQ_status && lsq.get(0).LSQ_status)
                    {
                        stg[3].ip_instruction = lsq.get(0);
                        lsq.remove(0);
                    }
                }
            }
        }
        
        
    }
    
    void division1()
    {
        int temp_val = 0;
        Instruction_info instructionInfo = stgD1.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\t\t\t\tNo Instruction in DIVISION 1\n");
            return;
        }
        if(instructionInfo.opcode.equals("DIV"))
        {
            temp_val = instructionInfo.IQ_src1_value / instructionInfo.IQ_src2_value;
            instructionInfo.val_dest_reg = temp_val;
        }
        else
        {
            halt = true;
            stg[0].ip_instruction = null;
            stg[1].ip_instruction = null;
        }
        sb.append("\t\t\t\t\t\tDIVISION 1 Stage :"+instructionInfo.renamed_inst_string+"\n");
        
        stgD1.op_instruction = instructionInfo;
        stgD2.ip_instruction = stgD1.op_instruction;
        stgD1.ip_instruction = null;   
    }
    
    void division2()
    {
        Instruction_info instructionInfo = stgD2.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\t\t\t\tNo Instruction in DIVISION 2\n");
            return;
        }
        sb.append("\t\t\t\t\t\tDIVISION 2 Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
        stgD2.op_instruction = instructionInfo;
        stgD3.ip_instruction = stgD2.op_instruction;
        stgD2.ip_instruction = null;
    }
    
    void division3()
    {
        Instruction_info instructionInfo = stgD3.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\t\t\t\tNo Instruction in DIVISION 3\n");
            return;
        }
        sb.append("\t\t\t\t\t\tDIVISION 3 Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
        stgD3.op_instruction = instructionInfo;
        stgD4.ip_instruction = stgD3.op_instruction;
        stgD3.ip_instruction = null;
    }
    
    void division4()
    {
        Instruction_info instructionInfo = stgD4.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\t\t\t\tNo Instruction in DIVISION 4\n");
            return;
        }
        sb.append("\t\t\t\t\t\tDIVISION 4 Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
        
        if(instructionInfo.opcode.equals("DIV"))
        {
            P[instructionInfo.IQ_dest_tag].value = instructionInfo.val_dest_reg;
            P[instructionInfo.IQ_dest_tag].valid = true;
        }   
        
        stgD4.op_instruction = instructionInfo;
        instructionInfo.ROB_status = true;
        instructionInfo.ROB_dest_val = instructionInfo.val_dest_reg;
        stgD4.ip_instruction = null;
    }
    
    void multiply1()
    {
        int temp_val = 0;
        Instruction_info instructionInfo = stgM1.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\tNo Instruction in MULTIPLY 1\n");
            return;
        }
        
        temp_val = instructionInfo.IQ_src1_value * instructionInfo.IQ_src2_value;
        instructionInfo.val_dest_reg = temp_val;
        
        if(!RFile.R[instructionInfo.addr_dest_reg].status)
        {
            RFile.R[instructionInfo.addr_dest_reg].status = true;
        }
        {
            sb.append("\t\t\tMULTIPLY 1 Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
            stgM1.op_instruction = instructionInfo;
            stgM2.ip_instruction = stgM1.op_instruction;
            stgM1.ip_instruction = null;
            stgM1.stalled = false;
        }
    }
    
    void multiply2()
    {
        Instruction_info instructionInfo = stgM2.ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("\t\t\tNo Instruction in MULTIPLY 2\n");
            return;
        }
        
        P[instructionInfo.IQ_dest_tag].value = instructionInfo.val_dest_reg;
        P[instructionInfo.IQ_dest_tag].valid = true;
        {
            sb.append("\t\t\tMULTIPLY 2 Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
            stgM2.op_instruction = instructionInfo;
            instructionInfo.ROB_dest_val = instructionInfo.val_dest_reg;
            instructionInfo.ROB_status = true;
            stgM2.ip_instruction = null;
            stgM2.stalled = false;
        }
    }
    
    void execute()
    {
        int temp_val = 0;
        Instruction_info instructionInfo = stg[2].ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("No Instruction in EXECUTE\n");
            return;
        }
        switch(instructionInfo.opcode){
            case("MOVC"):
                temp_val = temp_val + instructionInfo.literal;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                P[instructionInfo.IQ_dest_tag].valid = true;
                break;
            case("ADD"):
                temp_val = instructionInfo.IQ_src1_value + instructionInfo.IQ_src2_value;
                P[instructionInfo.IQ_dest_tag].valid = true;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                
                if(FBus.FI[instructionInfo.addr_dest_reg].value == 0)
                {
                    FI_PSW.isZero = true;
                    FI_PSW.Line = instructionInfo.lineNumber;
                }
                else
                {
                    FI_PSW.isZero = false;
                    FI_PSW.Line = instructionInfo.lineNumber;
                }
                break;
            case("SUB"):
                temp_val = instructionInfo.IQ_src1_value - instructionInfo.IQ_src2_value;
                P[instructionInfo.IQ_dest_tag].valid = true;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                if(FBus.FI[instructionInfo.addr_dest_reg].value == 0)
                {
                    FI_PSW.isZero = true;
                    FI_PSW.Line = instructionInfo.lineNumber;
                }
                else
                {
                    FI_PSW.isZero = false;
                    FI_PSW.Line = instructionInfo.lineNumber;
                }
                break;
            case("LOAD"):
                temp_val = instructionInfo.IQ_src1_value + instructionInfo.literal;
                break;
            case("STORE"):
                temp_val = instructionInfo.IQ_src2_value + instructionInfo.literal;
                break;
            case("BZ"):
                if(B1)
                {
                    if(temp_Zero)
                    {
                        temp_val = instructionInfo.PC + (instructionInfo.literal);
                        temp_index = (temp_val-4000)/4;
                        branch = true;
                        OneCycleWait = true;
                    }
                    B1 = false;
                }
                else if(flag.zero)
                {
                    temp_val = instructionInfo.PC + (instructionInfo.literal);
                    temp_index = (temp_val-4000)/4;
                    branch = true;
                    OneCycleWait = true;
                }
                break;
            case("BNZ"):
                if(B1)
                {
                    if(!temp_Zero)
                    {
                        temp_val = instructionInfo.PC + (instructionInfo.literal);
                        temp_index = (temp_val-4000)/4;
                        branch = true;
                        OneCycleWait = true;
                    }
                    B1 = false;
                }
                else if(!flag.zero)
                {
                    temp_val = instructionInfo.PC + (instructionInfo.literal);
                    temp_index = (temp_val-4000)/4;
                    branch = true;
                    OneCycleWait = true;
                }
                break;
            case("JAL"):
                temp_val = instructionInfo.IQ_src1_value + (instructionInfo.literal);
                temp_index = (temp_val-4000)/4;
                branch = true;
                OneCycleWait = true;
                break;
            case("JUMP"):
                instructionInfo.val_src_reg1 = RFile.R[instructionInfo.addr_src_reg1].value;
                temp_val = instructionInfo.val_src_reg1 + (instructionInfo.literal);
                temp_index = (temp_val-4000)/4;
                branch = true;
                OneCycleWait = true;
                break;
            case("HALT"):
                    halt = true;
                    stg[0].ip_instruction = null;
                    stg[1].ip_instruction = null;
                
                break;
            case("AND"):
                temp_val = instructionInfo.IQ_src1_value & instructionInfo.IQ_src2_value;
                P[instructionInfo.IQ_dest_tag].valid = true;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                break;
            case("OR"):
                temp_val = instructionInfo.IQ_src1_value | instructionInfo.IQ_src2_value;
                P[instructionInfo.IQ_dest_tag].valid = true;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                break;
            case("EX-OR"):
                temp_val = instructionInfo.IQ_src1_value ^ instructionInfo.IQ_src2_value;
                P[instructionInfo.IQ_dest_tag].valid = true;
                P[instructionInfo.IQ_dest_tag].value = temp_val;
                break;
        }
        instructionInfo.val_dest_reg = temp_val;
        {    
            sb.append("EXECUTE Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
            if(instructionInfo.opcode.equalsIgnoreCase("LOAD"))
            {
                instructionInfo.LSQ_status = true;
                instructionInfo.LSQ_LD_mem_addr = temp_val;
            }
            if(instructionInfo.opcode.equalsIgnoreCase("STORE"))
            {
                instructionInfo.LSQ_status = true;
                instructionInfo.LSQ_STR_mem_addr = temp_val;
            }
            if(!instructionInfo.opcode.equalsIgnoreCase("LOAD") && !instructionInfo.opcode.equalsIgnoreCase("STORE"))
            {
                instructionInfo.ROB_dest_val = temp_val;
                instructionInfo.ROB_status = true;
            }
            stg[2].ip_instruction = null;
            stg[2].stalled = false;
        }
    }
    
    void mem()
    {
        Instruction_info instructionInfo = stg[3].ip_instruction;
        if(instructionInfo == null)
        {
            sb.append("No Instruction in MEMORY\n");
            return;
        }
         switch(instructionInfo.opcode){
            case("MOVC"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                break;
            case("ADD"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("SUB"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("LOAD"):
                instructionInfo.tar_mem_data = dme.data_mem_address[instructionInfo.val_dest_reg];
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("STORE"):
                dme.data_mem_address[instructionInfo.val_dest_reg] = instructionInfo.IQ_src1_value;
                if(instructionInfo.MEM_stage_count ==2)
                sb1.append("Data Memory Value at "+instructionInfo.val_dest_reg+"th Location is :"+dme.data_mem_address[instructionInfo.val_dest_reg]+"\n");
                break;
            case("BZ"):
                break;
            case("BNZ"):
                break;
            case("JAL"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("JUMP"):
                break;
            case("HALT"):
                break;
            case("AND"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("OR"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("EX-OR"):
                if(FBus.FI[instructionInfo.addr_dest_reg].status)
                {
                    FBus.FI[instructionInfo.addr_dest_reg].status = false;
                }
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                break;
            case("MUL"):
                stg[0].stalled = false;
                stg[1].stalled = false;
                exeWait = false;
                break;
            case("DIV"):
                if(!RFile.R[instructionInfo.addr_dest_reg].status)
                {
                    RFile.R[instructionInfo.addr_dest_reg].status = true;
                }
                stg[0].stalled = false;
                stg[1].stalled = false;
                exeWait = false;
                mulWait = false;
                break;    
        }
        instructionInfo.MEM_stage_count++;
        sb.append("MEMORY Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.renamed_inst_string+"\n");
        if(instructionInfo.MEM_stage_count == 3)
        {   
            if(instructionInfo.opcode.equals("LOAD"))
            {
                P[instructionInfo.IQ_dest_tag].value = instructionInfo.val_dest_reg;
                P[instructionInfo.IQ_dest_tag].valid = true;
            }
            instructionInfo.ROB_status = true;
            stg[3].ip_instruction = null;
        }
    }
    
    void writeback()
    {
        Instruction_info instructionInfo = stg[4].ip_instruction;
        if(instructionInfo == null)
        {
            System.out.println("No Instruction in WRITEBACK");
            sb.append("No Instruction in WRITEBACK\n");
            return;
        }      
         switch(instructionInfo.opcode){
            case("MOVC"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("ADD"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("SUB"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("LOAD"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.tar_mem_data;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;    
            case("STORE"):
                break;    
            case("BZ"):
                break;    
            case("BNZ"):
                break;
            case("JAL"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.PC + 4;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("JUMP"):
                break;    
            case("HALT"):
                break;    
            case("AND"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;    
            case("OR"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("EX-OR"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }
                break;
            case("DIV"):    
            case("MUL"):
                RFile.R[instructionInfo.addr_dest_reg].value = instructionInfo.val_dest_reg;
                RFile.R[instructionInfo.addr_dest_reg].status = false;
                if(!RFile.R[instructionInfo.addr_src_reg1].status && !RFile.R[instructionInfo.addr_src_reg2].status)
                {
                    stg[1].stalled = false;
                    stg[0].stalled = false;
                }    
                break;
        }
         stg[0].stalled = false;
         stg[1].stalled = false;
         System.out.println("WRITEBACK Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.inst_string);
         sb.append("WRITEBACK Stage :I"+instructionInfo.lineNumber+": "+instructionInfo.inst_string+"\n");
         if(instructionInfo.opcode.equals("ADD")||
               instructionInfo.opcode.equals("SUB")||
               instructionInfo.opcode.equals("MUL")||
               instructionInfo.opcode.equals("DIV"))
            {
                R_PSW.Line = instructionInfo.lineNumber;
                if(instructionInfo.val_dest_reg == 0)
                {
                    R_PSW.isZero = true;
                    
                }
                else
                {
                    R_PSW.isZero = false;
                    
                }    
            }
        stg[4].ip_instruction = null;
    }
    
    void display_RAT()
    {
        if(RAT.isEmpty())
        {
            sb.append("RENAME Table: <EMPTY>\n");
        }
        else
        {
            sb.append("RENAME Table: \n");
            for(int i=0;i<RAT.size();i++)
            {
                sb.append("\t\t"+RAT.get(i)+"\n");
            }
        }
    }
    
    void simulate(int m)
    {
        stat = new Stats();
        stat.cycle = 0;
            while(true)
            {
                //writeback();
                mem();
                division4();
                division3();
                division2();
                division1();
                multiply2();
                multiply1();
                execute();
                lsq();
                commit();
                rob();
                isq();
                display_RAT();
                decode();
                fetch();
                stat.cycle++;
                sb.append("End of Cycle: "+stat.cycle+"\n\n");
                System.out.println();
                if(stat.cycle==14)
                    System.out.println();
                if(stat.cycle == m)  //endOfCYcle
                  break;
            }
    }
        
    void initialize(String Fname)
    {
        try
        {
            int j=0;
            int baseAddress = 4000;
            
            Code_Line cl[] = new Code_Line[100];
            Register register[] = new Register[16];
            Register FIregister[] = new Register[16];
            Register FMregister[] = new Register[16];
            Register FDregister[] = new Register[16];
            
            // Open the file
            FileInputStream fstream = new FileInputStream(Fname);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)
            {
                Code_Line line = new Code_Line();
                line.Inst_String = strLine;
                line.address = baseAddress;
                line.line_no = lnNumber;
                cl[lnNumber-1] = line;
                baseAddress = baseAddress + 4;
                System.out.println(lnNumber+":"+baseAddress+":"+strLine);
                String[] splitted = strLine.split(",");
                lnNumber++;
                j++;
            }
            System.out.println(lnNumber-1+" lines of file");
            System.out.println(baseAddress);
            cme = new Code_Memory(cl);
            int mem_add[] = new int[4000];
            dme = new Data_Memory(mem_add);
            flag = new Flags();
            Flags flg = new Flags();
            flg.carry=false;
            flg.negative=false;
            flg.zero=false;
            for(int i=0; i<register.length ; i++)
            {
                register[i] = new Register();
                register[i].status = false;
                register[i].value = 0;
                FIregister[i] = new Register();
                FIregister[i].status = false;
                FIregister[i].value = 0;
                FMregister[i] = new Register();
                FMregister[i].status = false;
                FMregister[i].value = 0;
                FDregister[i] = new Register();
                FDregister[i].status = false;
                FDregister[i].value = 0;
            }
            RFile = new Register_File(register);
            FBus = new Forward_Bus(FIregister, FMregister, FDregister);
            System.out.println(cl[1].address);
            for(int i=0;i<P.length;i++)
            {
                P[i] = new P_Register();
                P[i].status = false;
            }
            for(int i=0; i<RFile.R.length ; i++)
            {
                System.out.println("R[" + i + "] = " + RFile.R[i].value + ", Status= " + RFile.R[i].status);
            }
            i=0;
            while(cme.c1[i] != null)
            {
                System.out.println("Code Memory: " + cme.c1[i].Inst_String);
                i++;
            }
            in.close();
        }
        catch (Exception e)
        {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    void display()
    {
        System.out.println("DISPLAY FUNCTION");
        System.out.println(sb);
        System.out.println("REGISTER VALUES");
        System.out.println("Program Counter Value :"+temp_PC);
        System.out.println(sb1);
    }
    
    int next_Avail_P_Reg()
    {
        for(int i = 0;i<P.length;i++)
        {
            if(!P[i].status)
            {
              P[i].status = true;
              return i;
            }
            else
              continue;  
        }
        if(i>=31)
        {
            free_P_Reg();
        }
        return i;
    }
    
    void free_P_Reg()
    {
        for(int i = 0;i<P.length;i++)
        {
            if(P[i].valid)
            {
                P[i].valid = false;
                if(P[i].status)
                {
                    P[i].status = false;
                }
            }
        }
    }
}