package com.exploration.asela.sales.specification;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class SherlockCost {


    private static int TEST_COUNT = Integer.MAX_VALUE;


    private int getValue(String[] binary, int[] array, int j) {
        int i = array[j] * Integer.parseInt(binary[j]);
        return i == 0 ? 1 : i;

    }

    @Test
    public void bruteForceAlgorithm() throws IOException {

        TEST_COUNT = 2;

        test("sherlock", "regex:.*simple-case-1.txt", array -> {
            int s = 0;
            int max = 0;
            String[] maxArray = new String[0];
            for (int i = 0; i < 1 << array.length; i++) {
                String[] binary = String.format("%" + array.length + "s", Integer.toBinaryString(i)).replaceAll(" ", "0").split("");
                //    System.out.println(Arrays.toString(binary));

                s = 0;
                for (int j = 1; j < binary.length; j++) {
                    s += abs(getValue(binary, array, j - 1) - getValue(binary, array, j));
                }

                if (s > max) {
                    max = s;
                    maxArray = binary;
                }

            }


            final String[] solution = maxArray;
            int[] answer = new int[array.length];
            int[] diff = new int[array.length];
            IntStream.range(0, answer.length).forEach(i -> {
                answer[i] = ((solution[i].equals("0")) ? 1 : array[i]);
                if (i > 0) {
                    diff[i] = abs(answer[i] - answer[i - 1]);
                }
            });

            System.out.println(max);
            System.out.printf("%15s=%s%n", "Original", Arrays.toString(array));
            System.out.printf("%15s=%s%n", "Binary", Arrays.toString(maxArray));
            System.out.printf("%15s=%s%n", "Answer", Arrays.toString(answer));
            System.out.printf("%15s=%s%n", "Diff", Arrays.toString(diff));
        });


    }

    @Test
    public void recursive() throws IOException {

        TEST_COUNT = 1;
        test("sherlock", "regex:.*simple-case-1.txt", array -> {
            System.out.println("Arrays.toString(array) = " + Arrays.toString(array));
            System.out.println("sherlockR(array, 0) = " + sherlockR(array, 0));
        });

    }


    private int tab = 0;
    private int sherlockR(int[] array, int start) {

        tab+=2;

        int result = sherlockRLogic(array, start);

        System.out.printf("%"+tab+"s%s%n","","array = [" + Arrays.toString(array) + "], start = [" + start + "]");
        System.out.printf("%"+tab+"s%s%n","","result = " + result);


        tab-=2;
        return result;
    }

    private int sherlockRLogic(int[] array, int start) {
        if(start == array.length - 1) return  0;

        int a = array[start];
        array[start] = 1;
        int a1 = sherlockR(array, start + 1);
        array[start] = a;
        int ab = sherlockR(array, start + 1);

        int b = array[start+1];
        return IntStream.of( b - 1 + a1).max().getAsInt();
    }

    private void test(String folder, String regex, Consumer<int[]> consumer) throws IOException {
        operateOnPath(folder, regex, path -> {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

                int noTests = Integer.parseInt(reader.readLine());
                System.out.println("noTests = " + noTests);
                for (int i = 0; i < Integer.min(TEST_COUNT, noTests); i++) {
                    Instant start = Instant.now();
                    int noElements = Integer.parseInt(reader.readLine());
                    int[] elements = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                    consumer.accept(elements);
                    System.out.printf("Test Case %s, Time Taken = %s%n", i, Duration.between(start, Instant.now()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void operateOnPath(String folder, String regex, Consumer<? super Path> consumer) throws IOException {
        URI root = Paths.get(".").toUri();
        System.out.println("root = " + root);
        URI testResources = root.resolve("./src/test/resources");
        System.out.println("testResources = " + testResources);

        Path sherlockInputFiles = Paths.get(testResources.resolve("resources/" + folder));
        DirectoryStream<Path> dirStream = Files.newDirectoryStream(sherlockInputFiles);

        PathMatcher matcher = sherlockInputFiles.getFileSystem().getPathMatcher(regex);


        StreamSupport.stream(dirStream.spliterator(), false)
                // .peek(System.out::println)
                .filter(matcher::matches)
                .forEach(consumer);
    }

}