/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apexsimulator;

/**
 *
 * @author Kaushal
 */
class Instruction_info
{
    int PC;
    String inst_string;
    int val_src_reg1;
    int addr_src_reg1;
    int val_src_reg2;
    int addr_src_reg2;
    int val_dest_reg;
    int addr_dest_reg;
    int tar_mem_addr;
    int tar_mem_data;
    String opcode;
    int literal;
    int lineNumber;
    boolean IQ_status;
    String IQ_FU;
    int IQ_literal;
    boolean IQ_src1_ready;
    boolean IQ_src2_ready;
    int IQ_src1_tag;
    int IQ_src2_tag;
    int IQ_dest_tag;
    int IQ_src1_value;
    int IQ_src2_value;
    boolean ROB_status;
    int ROB_dest_val;
    boolean LSQ_status;
    int LSQ_LD_mem_addr;
    int LSQ_STR_mem_addr;
    int MEM_stage_count = 0;
    String renamed_inst_string;
}

