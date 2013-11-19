
package ibxm;

public class Sample {
	public String name;
	public boolean set_panning;
	public int volume, panning;
	public int transpose;
	
	private int loop_start, loop_length;
	private short[] sample_data;

	/* For the sinc interpolator.*/
	private static final int POINT_SHIFT = 4;
	private static final int POINTS = 1 << POINT_SHIFT;
	private static final int OVERLAP = POINTS >> 1;
	private static final int INTERP_SHIFT = IBXM.FP_SHIFT - 4;
	private static final int INTERP_BITMASK = ( 1 << INTERP_SHIFT ) - 1;
	private static final short[] sinc_table = {
		 0, -7,  27, -71,  142, -227,  299,  32439,   299,  -227,  142,  -71,  27,  -7,  0,  0,
		 0,  0,  -5,  36, -142,  450, -1439, 32224,  2302,  -974,  455, -190,  64, -15,  2,  0,
		 0,  6, -33, 128, -391, 1042, -2894, 31584,  4540, -1765,  786, -318, 105, -25,  3,  0,
		 0, 10, -55, 204, -597, 1533, -4056, 30535,  6977, -2573, 1121, -449, 148, -36,  5,  0,
		-1, 13, -71, 261, -757, 1916, -4922, 29105,  9568, -3366, 1448, -578, 191, -47,  7,  0,
		-1, 15, -81, 300, -870, 2185, -5498, 27328, 12263, -4109, 1749, -698, 232, -58,  9,  0,
		-1, 15, -86, 322, -936, 2343, -5800, 25249, 15006, -4765, 2011, -802, 269, -68, 10,  0,
		-1, 15, -87, 328, -957, 2394, -5849, 22920, 17738, -5298, 2215, -885, 299, -77, 12,  0,
		 0, 14, -83, 319, -938, 2347, -5671, 20396, 20396, -5671, 2347, -938, 319, -83, 14,  0,
		 0, 12, -77, 299, -885, 2215, -5298, 17738, 22920, -5849, 2394, -957, 328, -87, 15, -1,
		 0, 10, -68, 269, -802, 2011, -4765, 15006, 25249, -5800, 2343, -936, 322, -86, 15, -1,
		 0,  9, -58, 232, -698, 1749, -4109, 12263, 27328, -5498, 2185, -870, 300, -81, 15, -1,
		 0,  7, -47, 191, -578, 1448, -3366,  9568, 29105, -4922, 1916, -757, 261, -71, 13, -1,
		 0,  5, -36, 148, -449, 1121, -2573,  6977, 30535, -4056, 1533, -597, 204, -55, 10,  0,
		 0,  3, -25, 105, -318,  786, -1765,  4540, 31584, -2894, 1042, -391, 128, -33,  6,  0,
		 0,  2, -15,  64, -190,  455,  -974,  2302, 32224, -1439,  450, -142,  36,  -5,  0,  0,
		 0,  0,  -7,  27,  -71,  142,  -227,   299, 32439,   299, -227,  142, -71,  27, -7,  0
	};
	
	public Sample() {
		name = "";
		set_sample_data( new short[ 0 ], 0, 0, false );
	}
	
	public void set_sample_data( short[] data, int loop_start, int loop_length, boolean ping_pong ) {
		int offset;
		short sample;		
		if( loop_start < 0 ) {
			loop_start = 0;
		}
		if( loop_start >= data.length ) {
			loop_start = data.length - 1;
		}
		if( loop_start + loop_length > data.length ) {
			loop_length = data.length - loop_start;
		}
		if( loop_length <= 1 ) {
			sample_data = new short[ OVERLAP + data.length + OVERLAP * 3 ];
			System.arraycopy( data, 0, sample_data, OVERLAP, data.length );
			offset = 0;
			while( offset < OVERLAP ) {
				sample = sample_data[ OVERLAP + data.length - 1 ];
				sample = ( short ) ( sample * ( OVERLAP - offset ) / OVERLAP );
				sample_data[ OVERLAP + data.length + offset ] = sample;
				offset += 1;
			}
			loop_start = OVERLAP + data.length + OVERLAP;
			loop_length = 1;
		} else {
			if( ping_pong ) {
				sample_data = new short[ OVERLAP + loop_start + loop_length * 2 + OVERLAP * 2 ];
				System.arraycopy( data, 0, sample_data, OVERLAP, loop_start + loop_length );
				offset = 0;
				while( offset < loop_length ) {
					sample = data[ loop_start + loop_length - offset - 1 ];
					sample_data[ OVERLAP + loop_start + loop_length + offset ] = sample;
					offset += 1;
				}
				loop_start = loop_start + OVERLAP;
				loop_length = loop_length * 2;
			} else {
				sample_data = new short[ OVERLAP + loop_start + loop_length + OVERLAP * 2 ];
				System.arraycopy( data, 0, sample_data, OVERLAP, loop_start + loop_length );
				loop_start = loop_start + OVERLAP;
			}
			offset = 0;
			while( offset < OVERLAP * 2 ) {
				sample = sample_data[ loop_start + offset ];
				sample_data[ loop_start + loop_length + offset ] = sample;
				offset += 1;
			}
		}
		this.loop_start = loop_start;
		this.loop_length = loop_length;
	}

