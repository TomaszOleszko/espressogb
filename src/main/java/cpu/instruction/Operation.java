package cpu.instruction;

import cpu.Context;
import cpu.Registers;
import memory.AddressSpace;

public interface Operation {
    int execute(Registers registers, AddressSpace addressSpace, int accumulator, Context context);
}
