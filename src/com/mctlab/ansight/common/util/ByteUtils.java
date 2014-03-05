package com.mctlab.ansight.common.util;

public class ByteUtils {

    public static long byteToLong(byte[] b) {
        return (((long) b[7] << 56) + ((long) (b[6] & 255) << 48)
                + ((long) (b[5] & 255) << 40) + ((long) (b[4] & 255) << 32)
                + ((long) (b[3] & 255) << 24) + ((b[2] & 255) << 16)
                + ((b[1] & 255) << 8) + ((b[0] & 255) << 0));
    }

    public static byte[] longToByte(long v) {
        byte[] b = new byte[8];
        b[0] = (byte) (v);
        b[1] = (byte) (v >>> 8);
        b[2] = (byte) (v >>> 16);
        b[3] = (byte) (v >>> 24);
        b[4] = (byte) (v >>> 32);
        b[5] = (byte) (v >>> 40);
        b[6] = (byte) (v >>> 48);
        b[7] = (byte) (v >>> 56);
        return b;
    }

    public static int byteToInt(byte[] b, int start) {
        return (((b[start] & 0xff)) + ((b[start + 1] & 0xff) << 8) + ((b[start + 2] & 0xff) << 16) + ((b[start + 3] & 0xff) << 24));
    }

    public static String byteToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte t : b) {
            sb.append(String.format("%02x", t));
        }
        return sb.toString();
    }

    public static byte[] intToByte(int v) {
        byte[] b = new byte[4];
        b[0] = (byte) (v);
        b[1] = (byte) (v >>> 8);
        b[2] = (byte) (v >>> 16);
        b[3] = (byte) (v >>> 24);
        return b;
    }

    public static float byteToFloat(byte[] b) {
        int n = byteToInt(b, 0);
        return Float.intBitsToFloat(n);
    }

    public static byte[] floatToByte(float f) {
        int n = Float.floatToIntBits(f);
        return intToByte(n);
    }

    public static void writeInt(byte[] buffer, int offset, int value) {
        if (buffer.length < offset + 4) {
            throw new ArrayIndexOutOfBoundsException("buffer overflow");
        }
        buffer[offset] = (byte) (value >>> 0);
        buffer[offset + 1] = (byte) (value >>> 8);
        buffer[offset + 2] = (byte) (value >>> 16);
        buffer[offset + 3] = (byte) (value >>> 24);
    }

    public static void writeLong(byte[] buffer, int offset, long value) {
        if (buffer.length < offset + 8) {
            throw new ArrayIndexOutOfBoundsException("buffer overflow");
        }
        buffer[offset] = (byte) (value >>> 0);
        buffer[offset + 1] = (byte) (value >>> 8);
        buffer[offset + 2] = (byte) (value >>> 16);
        buffer[offset + 3] = (byte) (value >>> 24);
        buffer[offset + 4] = (byte) (value >>> 32);
        buffer[offset + 5] = (byte) (value >>> 40);
        buffer[offset + 6] = (byte) (value >>> 48);
        buffer[offset + 7] = (byte) (value >>> 56);
    }

    public static int readInt(byte[] buffer, int offset) {
        if (buffer.length < offset + 4) {
            throw new ArrayIndexOutOfBoundsException("buffer overflow");
        }
        return (((buffer[offset] & 0xff))
                + ((buffer[offset + 1] & 0xff) << 8)
                + ((buffer[offset + 2] & 0xff) << 16)
                + ((buffer[offset + 3] & 0xff) << 24));
    }

    public static long readLong(byte[] buffer, int offset) {
        if (buffer.length < offset + 8) {
            throw new ArrayIndexOutOfBoundsException("buffer overflow");
        }
        return (((long) buffer[offset + 7] << 56)
                + ((long) (buffer[offset + 6] & 255) << 48)
                + ((long) (buffer[offset + 5] & 255) << 40)
                + ((long) (buffer[offset + 4] & 255) << 32)
                + ((long) (buffer[offset + 3] & 255) << 24)
                + ((buffer[offset + 2] & 255) << 16)
                + ((buffer[offset + 1] & 255) << 8)
                + (buffer[offset] & 255));
    }

}
