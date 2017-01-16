package knapsackproblem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */

public final class KnapsackProblem {

	public static void main(final String[] args) {
		final double[] c = {3, 1, 4, 5, 6};//{1.0, 6.0, 4.0, 7.0, 6.0}; // стоимость предмета
		final double[] a = {2, 4, 7, 8, 5};//{3.0, 4.0, 5.0, 8.0, 9.0}; // вес предмета
		final double p = 10.0;//13.0; // вместимость рюкзака

		Solution solution = new KnapsackProblem(c, a, p).buildKnapsackProblem();

		System.out.println("Решение " + stringSolution(solution.x));
		System.out.println("Кси = " + solution.getKsi());
	}

    public static String stringSolution(byte[] arr) {
		StringBuilder stringBuilder = new StringBuilder().append('(');
		for (int i = 0; i < arr.length; i++) {
			stringBuilder
					.append(arr[i])
					.append(", ");
		}
		stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length());
		return stringBuilder.append(')').toString();
	}
	
	
    private final double[] cost;
    private final double[] weight;
    private final double limitWeight;
    private final int[] sequence; // порядок индексов после нанжировки
    private final int N;

    public KnapsackProblem(final double[] cost, final double[] weight, final double maxWeight) {
        this.cost = cost;
        this.weight = weight;
        this.limitWeight = maxWeight;
        this.N = cost.length;
        this.sequence = ranging();
    }


    public Solution buildKnapsackProblem() {
        final List<Solution> solutions = new ArrayList<>();
        byte[] newX= new byte[N];
        Arrays.fill(newX, (byte) -1);
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
		if (possibleSolution.correctSolution) 
			solutions.add(possibleSolution);
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


	public final class Solution {

        private final byte[] x;
	    private boolean correctSolution;
        private double ksi;
        private int taken = -1; //индекс взятого предмета

        public Solution(final byte[] x) {
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
			
            for (int i = 0; i < N; i++) {
                if (i == taken && maxWeight < 0) {
					ksi += (1.0 + maxWeight / weight[i]) * cost[i];
               } else if (x[i] == 1) {
					ksi += cost[i];
				}
			}
			
			if (maxWeight > 0) {
                replaceToNull();
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
		
		void replaceToNull(){
			for (int k = 0; k < N; k++) {
                    if (x[k] == -1) {
                        x[k] = 0;
                    }
                }
		}

        public double getKsi() {
            return this.ksi;
        }

        public byte[] getX() {
            return this.x;
        }

        public int getTaken() {
            return this.taken;
        }

    }
}

