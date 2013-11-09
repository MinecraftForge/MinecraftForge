
package ibxm;

/*
	Base-2 Log and Exp functions, using linear-interpolated tables.
*/
public class LogTable {
	private static final int TABLE_SHIFT = 7; // 128 points (+1 for interp)
	private static final int INTERP_SHIFT = IBXM.FP_SHIFT - TABLE_SHIFT;
	private static final int INTERP_MASK = ( 1 << INTERP_SHIFT ) - 1;

	private static final int[] exp_2_table = {
		32768, 32945, 33124, 33304, 33485, 33667, 33850, 34033,
		34218, 34404, 34591, 34779, 34968, 35157, 35348, 35540,
		35733, 35927, 36122, 36319, 36516, 36714, 36913, 37114,
		37315, 37518, 37722, 37926, 38132, 38339, 38548, 38757,
		38967, 39179, 39392, 39606, 39821, 40037, 40254, 40473,
		40693, 40914, 41136, 41359, 41584, 41810, 42037, 42265,
		42494, 42725, 42957, 43190, 43425, 43661, 43898, 44136,
		44376, 44617, 44859, 45103, 45347, 45594, 45841, 46090,
		46340, 46592, 46845, 47099, 47355, 47612, 47871, 48131,
		48392, 48655, 48919, 49185, 49452, 49720, 49990, 50262,
		50535, 50809, 51085, 51362, 51641, 51922, 52204, 52487,
		52772, 53059, 53347, 53636, 53928, 54220, 54515, 54811,
		55108, 55408, 55709, 56011, 56315, 56621, 56928, 57238,
		57548, 57861, 58175, 58491, 58809, 59128, 59449, 59772,
		60096, 60423, 60751, 61081, 61412, 61746, 62081, 62418,
		62757, 63098, 63440, 63785, 64131, 64479, 64830, 65182,
		65536
	};

	private static final int[] log_2_table = {
		    0,   367,   732,  1095,  1454,  1811,  2165,  2517,
		 2865,  3212,  3556,  3897,  4236,  4572,  4906,  5238,
		 5568,  5895,  6220,  6542,  6863,  7181,  7497,  7812,
		 8124,  8434,  8742,  9048,  9352,  9654,  9954, 10252,
		10548, 10843, 11136, 11427, 11716, 12003, 12289, 12573,
		12855, 13136, 13414, 13692, 13967, 14241, 14514, 14785,
		15054, 15322, 15588, 15853, 16117, 16378, 16639, 16898,
		17156, 17412, 17667, 17920, 18172, 18423, 18673, 18921,
		19168, 19413, 19657, 19900, 20142, 20383, 20622, 20860,
		21097, 21333, 21568, 21801, 22034, 22265, 22495, 22724,
		22952, 23178, 23404, 23628, 23852, 24074, 24296, 24516,
		24736, 24954, 25171, 25388, 25603, 25817, 26031, 26243,
		26455, 26665, 26875, 27084, 27292, 27499, 27705, 27910,
		28114, 28317, 28520, 28721, 28922, 29122, 29321, 29519,
		29716, 29913, 30109, 30304, 30498, 30691, 30884, 31076,
		31267, 31457, 31646, 31835, 32023, 32210, 32397, 32582,
		32768
	};

	/*
		Calculate log-base-2 of x (non-fixed-point).
		A fixed point value is returned.
	*/
	public static int log_2( int x ) {
		int shift;
		/* Scale x to range 1.0 <= x < 2.0 */
		shift = IBXM.FP_SHIFT;
		while( x < IBXM.FP_ONE ) {
			x <<= 1;
			shift--;
		}
		while( x >= ( IBXM.FP_ONE << 1 ) ) {
			x >>= 1;
			shift++;
		}
		return ( IBXM.FP_ONE * shift ) + eval_table( log_2_table, x - IBXM.FP_ONE );
	}

	/*
		Raise 2 to the power x (fixed point).
		A fixed point value is returned.
	*/
	public static int raise_2( int x ) {
		int y;
		y = eval_table( exp_2_table, x & IBXM.FP_MASK ) << IBXM.FP_SHIFT;
		return y >> IBXM.FP_SHIFT - ( x >> IBXM.FP_SHIFT );
	}

	private static int eval_table( int[] table, int x ) {
		int table_idx, table_frac, c, m, y;
		table_idx = x >> INTERP_SHIFT;
		table_frac = x & INTERP_MASK;
		c = table[ table_idx ];
		m = table[ table_idx + 1 ] - c;
		y = ( m * table_frac >> INTERP_SHIFT ) + c;
		return y >> 15 - IBXM.FP_SHIFT;
	}
}

