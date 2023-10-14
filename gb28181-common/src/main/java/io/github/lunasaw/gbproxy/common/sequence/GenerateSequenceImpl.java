package io.github.lunasaw.gbproxy.common.sequence;

import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;

/**
 * @author luna
 * @date 2023/10/13
 */
public class GenerateSequenceImpl implements GenerateSequence {

    public static long getSequence() {
        long timestamp = System.currentTimeMillis();
        return (timestamp & 0x3FFF) % Integer.MAX_VALUE;
    }

    @Override
    public Long generateSequence() {
        return getSequence();
    }


    @SneakyThrows
    public static void main(String[] args) {

        Set<Long> list = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(1);
            long sequence = getSequence();
            System.out.println(sequence);
            list.add(sequence);
        }

        System.out.println(list.size());
    }
}
