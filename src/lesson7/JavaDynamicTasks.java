package lesson7;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.max;

@SuppressWarnings("unused")
public class JavaDynamicTasks {
    /**
     * Наибольшая общая подпоследовательность.
     * Средняя
     *
     * Дано две строки, например "nematode knowledge" и "empty bottle".
     * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
     * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
     * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
     * Если общей подпоследовательности нет, вернуть пустую строку.
     * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
     * При сравнении подстрок, регистр символов *имеет* значение.
     */
    //T = O(A * B), где A - длина первой строки, B - длина второй строки
    //R = O(A * B), где A - длина первой строки, B - длина второй строки
    public static String longestCommonSubSequence(@NotNull String first, @NotNull String second) {
        if (first.equals(second)) return first;
        StringBuilder sb = new StringBuilder();
        int fl = first.length(), firstCount = fl, sl = second.length(), secondCount = sl;
        int[][] strings = new int[fl + 1][sl + 1];

        for (int i = 1; i <= fl; i++) {
            for (int j = 1; j <= sl; j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) strings[i][j] = strings[i - 1][j - 1] + 1;
                else strings[i][j] = Math.max(strings[i - 1][j], strings[i][j - 1]);
            }
        }
        while (firstCount > 0 && secondCount > 0) {
            if (strings[firstCount][secondCount - 1] == strings[firstCount][secondCount]) firstCount++;
            else if (strings[firstCount][secondCount] == strings[firstCount - 1][secondCount]) secondCount++;
            else sb.append(first.charAt(firstCount - 1));
            firstCount--;
            secondCount--;
        }
        return sb.reverse().toString();
    }

    /**
     * Наибольшая возрастающая подпоследовательность
     * Сложная
     *
     * Дан список целых чисел, например, [2 8 5 9 12 6].
     * Найти в нём самую длинную возрастающую подпоследовательность.
     * Элементы подпоследовательности не обязаны идти подряд,
     * но должны быть расположены в исходном списке в том же порядке.
     * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
     * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
     * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
     */
    //T = O(N*Log(N))
    //R = O(N)
    @NotNull
    public static List<Integer> longestIncreasingSubSequence(@NotNull List<Integer> list) {
        if (list.isEmpty()) return list;

        int[] prev = new int[list.size()];
        int[] positionsInList = new int[list.size() + 1];
        int[] numbers = new int[list.size() + 1];
        int len = 0;

        positionsInList[0] = -1;
        Arrays.fill(numbers, Integer.MIN_VALUE);
        numbers[0] = Integer.MAX_VALUE;

        for (int i = list.size() - 1; i >= 0; i--) {
            int a = 0;
            int b = list.size();
            while (b - a > 1) {
                int c = (a + b) / 2;
                if (list.get(i) < numbers[c]) a = c;
                else b = c;
            }
            if (numbers[b] < list.get(i) && numbers[b - 1] > list.get(i)) {
                len = max(len, b);
                numbers[b] = list.get(i);
                positionsInList[b] = i;
                prev[i] = positionsInList[b - 1];
            }
        }
        if (len == 1) return List.of(list.get(0));
        int p = positionsInList[len];
        List<Integer> subSequenceToReturn = new LinkedList<>();
        while (p != -1) {
            subSequenceToReturn.add(list.get(p));
            p = prev[p];
        }
        return subSequenceToReturn;
    }


    /**
     * Самый короткий маршрут на прямоугольном поле.
     * Средняя
     *
     * В файле с именем inputName задано прямоугольное поле:
     *
     * 0 2 3 2 4 1
     * 1 5 3 4 6 2
     * 2 6 2 5 1 3
     * 1 4 3 2 6 2
     * 4 2 3 1 5 0
     *
     * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
     * В каждой клетке записано некоторое натуральное число или нуль.
     * Необходимо попасть из верхней левой клетки в правую нижнюю.
     * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
     * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
     *
     * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
     */
    public static int shortestPathOnField(String inputName) {
        throw new NotImplementedError();
    }
}
