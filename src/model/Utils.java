package model;

public class Utils {
	/* This function quantizes the color components by mapping one color comp value
	 * to 64 color comp values. This enables us to use just 2 bits instead of 8 bits
	 * to represent a color component. Only 4 different color component values instead
	 * of 256 values.
	 * So for a RGB color it would reduce to 4*4*4= 64 different colors */
	public static byte getQuantizedByteVal(byte color) {
		int	val		= color & 0xFF;
		int	newCol	= (color & ~0x3F) & 0xFF;
		
		if((color & 0x3F) > 31) {
			newCol += 63;
		}
		else if(newCol != 0) {
			newCol -= 1;
		}
		
		return (byte)newCol;
	}
}
