package hungarianmethod;

import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Базовый класс для решения задачи венгерским методом.
 *
 * @author Alina Skorokhodova <alina.skorokhodova@vistar.su>
 */
public class HungarianMethod {

    /** Матрица назначения. */
    public static RealMatrix matrix;
    /** Матрица пометок. */
    public static RealMatrix markMatrix;
    /** Матрица ограничений. */
    public static RealMatrix blockMatrix;
    /** Список независимых нулей. */
    public static ArrayList<ZerosPosition> indepZerList;
    /** Проверка результата. */
    public static double check = 0;
    /** Число независимых нулей. */
    public static int k = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int n = 4;
        double[][] d = initRandMatr(n);
            
//        double[][] d = { { 3, 6, 4, 8, 10, 15 }, 
//                         { 4, 8, 9, 10, 12, 16 }, 
//                         { 2, 4, 10, 12, 14, 15 },
//                         { 5, 6, 9, 10, 10, 11 },
//						 { 1, 2, 3, 4, 5, 6 },
//                         { 5, 6, 7, 9, 10, 12 } };
//		double[][] d = { { 0, 1, 1, 1, 1, 1 }, 
//                         { 0, 1, 1, 1, 1, 1 }, 
//                         { 0, 1, 0, 0, 1, 1 },
//                         { 0, 1, 0, 0, 1, 1 },
//			 { 0, 1, 0, 0, 0, 1 },
//                         { 0, 0, 0, 0, 0, 0 } };
//        double[][] d = {{2, 4, 1, 3, 3, 2},
//        {1, 5, 4, 1, 2, 1},
//        {3, 5, 2, 2, 4, 4},
//        {1, 4, 3, 1, 4, 3},
//        {3, 2, 5, 3, 5, 2},
//        {1, 2, 4, 5, 3, 2}};
//                double[][] d = {{0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0},
//        {0, 0, 0, 0, 0, 0}};
        matrix = new Array2DRowRealMatrix(d);
        while (k < matrix.getColumnDimension()) {
            markMatrix = new Array2DRowRealMatrix(d.length, d.length);
            blockMatrix = new Array2DRowRealMatrix(d.length, d.length);

            reduction();
            k = countingIndependentZeros();
            System.out.println("IndependentZeros = "+ k);
            if (k < matrix.getColumnDimension()) {
                makeNewZeros();
                System.out.println(markMatrix);
                System.out.println(matrix);
            }

        }

        int result = 0;
        for (ZerosPosition zerosPosition : indepZerList) {
            result += d[zerosPosition.getRow()][zerosPosition.getColumn()];
        }

