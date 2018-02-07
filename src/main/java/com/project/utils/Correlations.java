package com.project.utils;

/**
 * Klasa Correlations koja izvrsava kalkulaciju Pearson i Spearman korelacija
 * 
 * @author Srdjan Ristic
 */
public class Correlations {

	/**
	 * Pearson korelacija
	 * 
	 * @param xs
	 * @param ys
	 * @return
	 */
	public static double pearsonCorrelation(double[] xs, double[] ys) {
		double sx = 0.0;
		double sy = 0.0;
		double sxx = 0.0;
		double syy = 0.0;
		double sxy = 0.0;

		int n = xs.length;

		for (int i = 0; i < n; ++i) {
			double x = xs[i];
			double y = ys[i];

			sx += x;
			sy += y;
			sxx += x * x;
			syy += y * y;
			sxy += x * y;
		}

		double cov = (sxy / n) - (sx * (sy / n) / n);
		double sigmax = Math.sqrt((sxx / n) - (sx * sx / n / n));
		double sigmay = Math.sqrt((syy / n) - (sy * sy / n / n));
		double result = cov / sigmax / sigmay;

		return result;
	}

	/**
	 * Spearman korelacija
	 * 
	 * @param xs
	 * @param ys
	 * @return
	 */
	public static double spearmanCorrelation(double[] xs, double[] ys) {
		double[] rankX = new double[xs.length];
		double[] rankY = new double[xs.length];
		double[] difference = new double[xs.length];
		double[] differenceSquared = new double[xs.length];
		int n = xs.length;

		for (int i = 0; i < n; i++) {
			rankX[i] = fillRank(xs, xs[i], i);
		}

		for (int i = 0; i < n; i++) {
			rankY[i] = fillRank(ys, ys[i], i);
		}

		for (int i = 0; i < n; i++) {
			difference[i] = rankX[i] - rankY[i];
		}

		for (int i = 0; i < n; i++) {
			differenceSquared[i] = difference[i] * difference[i];
		}

		double dSquared = 0.0;
		for (int i = 0; i < n; i++) {
			dSquared += differenceSquared[i];
		}

		double result = 1 - ((6 * dSquared) / ((n * n * n) - n));
		return result;
	}

	/**
	 * Metoda koja postavlja rank za svu od vrednosti u nizu
	 * 
	 * @param ys
	 * @param element
	 * @param position
	 * @return
	 */
	private static int fillRank(double[] ys, double element, int position) {
		int count = 1;
		for (int i = 0; i < ys.length; i++) {
			if (i != position) {
				if (element > ys[i]) {
					count++;
				}
			}
		}
		return count;
	}
}
