package knapsackproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */
public final class KnapsackProblem {

    public static void main(final String[] args) {
        final double[] c = {3, 1, 4, 5, 6};//{1.0, 6.0, 4.0, 7.0, 6.0}; // стоимость предмета
        final double[] a = {2, 4, 7, 8, 5};//{3.0, 4.0, 5.0, 8.0, 9.0}; // вес предмета
        final double p = 10.0;//13.0; // вместимость рюкзака
        KnapsackProblem knapsack = new KnapsackProblem(c, a, p);
        Solution solution = knapsack.build();
        System.out.println("Task:\n" + task(c, a, p));
        System.out.println("Ranking:\n" + rangToString(knapsack.sequence, c, a));
        System.out.println("Solution:");
        System.out.println("X = " + solutionToString(solution.getX()));
        System.out.println("ξ = " + solution.getKsi());
    }
    
    public static String task(final double[] cost, final double[] weight, final double maxWeight) {
        StringBuilder task = new StringBuilder();
        String costString = arrayToProduct(cost);
        String weightString = arrayToProduct(weight);
        
        task.append(costString).append(" -> max\n")
                .append(weightString).append(" <= ").append(maxWeight).append("\n");
        return task.toString();
    }
    
    public static String arrayToProduct(final double[] arr) {
        return IntStream.rangeClosed(1, arr.length)
                .mapToObj(i -> Integer.toString((int)arr[i-1]) + "∙x" + Integer.toString(i))
                .collect(Collectors.joining(" + "));
    }

    public static String solutionToString(int[] arr) {
        return Arrays.stream(arr)
                .mapToObj(x -> Integer.toString(x))
                .collect(Collectors.joining(", ", "(", ")"));
    }
    
    public static String rangToString(int[] sequence, final double[] cost, final double[] weight) {
        StringBuilder sb = new StringBuilder();
        String calcRang = Arrays.stream(sequence)
                .mapToObj(i -> Integer.toString((int)cost[i]) + "/" + Integer.toString((int)weight[i]))
                .collect(Collectors.joining(" > ","","\n"));
        String x = Arrays.stream(sequence)
                .mapToObj(i -> "x" + Integer.toString((int)(i+1)))
                .collect(Collectors.joining("    "));
        sb.append(calcRang).append(x).append("\n");
        return sb.toString();
    }

    private final double[] cost;
    private final double[] weight;
    private final double limitWeight;
    private final int[] sequence; // порядок индексов после ранжировки
    private final int N;

    public KnapsackProblem(final double[] cost, final double[] weight, final double maxWeight) {
        this.cost = cost;
        this.weight = weight;
        this.limitWeight = maxWeight;
        this.N = cost.length;
        this.sequence = ranging();
    }

    public Solution build() {
        final List<Solution> solutions = new ArrayList<>();
        int[] newX = new int[N];
        Arrays.fill(newX, -1);
        solutions.add(new Solution(newX));
        Solution sol = getRecord(solutions);

        while (isThereOtherSolutions(solutions, sol)) {
            final Solution nodeSol = solutions.stream()
                    .filter(s -> s.getTaken() != -1)
                    .min((s1, s2) -> Double.compare(s1.getKsi(), s2.getKsi()))
                    .get();

            newX = nodeSol.getX().clone();
            newX[nodeSol.getTaken()] = 0;
            final Solution ps1 = new Solution(newX);
            addCorrectSolution(solutions, ps1);

            newX = nodeSol.getX().clone();
            newX[nodeSol.getTaken()] = 1;
            final Solution ps2 = new Solution(newX);
            addCorrectSolution(solutions, ps2);

            solutions.remove(nodeSol);

            sol = getRecord(solutions);
        }

        return sol;
    }

    private void addCorrectSolution(final List<Solution> solutions, Solution possibleSolution) {
        if (possibleSolution.isCorrectSolution()) {
            solutions.add(possibleSolution);
        }
    }

    private boolean isThereOtherSolutions(final List<Solution> solutions, final Solution record) {
        if (record == null) {
            return true;
        }
        return solutions.stream()
                .filter(s -> s.getKsi() > record.getKsi())
                .count() > 0;
    }

    private Solution getRecord(final List<Solution> possibleSolutions) {
        return possibleSolutions.stream()
                .filter(s -> s.getTaken() == -1)
                .max((s1, s2) -> Double.compare(s1.getKsi(), s2.getKsi()))
                .orElse(null);
    }

    /**
     * Ранжирование элементов.
     *
     * @return массив значений
     */
    private int[] ranging() {
        final double[] ratio = new double[N];
        final int[] result = new int[N];

        // инициализация
        for (int i = 0; i < N; i++) {
            ratio[i] = cost[i] / weight[i];
            result[i] = -1;
        }

        for (int i = 0; i < N; i++) {
            double max = Double.NEGATIVE_INFINITY;
            int index = -1;
            for (int j = 0; j < N; j++) {
                if (ratio[j] > max && !isChosen(result, j, i)) {
                    max = ratio[j];
                    index = j;
                }
            }
            result[i] = index;
        }

        return result;
    }

    
    
    /**
     * Проверка, была ли эта позиция уже просмотрена
     *
     * @param arr результирующий массив с новыми позициями
     * @param index номер рассматриваемой позиции
     * @param maxIndex предельный индекс
     * @return да - если был рассмотрен, нет - иначе
     */
    private boolean isChosen(final int[] arr, final int index, final int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            if (arr[i] == index) {
                return true;
            }
        }
        return false;
    }

    public int[] getSequence() {
        return sequence;
    }

    public final class Solution {

        private final int[] x;
        private boolean correctSolution;
        private double ksi;
        private int taken = -1; //индекс взятого предмета

        public Solution(final int[] x) {
            this.x = x;
            correctSolution = true;
            calcKsiAndCurrObj();
        }

        private void calcKsiAndCurrObj() {
            double maxWeight = recalcMaxWeight();

            if (maxWeight < 0) {
                correctSolution = false;
                return;
            }

            int indexOfNextTaken = 0;
            while (maxWeight > 0 && indexOfNextTaken < N) {
                final int index = sequence[indexOfNextTaken];
                if (x[index] == -1) {
                    maxWeight -= weight[index];
                    x[index] = 1;
                }
                indexOfNextTaken++;
            }

            if (maxWeight < 0) 
                taken = sequence[indexOfNextTaken - 1];
            else if (maxWeight > 0)
                replaceToNull();
            
            for (int i = 0; i < N; i++) {
                if (i == taken && maxWeight < 0) {
                    ksi += (1.0 + maxWeight / weight[i]) * cost[i];
                } else if (x[i] == 1) {
                    ksi += cost[i];
                }
            }
        }

        double recalcMaxWeight() {
            double maxWeight = limitWeight;
            for (int i = 0; i < N; i++) {
                if (x[i] == 1) {
                    maxWeight -= weight[i];
                }
            }

            return maxWeight;
        }

        void replaceToNull() {
            for (int k = 0; k < N; k++) {
                if (x[k] == -1) {
                    x[k] = 0;
                }
            }
        }

        public double getKsi() { return this.ksi; }

        public int[] getX() { return this.x; }

        public int getTaken() { return this.taken; }

        public boolean isCorrectSolution() { return correctSolution; }
    }
}
