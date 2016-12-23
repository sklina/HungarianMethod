package knapsackproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */

public final class KnapsackProblem {

    public final class PossibleSolution {

        private final byte[] sol;
        private double ksi;
        private int indexToSplit = -1;
		private boolean correctSolution;
		
		/**
		 * Конструктор с параметром.
		 * @param sol возможное решение
		 */
        public PossibleSolution(final byte[] sol) {
            this.sol = sol;
			correctSolution = true;
            calcKsi();
        }

		/**
		 * Подсчет ksi.
		 */
        private void calcKsi() {
            double currentP = p;
            for (int i = 0; i < n; i++) {
                if (sol[i] == 1) {
                    currentP -= a[i];
                }
            }
			if (currentP < 0) {
				correctSolution = false;
				return;
			}
			
            int i = 0;
            while (currentP > 0 && i < n) {
                final int index = positions[i];
                if (sol[index] == -1) {
                    currentP -= a[index];
                    sol[index] = 1;
                }
                i++;
            }
            if (currentP > 0) {
                for (int k = 0; k < n; k++) {
                    if (sol[k] == -1) {
                        sol[k] = 0;
                    }
                }
            }
            if (currentP != 0)
				i = positions[--i];
			else 
				i = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    if (currentP < 0) {
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

        public byte[] getSol() {
            return this.sol;
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
        this.positions = ranging();
    }


    public PossibleSolution run() {
        final List<PossibleSolution> possibleSolutions = new ArrayList<>();
        final byte[] start = new byte[n];
        Arrays.fill(start, (byte) -1);
        possibleSolutions.add(new PossibleSolution(start));

        PossibleSolution solution = getRecord(possibleSolutions);

        while (isThereOtherSolutions(possibleSolutions, solution)) {
            final PossibleSolution solutionToDivide = possibleSolutions.stream()
					.filter(s -> s.getIndexToSplit() != -1)
                    .min((s1, s2) -> Double.compare(s1.getKsi(), s2.getKsi()))
                    .get();

            byte[] arr = solutionToDivide.getSol().clone();
            arr[solutionToDivide.getIndexToSplit()] = 0;
            final PossibleSolution ps1 = new PossibleSolution(arr);
            addCorrectSolution(possibleSolutions, ps1);
			
            arr = solutionToDivide.getSol().clone();
            arr[solutionToDivide.getIndexToSplit()] = 1;
            final PossibleSolution ps2 = new PossibleSolution(arr);
			addCorrectSolution(possibleSolutions, ps2);
			
            possibleSolutions.remove(solutionToDivide);

            solution = getRecord(possibleSolutions);
        }
		
        System.out.println("Solution is " + Arrays.toString(solution.sol));
        System.out.println("Ksi = " + solution.getKsi());

        return solution;
    }
	private void addCorrectSolution(final List<PossibleSolution> solutions, PossibleSolution possibleSolution) {
		if (possibleSolution.correctSolution) 
			solutions.add(possibleSolution);
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

	/**
	 * Ранжирование элементов.
	 * @return массив значений
	 */
    private int[] ranging() {
        final double[] ratio = new double[n];
        final int[] result = new int[n];

		// инициализация
        for (int i = 0; i < n; i++) {
            ratio[i] = c[i] / a[i];
			result[i] = -1;
        }

		for (int i = 0; i < n; i++) {
			double max = Double.NEGATIVE_INFINITY;
			int index = -1;
			for (int j = 0; j < n; j++) {
				if (ratio[j] > max && !isChosen(result, j, i)) {
					max = ratio[j];
					index = j;
				}
			}
			result[i] = index;
		}

        System.out.println("Calculated order: " + Arrays.toString(result));

        return result;

    }

	/**
	 * Проверка, была ли эта позиция уже просмотрена
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

    public static void main(final String[] args) {
        final double[] c = {3, 1, 4, 5, 6};//{1.0, 6.0, 4.0, 7.0, 6.0}; // стоимость предмета
        final double[] a = {2, 4, 7, 8, 5};//{3.0, 4.0, 5.0, 8.0, 9.0}; // вес предмета
        final double p = 10.0;//13.0;
        final long start = System.currentTimeMillis();
        new KnapsackProblem(c, a, p).run();
        System.out.println(String.format("Calc takes: %d ms", System.currentTimeMillis() - start));
    }


}

