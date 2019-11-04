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
class Forward_Bus
{
    Register FI[];
    Register FM[];
    Register FD[];
    Forward_Bus(Register FI[], Register FM[],Register FD[])
    {
        this.FI = FI;
        this.FM = FM;
        this.FD = FD;
    }
}
