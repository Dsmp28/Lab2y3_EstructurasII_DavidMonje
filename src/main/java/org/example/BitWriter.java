package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitWriter {
    private final ByteArrayOutputStream outputStream;
    private int currentByte;
    private int bitPosition;

    public BitWriter() {
        this.outputStream = new ByteArrayOutputStream();
        this.currentByte = 0;
        this.bitPosition = 7;
    }

    public void writeBit(boolean bit) throws IOException {
        if (bit) {
            currentByte |= (1 << bitPosition);
        }

        bitPosition--;

        if (bitPosition < 0) {
            outputStream.write(currentByte);
            currentByte = 0;
            bitPosition = 7;
        }
    }

    public void flush() throws IOException {
        if (bitPosition != 7) {
            outputStream.write(currentByte);
        }
    }

    public byte[] getOutput() {
        return outputStream.toByteArray();
    }
}
