package io.spring.start.site.support;

import io.spring.start.site.support.utils.IpUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MongoDB ObjectId
 * <p>
 * 12345678(time)_12345678(ip)_1234(tid)_1234(counter)
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.5.4 2022/7/22
 */
public final class ObjectId {

    private static final int LOW_ORDER_TWO_BYTES = 0x0000ffff;
    private static final int MACHINE_IDENTIFIER = createMachineIdentifier();
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private final int timestamp;
    private final int machineIdentifier;
    private final int counter;

    public static String getId() {
        return new ObjectId().toString();
    }

    public ObjectId() {
        this(new Date());
    }

    public ObjectId(Date date) {
        this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
    }

    private ObjectId(int timestamp,
                     int machineIdentifier,
                     int counter,
                     boolean checkCounter) {
        if (checkCounter && ((counter & 0xffff0000) != 0)) {
            throw new IllegalArgumentException("The counter must be between 0 and 32767 (it must fit in 2 bytes).");
        }
        this.timestamp = timestamp;
        this.machineIdentifier = machineIdentifier;
        this.counter = counter & LOW_ORDER_TWO_BYTES;
    }

    @Override
    public String toString() {
        return toHexString();
    }

    public String toHexString() {
        char[] chars = new char[24];
        int i = 0;
        for (byte b : toByteArray()) {
            chars[i++] = HEX_CHARS[b >> 4 & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        putToByteBuffer(buffer);
        return buffer.array();
    }

    public void putToByteBuffer(ByteBuffer buffer) {
        buffer.put(int3(timestamp));
        buffer.put(int2(timestamp));
        buffer.put(int1(timestamp));
        buffer.put(int0(timestamp));
        buffer.put(int3(machineIdentifier));
        buffer.put(int2(machineIdentifier));
        buffer.put(int1(machineIdentifier));
        buffer.put(int0(machineIdentifier));
        buffer.put(short1(getThreadId()));
        buffer.put(short0(getThreadId()));
        buffer.put(int1(counter));
        buffer.put(int0(counter));
    }

    private short getThreadId() {
        return (short) Thread.currentThread().getId();
    }

    private static int createMachineIdentifier() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException t) {
            ip = IpUtils.randomIp();
        }
        return IpUtils.ip2Int(ip);
    }

    private static int dateToTimestampSeconds(Date time) {
        return (int) (time.getTime() / 1000);
    }

    private static byte int3(int x) {
        return (byte) (x >> 24);
    }

    private static byte int2(int x) {
        return (byte) (x >> 16);
    }

    private static byte int1(int x) {
        return (byte) (x >> 8);
    }

    private static byte int0(int x) {
        return (byte) (x);
    }

    private static byte short1(short x) {
        return (byte) (x >> 8);
    }

    private static byte short0(short x) {
        return (byte) (x);
    }
}