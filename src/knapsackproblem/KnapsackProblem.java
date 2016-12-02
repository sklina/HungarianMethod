/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knapsackproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */

public final class KnapsackProblem {

    private static final double EPS = 1e-8;

    public final class PossibleSolution {

        private final byte[] sol;
        private final byte[] x;
        private double ksi;
        private int indexToSplit = -1;

        public PossibleSolution(final byte[] x) {
            this.x = x;
            sol = x.clone();
            calcKsi();
        }

        private void calcKsi() {
            double currentP = p;
            for (int i = 0; i < n; i++) {
                if (sol[i] == 1) {
                    currentP -= a[i];
                }
            }
            int i = 0;
            while (currentP > EPS && i < n) {
                final int index = positions[i];
                if (sol[index] == -1) {
                    currentP -= a[index];
                    sol[index] = 1;
                }
                i++;
            }
            if (currentP > -EPS) {
                for (int k = 0; k < n; k++) {
                    if (sol[k] == -1) {
                        sol[k] = 0;
                    }
                }
            }
            i = positions[--i];
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    if (currentP < -EPS) {
                        ksi += (1.0 + currentP / a[j]) * c[j];
                        indexToSplit = j;

                    } else {
                        if (sol[j] == 1) {
                            ksi += c[j];
                        }
                    }
                } else {
                    if (sol[j] == 1) {
                        ksi += c[j];
                    }
                }
            }



        }

        public double getKsi() {
            return this.ksi;
        }

        public byte[] getX() {
            return this.x;
        }

        public int getIndexToSplit() {
            return this.indexToSplit;
        }

    }

    private final double[] c;
    private final double[] a;
    private final double p;
    private final int[] positions;
    private final int n;


    public KnapsackProblem(final double[] c, final double[] a, final double p) {
        this.c = c;
        this.a = a;
        this.p = p;
        this.n = c.length;
        this.positions = calcPositions();
    }


    public PossibleSolution run() {
        final List<PossibleSolution> possibleSolutions = new ArrayList<>();
        final byte[] start = new byte[n];
        Arrays.fill(start, (byte) -1);
        possibleSolutions.add(new PossibleSolution(start));

        PossibleSolution solution = getRecord(possibleSolutions);

        while (isThereOtherSolutions(possibleSolutions, solution)) {
            final PossibleSolution solutionToDivide = possibleSolutions.stream()
                    .max((s1, s2) -> Double.compare(s1.getKsi(), s2.getKsi()))
                    .get();

            byte[] arr = solutionToDivide.getX().clone();
            arr[solutionToDivide.getIndexToSplit()] = 0;
            final PossibleSolution ps1 = new PossibleSolution(arr);

            arr = solutionToDivide.getX().clone();
            arr[solutionToDivide.getIndexToSplit()] = 1;
            final PossibleSolution ps2 = new PossibleSolution(arr);

            possibleSolutions.remove(solutionToDivide);
            possibleSolutions.add(ps1);
            possibleSolutions.add(ps2);

            solution = getRecord(possibleSolutions);
        }
        System.out.println(String.format("Size of tree: %d", possibleSolutions.size()));
        System.out.println("Solution is " + Arrays.toString(solution.sol));
        System.out.println("Ksi = " + solution.getKsi());

        return solution;
    }

    private boolean isThereOtherSolutions(final List<PossibleSolution> solutions, final PossibleSolution record) {
        if (record == null) {
            return true;
        }
        return solutions.stream()
                .filter(s -> s.getKsi() > record.getKsi())
                .count() > 0;
    }

    private PossibleSolution getRecord(final List<PossibleSolution> possibleSolutions) {
        return possibleSolutions.stream()
                .filter(s -> s.getIndexToSplit() == -1)
                .max((s1, s2) -> Double.compare(s1.getKsi(), s2.getKsi()))
                .orElse(null);
    }

    private int[] calcPositions() {
        final double[] tmp = new double[n];

        for (int i = 0; i < n; i++) {
            tmp[i] = c[i] / a[i];
        }

        final int[] result = new int[n];

        Arrays.fill(result, -1);

        for (int i = 0; i < n; i++) {
            double max = -1.0;
            int index = -1;
            for (int j = 0; j < n; j++) {
                if (tmp[j] > max) {
                    if (!isChosen(result, j, i)) {
                        max = tmp[j];
                        index = j;
                    }
                }
            }
            result[i] = index;
        }

        System.out.println("Calculated order: " + Arrays.toString(result));

        return result;

    }

    private boolean isChosen(final int[] arr, final int index, final int maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            if (arr[i] == index) {
                return true;
            }
        }
        return false;
    }

    public static void main(final String[] args) {
        final double[] c = {1, 4, 7, 8, 5, 4, 23, 9, 2, 7};//{3.0, 7.0, 1.0, 2.0, 1.0};
        final double[] a = {4, 7, 8, 4, 7, 4, 9, 2, 9, 1};//{7.0, 6.0, 5.0, 4.0, 3.0};
        final double p = 25;//15.0;
        final long start = System.currentTimeMillis();
        new KnapsackProblem(c, a, p).run();
        System.out.println(String.format("Calc takes: %d ms", System.currentTimeMillis() - start));
    }


}

