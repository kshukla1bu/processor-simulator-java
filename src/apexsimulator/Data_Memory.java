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
class Data_Memory
{
    int base_address;
    int data_mem_address[];
    Data_Memory(int data_mem_address[])
    {
        this.data_mem_address = data_mem_address;
    }   
}