	public void resample_nearest(
			int sample_idx, int sample_frac, int step, int left_gain, int right_gain,
			int[] mix_buffer, int frame_offset, int frames ) {
		int loop_end, offset, end, max_sample_idx;
		sample_idx += OVERLAP;
		loop_end = loop_start + loop_length - 1;
		offset = frame_offset << 1;
		end = ( frame_offset + frames - 1 ) << 1;
		while( frames > 0 ) {
			if( sample_idx > loop_end ) {
				if( loop_length <= 1 ) {
					break;
				}
				sample_idx = loop_start + ( sample_idx - loop_start ) % loop_length;
			}
			max_sample_idx = sample_idx + ( ( sample_frac + ( frames - 1 ) * step ) >> IBXM.FP_SHIFT );
			if( max_sample_idx > loop_end ) {
				while( sample_idx <= loop_end ) {
					mix_buffer[ offset++ ] += sample_data[ sample_idx ] * left_gain >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += sample_data[ sample_idx ] * right_gain >> IBXM.FP_SHIFT;
					sample_frac += step;
					sample_idx += sample_frac >> IBXM.FP_SHIFT;
					sample_frac &= IBXM.FP_MASK;
				}
			} else {
				while( offset <= end ) {
					mix_buffer[ offset++ ] += sample_data[ sample_idx ] * left_gain >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += sample_data[ sample_idx ] * right_gain >> IBXM.FP_SHIFT;
					sample_frac += step;
					sample_idx += sample_frac >> IBXM.FP_SHIFT;
					sample_frac &= IBXM.FP_MASK;
				}
			}
			frames = ( end - offset + 2 ) >> 1;
		}
	}

	public void resample_linear(
			int sample_idx, int sample_frac, int step, int left_gain, int right_gain,
			int[] mix_buffer, int frame_offset, int frames ) {
		int loop_end, offset, end, max_sample_idx, amplitude;
		sample_idx += OVERLAP;
		loop_end = loop_start + loop_length - 1;
		offset = frame_offset << 1;
		end = ( frame_offset + frames - 1 ) << 1;
		while( frames > 0 ) {
			if( sample_idx > loop_end ) {
				if( loop_length <= 1 ) {
					break;
				}
				sample_idx = loop_start + ( sample_idx - loop_start ) % loop_length;
			}
			max_sample_idx = sample_idx + ( ( sample_frac + ( frames - 1 ) * step ) >> IBXM.FP_SHIFT );
			if( max_sample_idx > loop_end ) {
				while( sample_idx <= loop_end ) {
					amplitude = sample_data[ sample_idx ];
					amplitude += ( sample_data[ sample_idx + 1 ] - amplitude ) * sample_frac >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += amplitude * left_gain >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += amplitude * right_gain >> IBXM.FP_SHIFT;
					sample_frac += step;
					sample_idx += sample_frac >> IBXM.FP_SHIFT;
					sample_frac &= IBXM.FP_MASK;
				}
			} else {
				while( offset <= end ) {
					amplitude = sample_data[ sample_idx ];
					amplitude += ( sample_data[ sample_idx + 1 ] - amplitude ) * sample_frac >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += amplitude * left_gain >> IBXM.FP_SHIFT;
					mix_buffer[ offset++ ] += amplitude * right_gain >> IBXM.FP_SHIFT;
					sample_frac += step;
					sample_idx += sample_frac >> IBXM.FP_SHIFT;
					sample_frac &= IBXM.FP_MASK;
				}
			}
			frames = ( end - offset + 2 ) >> 1;
		}
	}