        System.out.println("L(x) = " + result);
        System.out.println("Check = " + check);
    }

    /**
	 * Генерирует матрицу с случайными значениями.
	 *
	 * @param n размерность матрицы
	 * @return матрица с рандомными значениями
	 */
	public static double[][] initRandMatr(int n) {
		double[][] d = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				d[i][j] = 4 + (int) (Math.random() * 10);;
			}
		}
		return d;
	}
    /**
     * Поиск минимального элемента в строке.
     *
     * @param i номер строки
     * @return минимальный элемент в строке
     */
    public static double minInRow(int i) {
        double min = Double.POSITIVE_INFINITY;
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            if (matrix.getEntry(i, j) < min) {
                min = matrix.getEntry(i, j);
            }
        }
        return min;
    }

    /**
     * Поиск минимального элемента в столбце.
     *
     * @param j номер столбца
     * @return минимальный элемент в столбце
     */
    public static double minInColumn(int j) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            if (matrix.getEntry(i, j) < min) {
                min = matrix.getEntry(i, j);
            }
        }
        return min;
    }

    /**
     * Приведение матрицы.
     */
    public static void reduction() {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            double alpha = minInRow(i);
            matrix.setRowMatrix(i, matrix.getRowMatrix(i).scalarAdd(-alpha));
            check += alpha;
        }
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            double beta = minInColumn(j);
            matrix.setColumnMatrix(j, matrix.getColumnMatrix(j).scalarAdd(-beta));
            check += beta;
        }
    }

    /**
     * Подсчет независимых нулей в строке.
     *
     * @param i номер строки
     * @return количество нулей в строке
     */
    public static int countingZerosInRow(int i) {
        int count = 0;
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            if (markMatrix.getEntry(i, j) == 0 && matrix.getEntry(i, j) == 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Подсчет независимых нулей в столбце.
     *
     * @param j номер столбца
     * @return количество нулей в столбце
     */
    public static int countingZerosInColumn(int j) {
        int count = 0;
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            if (markMatrix.getEntry(i, j) == 0 && matrix.getEntry(i, j) == 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Подсчет независимых нулей по всей матрице.
     *
     * @return количество нулей
     */
    public static int countingZerosInMatrix() {
        int count = 0;
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getRowDimension(); j++) {
                if (markMatrix.getEntry(i, j) == 0 && matrix.getEntry(i, j) == 0 && blockMatrix.getEntry(i, j) == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Поиск первой позиции нуля в строке.
     *
     * @param i номер строки
     * @return индекс нулевого элемента
     */
    public static int searchZeroInRow(int i) {
        int index = Integer.MAX_VALUE;
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            if (markMatrix.getEntry(i, j) == 0 && matrix.getEntry(i, j) == 0) {
                index = j;
				 break;
            }
        }
        return index;
    }

    /**
     * Поиск первой позиции нуля в столбце.
     *
     * @param j номер столбца
     * @return индекс нулевого элемента
     */
    public static int searchZeroInColumn(int j) {
        int index = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            if (markMatrix.getEntry(i, j) == 0 && matrix.getEntry(i, j) == 0) {
                index = i;
				break;
            }

        }
        return index;
    }

    /**
     * Подсчет независимых нулей.
     *
     * @return число независимых нулей
     */
    public static int countingIndependentZeros() {
        indepZerList = new ArrayList<>();
        int result = 0;
        int allZeros = countingZerosInMatrix();
        int value = 1;
        boolean rowFlag = false, columnFlag = false, currentFlag;
        System.out.println(matrix);
        while (allZeros > 0 && k < matrix.getColumnDimension()) {
            if (rowFlag && columnFlag) {
                value++;
                rowFlag = !rowFlag;
                columnFlag = !columnFlag;
                
            }
            while (!rowFlag) {
                currentFlag = false;
                for (int i = 0; i < matrix.getRowDimension(); i++) {
                    currentFlag |= false;
                    if (countingZerosInRow(i) == value) {
                        int column = searchZeroInRow(i);
                        if (column < Integer.MAX_VALUE && blockMatrix.getEntry(i, column) == 0) {
                            markColumn(column);
                            blockingRowAndColumn(i, column);
                            indepZerList.add(new ZerosPosition(i, column));
                            currentFlag = true;
                            result++;
                            if (value > 1) {
                                value = 1;
                                break;
                            }

                        }
                    }
                    if (i == matrix.getRowDimension() - 1 && !currentFlag) {
                        // нечего вычеркивать в строке
                        rowFlag = !currentFlag;
                    }
                }
            }

            while (!columnFlag) {
                currentFlag = false;
                for (int j = 0; j < matrix.getColumnDimension(); j++) {
                    currentFlag |= false;
                    if (countingZerosInColumn(j) == value) {
                        int row = searchZeroInColumn(j);
                        if (row < Integer.MAX_VALUE && blockMatrix.getEntry(row, j) == 0) {
                            markRow(row);
                            blockingRowAndColumn(row, j);
                            indepZerList.add(new ZerosPosition(row, j));
                            currentFlag = true;
                            result++;
                            if (value > 1) {
                                value = 1;
                                break;
                            }
                        }
                    }
                    if (j == matrix.getColumnDimension() - 1 && !currentFlag) {
                        // нечего вычеркивать в столбце
                        columnFlag = !currentFlag;
                    }
                }
            }

            allZeros = countingZerosInMatrix();
            for (ZerosPosition zerosPosition : indepZerList) {
                   System.out.print(zerosPosition + " ");
                }
            System.out.println("");
        }

        return result;
    }

    /**
     * Вычеркиваем строку.
     *
     * @param i номер строки
     */
    public static void markRow(int i) {
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            markMatrix.addToEntry(i, j, 1);
        }
    }

    /**
     * Вычеркиваем столбец.
     *
     * @param j номер столбца
     */
    public static void markColumn(int j) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            markMatrix.addToEntry(i, j, 1);
        }
    }

    public static void blockingRowAndColumn(int row, int column) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            blockMatrix.addToEntry(i, column, 1);
        }
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            blockMatrix.addToEntry(row, j, 1);
        }
    }

    /**
     * Поиск минимального элемента среди незачеркнутых.
     *
     * @return значение минимального элемента
     */
    public static double minInNoMark() {
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                if (markMatrix.getEntry(i, j) == 0) {
                    double tmp = matrix.getEntry(i, j);
                    if (tmp < min) {
                        min = tmp;
                    }
                }
            }
        }

        return min;
    }

    /**
     * Получение новых нулей. Применятся тогда, когда число линейнонезависимых
     * нулей меньше размерности матрицы.
     */
    public static void makeNewZeros() {
        double alpha = minInNoMark();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                double tmp = markMatrix.getEntry(i, j);
                if (tmp == 0) {
                    matrix.addToEntry(i, j, -alpha);
                } else if (tmp == 2) {
                    matrix.addToEntry(i, j, alpha);
                } else if (tmp > 2) {
                    throw new IllegalArgumentException(
                            "Ошибочка вышла. Элемент c[" + i + "," + j + "] "
                            + "якобы зачеркнут " + tmp + " линиями.");
                }
            }
        }

        check += alpha * (matrix.getColumnDimension() - k);
    }
	
	public static class ZerosPosition {

		private final int row;
		private final int column;

		public ZerosPosition(int row, int column) {
			this.row = row;
			this.column = column;
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}

		@Override
		public String toString() {
			return "Zero[row=" + (row + 1)
					+ ", column=" + (column + 1)
					+ "]";
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof ZerosPosition)) {
				return false;
			}
			ZerosPosition pairo = (ZerosPosition) o;
			return this.row == pairo.getRow()
					|| this.column == pairo.getColumn();
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 19 * hash + this.row;
			hash = 19 * hash + this.column;
			return hash;
		}
	}
}
