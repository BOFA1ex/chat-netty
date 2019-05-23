package com.bofa.client.util;

import io.netty.util.CharsetUtil;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.util
 * @date 2019/4/24
 */
public class StreamUtils {

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    static List<String> strs = Arrays.asList("bofa", "yulu", "yuli", "ofa1ex");

    public static void main(String[] args) {

        byte[] bytes = "++nb".getBytes(CharsetUtil.UTF_8);
        Map<String, Integer> map = asStream(
                Arrays.asList(
                        new Entity("bofa", 22),
                        new Entity("yuli", 32),
                        new Entity("ch", 29)
                ).iterator(), true)
                .filter(entity -> {
                    for (String str : strs) {
                        if (entity.getName().equals(str)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toMap(
                        Entity::getName, Entity::getAge
                        )
                );

        map.forEach((k, v) -> System.out.println("key: " + k + " value: " + v));
    }

    static boolean isExists(Entity entity) {
        for (String str : strs) {
            if (entity.getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Data
    static class Entity {

        List<String> args = Arrays.asList("bofa", "yulu", "yuli", "ofa1ex");

        public Entity(String name, int age) {
            this.name = name;
            this.age = age;
        }

        boolean isGreatThan20() {
            return this.age > 25;
        }

        boolean isBofa() {
            for (String arg : args) {
                if (arg.equals(name)) {
                    return true;
                }
            }
            return false;
        }

        String name;

        int age;
    }
}
