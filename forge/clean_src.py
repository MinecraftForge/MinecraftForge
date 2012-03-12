import re, os, shutil, sys, fnmatch

if __name__ == '__main__':
    print "Cleaning trailing zeros from source files"
    trailing = re.compile(r'(?P<full>[0-9]+\.(?P<decimal>[0-9]+?)0)(?P<type>[Dd])')
    for path, _, filelist in os.walk(sys.argv[1], followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.java'):
            src_file = os.path.normpath(os.path.join(path, cur_file))
            tmp_file = src_file + '.tmp'
            with open(src_file, 'r') as fh:
                buf = fh.read()

                
            def strip_zeros_match(match):
                ret = match.group('full').rstrip('0')
                if ret[-1] == '.':
                    ret += '0'
                return ret
                
            buf = trailing.sub(strip_zeros_match, buf) #Strip trailing zeroes: 1.0040D -> 1.004D

            with open(tmp_file, 'w') as fh:
                fh.write(buf)
            shutil.move(tmp_file, src_file)