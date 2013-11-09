
package ibxm;

public class Pattern {
	public int num_rows;
	
	private int data_offset, note_index;
	private byte[] pattern_data;

	public Pattern() {
		num_rows = 1;
		set_pattern_data( new byte[ 0 ] );
	}
	
	public void set_pattern_data( byte[] data ) {
		if( data != null ) {
			pattern_data = data;
		}
		data_offset = 0;
		note_index = 0;
	}

	public void get_note( int[] note, int index ) {
		if( index < note_index ) {
			note_index = 0;
			data_offset = 0;
		}
		while( note_index <= index ) {
			data_offset = next_note( data_offset, note );
			note_index += 1;
		}
	}

	public int next_note( int data_offset, int[] note ) {
		int bitmask, field;
		if( data_offset < 0 ) {
			data_offset = pattern_data.length;
		}
		bitmask = 0x80;
		if( data_offset < pattern_data.length ) {
			bitmask = pattern_data[ data_offset ] & 0xFF;
		}
		if( ( bitmask & 0x80 ) == 0x80 ) {
			data_offset += 1;
		} else {
			bitmask = 0x1F;
		}
		for( field = 0; field < 5; field++ ) {
			note[ field ] = 0;
			if( ( bitmask & 0x01 ) == 0x01 ) {
				if( data_offset < pattern_data.length ) {
					note[ field ] = pattern_data[ data_offset ] & 0xFF;
					data_offset += 1;
				}
			}
			bitmask = bitmask >> 1;
		}
		return data_offset;
	}
}

