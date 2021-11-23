package cpu;

import cpu.instructions.Instruction;
import cpu.instructions.Instructions;
import memory.AddressSpace;
import memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class WordLoadInstructionsTest {

    Registers registers;
    AddressSpace addressSpace;

    @BeforeEach
    void init() {
        registers = new Registers();
        addressSpace = new Memory(0xFFFF);
    }

    @Test
    void testLoadBC_immediateWord_0x01() {
        registers.setPC(0x2000);
        addressSpace.set(0x2001, 0x99);
        addressSpace.set(0x2002, 0xA0);

        Instruction instr = Instructions.get(0x01);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0xA099, registers.getBC());
        assertEquals(0x2002, registers.getPC());
    }

    @Test
    void testLoadSP_immediateWord_0x31() {
        registers.setPC(0x2000);
        addressSpace.set(0x2001, 0x99);
        addressSpace.set(0x2002, 0xA0);

        Instruction instr = Instructions.get(0x31);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0xA099, registers.getSP());
        assertEquals(0x2002, registers.getPC());
    }

    @Test
    void testLoadSP_HL_0xF9() {
        registers.setPC(0x2000);
        registers.setSP(0x2001);
        registers.setHL(0x1111);

        Instruction instr = Instructions.get(0xF9);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(2, instr.getCycles());
        assertEquals(0x1111, registers.getSP());
        assertEquals(0x2000, registers.getPC());
    }

    @Test
    void testLoadHL_SPplusN_bothCarrySet_0xF8() {
        registers.setPC(0x2000);
        registers.setSP(0x00FF);
        registers.setHL(0x1111);
        addressSpace.set(0x2001, 0x01);

        Instruction instr = Instructions.get(0xF8);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0x00FF, registers.getSP());
        assertEquals(0x2001, registers.getPC());
        assertEquals(0x0100, registers.getHL());
        assertTrue(registers.getFlags().isHFlag());
        assertTrue(registers.getFlags().isCFlag());
        assertFalse(registers.getFlags().isZFlag());
        assertFalse(registers.getFlags().isNFlag());
    }

    @Test
    void testLoadHL_SPplusN_halfCarrySet_0xF8() {
        registers.setPC(0x2000);
        registers.setSP(0x001F);
        registers.setHL(0x1111);
        addressSpace.set(0x2001, 0x01);

        Instruction instr = Instructions.get(0xF8);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0x001F, registers.getSP());
        assertEquals(0x2001, registers.getPC());
        assertEquals(0x0020, registers.getHL());
        assertTrue(registers.getFlags().isHFlag());
        assertFalse(registers.getFlags().isCFlag());
        assertFalse(registers.getFlags().isZFlag());
        assertFalse(registers.getFlags().isNFlag());
    }

    @Test
    void testLoadHL_SPplusN_noFlagSet_0xF8() {
        registers.setPC(0x2000);
        registers.setSP(0x001E);
        registers.setHL(0x1111);
        addressSpace.set(0x2001, 0x01);

        Instruction instr = Instructions.get(0xF8);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0x001E, registers.getSP());
        assertEquals(0x2001, registers.getPC());
        assertEquals(0x001F, registers.getHL());
        assertEquals(0, registers.getFlags().getByte());
    }

    @Test
    void testLoadImmediateAddress_SP_0x08() {
        registers.setPC(0x2000);
        registers.setSP(0xAB1F);
        addressSpace.set(0x2001, 0x11);
        addressSpace.set(0x2002, 0xFC);

        Instruction instr = Instructions.get(0x08);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(5, instr.getCycles());
        assertEquals(0xAB1F, registers.getSP());
        assertEquals(0x2002, registers.getPC());
        assertEquals(0x1F, addressSpace.get(0xFC11));
        assertEquals(0xAB, addressSpace.get(0xFC12));
    }

    @Test
    void testPush_AF_0xF5() {
        registers.setPC(0x2000);
        registers.setSP(0x221F);
        registers.setAF(0x1234);

        Instruction instr = Instructions.get(0xF5);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(4, instr.getCycles());
        assertEquals(0x221D, registers.getSP());
        assertEquals(0x2000, registers.getPC());
        assertEquals(0x12, addressSpace.get(0x221E));
        assertEquals(0x34, addressSpace.get(0x221D));
    }

    @Test
    void testPop_AF_0xF1() {
        registers.setPC(0x2000);
        registers.setSP(0x221D);
        registers.setAF(0xFFFF);
        addressSpace.set(0x221E, 0x12);
        addressSpace.set(0x221D, 0x34);

        Instruction instr = Instructions.get(0xF1);

        int accumulator = 0;
        for (var operation : instr.getOperations()) {
            accumulator = operation.execute(registers, addressSpace, accumulator);
        }
        assertEquals(3, instr.getCycles());
        assertEquals(0x221F, registers.getSP());
        assertEquals(0x2000, registers.getPC());
        assertEquals(0x1234, registers.getAF());
    }

}