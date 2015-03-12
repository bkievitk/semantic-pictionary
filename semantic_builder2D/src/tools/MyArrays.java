package tools;

/**
 * Some simple functions on arrays.
 * This basically exists for Mac compatibility since the MyArrays function is non-standard.
 * READY
 * @author bkievitk
 */

public class MyArrays {

	/**
	 * Check if two integer arrays are equal.
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static boolean equal(int[] arr1, int[] arr2) {
		if(arr1.length == arr2.length) {
			for(int i=0;i<arr1.length;i++) {
				if(arr1[i] != arr2[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Check if two short arrays are equal.
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static boolean equal(short[] arr1, short[] arr2) {
		if(arr1.length == arr2.length) {
			for(int i=0;i<arr1.length;i++) {
				if(arr1[i] != arr2[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Create copy of the array.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static int[] copyOf(int[] arr) {
		int[] ret = new int[arr.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static short[] copyOf(short[] arr) {
		short[] ret = new short[arr.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static long[] copyOf(long[] arr) {
		long[] ret = new long[arr.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static float[] copyOf(float[] arr) {
		float[] ret = new float[arr.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static double[] copyOf(double[] arr) {
		double[] ret = new double[arr.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = arr[i];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array from start to start plus len.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static int[] copyOfRange(int[] arr, int start, int len) {
		int[] ret = new int[len];
		for(int i=0;i<len;i++) {
			ret[i] = ret[i+start];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array from start to start plus len.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static short[] copyOfRange(short[] arr, int start, int len) {
		short[] ret = new short[len];
		for(int i=0;i<len;i++) {
			ret[i] = ret[i+start];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array from start to start plus len.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static long[] copyOfRange(long[] arr, int start, int len) {
		long[] ret = new long[len];
		for(int i=0;i<len;i++) {
			ret[i] = ret[i+start];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array from start to start plus len.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static float[] copyOfRange(float[] arr, int start, int len) {
		float[] ret = new float[len];
		for(int i=0;i<len;i++) {
			ret[i] = ret[i+start];
		}
		return ret;
	}
	
	/**
	 * Create copy of the array from start to start plus len.
	 * This is for compatibility with Mac computers.
	 * @param arr
	 * @return
	 */
	public static double[] copyOfRange(double[] arr, int start, int len) {
		double[] ret = new double[len];
		for(int i=0;i<len;i++) {
			ret[i] = ret[i+start];
		}
		return ret;
	}
	
	/**
	 * Creates a copy of the from into the to starting at the given locations.
	 * @param from
	 * @param to
	 * @param startFrom
	 * @param startTo
	 * @param len
	 */
	public static void copyIntoRange(double[] from, double[] to, int startFrom, int startTo, int len) {
		for(int i=0;i<len;i++) {
			if(startTo+i<to.length && startFrom+i < from.length) {
				to[startTo+i] = from[startFrom + i];
			}
		}
	}
	
	/**
	 * Find the sum of all elements in an integer array.
	 * @param a Array to sum over.
	 * @return
	 */
	public static int sum(int[] a) {
		int sum = 0;
		for(int i=0;i<a.length;i++) {
			sum += a[i];
		}
		return sum;
	}

}
