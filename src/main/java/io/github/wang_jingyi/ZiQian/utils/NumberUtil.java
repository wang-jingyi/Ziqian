package io.github.wang_jingyi.ZiQian.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NumberUtil {


    public static final String DIGITS = "(\\p{Digit}+)";
    public static final String HEX_DIGITS = "(\\p{XDigit}+)";

    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    public static final String EXP = "[eE][+-]?" + DIGITS;

    public static final String FP_REGEX = ("[\\x00-\\x20]*" +  // Optional leading "whitespace"
            "[+-]?(" + // Optional sign character
            "NaN|" +           // "NaN" string
            "Infinity|" +      // "Infinity" string

            // A decimal floating-point string representing a finite positive
            // number without a leading sign has at most five basic pieces:
            // Digits . Digits ExponentPart FloatTypeSuffix
            //
            // Since this method allows integer-only strings as input
            // in addition to strings of floating-point literals, the
            // two sub-patterns below are simplifications of the grammar
            // productions from section 3.10.2 of
            // The Java™ Language Specification.

            // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
            "(((" + DIGITS + "(\\.)?(" + DIGITS + "?)(" + EXP + ")?)|" +

            // . Digits ExponentPart_opt FloatTypeSuffix_opt
            "(\\.(" + DIGITS + ")(" + EXP + ")?)|" +

            // Hexadecimal strings
            "((" +
            // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HEX_DIGITS + "(\\.)?)|" +

            // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HEX_DIGITS + "?(\\.)" + HEX_DIGITS + ")" +

            ")[pP][+-]?" + DIGITS + "))" +
            "[fFdD]?))" +
            "[\\x00-\\x20]*"); // Optional trailing "whitespace"

    public static Pattern p = Pattern.compile(FP_REGEX);

    /**
     * http://docs.oracle.com/javase/7/docs/api/java/lang/Double.html#parseDouble(java.lang.String)
     * @param numberString
     * @return whether numberString can be parsed into a double
     */
    public static boolean isDouble(String numberString) {
        if (p.matcher(numberString).matches())
            return true; // Will not throw NumberFormatException
        else {
            return false;
        }
    }
    
    public static String ArrayToString(double[] arr){
    	List<Double> da = new ArrayList<>();
    	for(int i=0; i<arr.length; i++){
    		da.add(arr[i]);
    	}
    	return da.toString();
    }
    
    public static String twoDArrayToString(double[][] arr){
    	List<String> da = new ArrayList<>();
    	for(int i=0; i<arr.length; i++){
    		da.add(ArrayToString(arr[i]));
    	}
    	return da.toString();
    }
    
    public static String ArrayToString(int[] arr){
    	List<Integer> da = new ArrayList<>();
    	for(int i=0; i<arr.length; i++){
    		da.add(arr[i]);
    	}
    	return da.toString();
    }
    
    public static String twoDArrayToString(int[][] arr){
    	List<String> da = new ArrayList<>();
    	for(int i=0; i<arr.length; i++){
    		da.add(ArrayToString(arr[i]));
    	}
    	return da.toString();
    }
    
    public static int[] getRowSums(int[][] arr){
    	int n = arr.length;
    	int[] rowsums = new int[n];
    	
		for(int i=0; i<n; i++){
			int rowsum = 0;
			int[] row = arr[i];
			for(int j=0; j<n; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}
		return rowsums;
    }
    
    public static double[] getRowSums(double[][] arr){
    	int n = arr.length;
    	double[] rowsums = new double[n];
    	
		for(int i=0; i<n; i++){
			double rowsum = 0;
			double[] row = arr[i];
			for(int j=0; j<n; j++){
				rowsum += row[j];
			}
			rowsums[i] = rowsum;
		}
		return rowsums;
    }
    
    public static boolean zeroOneDistribution(double[] dist){
    	for(int i=0; i<dist.length; i++){
    		if(dist[i]>=1){
    			return true;
    		}
    	}
    	return false;
    }
     
}