	public void resample_sinc(
			int sample_idx, int sample_frac, int step, int left_gain, int right_gain,
			int[] mix_buffer, int frame_offset, int frames ) {
		int offset, end, loop_end, table_idx, a1, a2, amplitude;
		loop_end = loop_start + loop_length - 1;
		offset = frame_offset << 1;
		end = ( frame_offset + frames - 1 ) << 1;
		while( offset <= end ) {
			if( sample_idx > loop_end ) {
				if( loop_length <= 1 ) {
					break;
				}
				sample_idx = loop_start + ( sample_idx - loop_start ) % loop_length;
			}
			table_idx = ( sample_frac >> INTERP_SHIFT ) << POINT_SHIFT;
			a1  = sinc_table[ table_idx + 0  ] * sample_data[ sample_idx + 0  ] >> 15;
			a1 += sinc_table[ table_idx + 1  ] * sample_data[ sample_idx + 1  ] >> 15;
			a1 += sinc_table[ table_idx + 2  ] * sample_data[ sample_idx + 2  ] >> 15;
			a1 += sinc_table[ table_idx + 3  ] * sample_data[ sample_idx + 3  ] >> 15;
			a1 += sinc_table[ table_idx + 4  ] * sample_data[ sample_idx + 4  ] >> 15;
			a1 += sinc_table[ table_idx + 5  ] * sample_data[ sample_idx + 5  ] >> 15;
			a1 += sinc_table[ table_idx + 6  ] * sample_data[ sample_idx + 6  ] >> 15;
			a1 += sinc_table[ table_idx + 7  ] * sample_data[ sample_idx + 7  ] >> 15;
			a1 += sinc_table[ table_idx + 8  ] * sample_data[ sample_idx + 8  ] >> 15;
			a1 += sinc_table[ table_idx + 9  ] * sample_data[ sample_idx + 9  ] >> 15;
			a1 += sinc_table[ table_idx + 10 ] * sample_data[ sample_idx + 10 ] >> 15;
			a1 += sinc_table[ table_idx + 11 ] * sample_data[ sample_idx + 11 ] >> 15;
			a1 += sinc_table[ table_idx + 12 ] * sample_data[ sample_idx + 12 ] >> 15;
			a1 += sinc_table[ table_idx + 13 ] * sample_data[ sample_idx + 13 ] >> 15;
			a1 += sinc_table[ table_idx + 14 ] * sample_data[ sample_idx + 14 ] >> 15;
			a1 += sinc_table[ table_idx + 15 ] * sample_data[ sample_idx + 15 ] >> 15;
			a2  = sinc_table[ table_idx + 16 ] * sample_data[ sample_idx + 0  ] >> 15;
			a2 += sinc_table[ table_idx + 17 ] * sample_data[ sample_idx + 1  ] >> 15;
			a2 += sinc_table[ table_idx + 18 ] * sample_data[ sample_idx + 2  ] >> 15;
			a2 += sinc_table[ table_idx + 19 ] * sample_data[ sample_idx + 3  ] >> 15;
			a2 += sinc_table[ table_idx + 20 ] * sample_data[ sample_idx + 4  ] >> 15;
			a2 += sinc_table[ table_idx + 21 ] * sample_data[ sample_idx + 5  ] >> 15;
			a2 += sinc_table[ table_idx + 22 ] * sample_data[ sample_idx + 6  ] >> 15;
			a2 += sinc_table[ table_idx + 23 ] * sample_data[ sample_idx + 7  ] >> 15;
			a2 += sinc_table[ table_idx + 24 ] * sample_data[ sample_idx + 8  ] >> 15;
			a2 += sinc_table[ table_idx + 25 ] * sample_data[ sample_idx + 9  ] >> 15;
			a2 += sinc_table[ table_idx + 26 ] * sample_data[ sample_idx + 10 ] >> 15;
			a2 += sinc_table[ table_idx + 27 ] * sample_data[ sample_idx + 11 ] >> 15;
			a2 += sinc_table[ table_idx + 28 ] * sample_data[ sample_idx + 12 ] >> 15;
			a2 += sinc_table[ table_idx + 29 ] * sample_data[ sample_idx + 13 ] >> 15;
			a2 += sinc_table[ table_idx + 30 ] * sample_data[ sample_idx + 14 ] >> 15;
			a2 += sinc_table[ table_idx + 31 ] * sample_data[ sample_idx + 15 ] >> 15;			
			amplitude = a1 + ( ( a2 - a1 ) * ( sample_frac & INTERP_BITMASK ) >> INTERP_SHIFT );
			mix_buffer[ offset ] += amplitude * left_gain >> IBXM.FP_SHIFT;
			mix_buffer[ offset + 1 ] += amplitude * right_gain >> IBXM.FP_SHIFT;
			offset += 2;
			sample_frac += step;
			sample_idx += sample_frac >> IBXM.FP_SHIFT;
			sample_frac &= IBXM.FP_MASK;
		}
	}

	public boolean has_finished( int sample_idx ) {
		boolean finished;
		finished = false;
		if( loop_length <= 1 && sample_idx > loop_start ) {
			finished = true;
		}
		return finished;
	}
}
