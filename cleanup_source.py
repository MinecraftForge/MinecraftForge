import sys
import os, os.path, shutil
import fnmatch
import re
                    
def main():
    sys.stdout.flush()
    for x in range(1, len(sys.argv):
        cleanup_source(sys.argv[x])
        
count = 0
def cleanup_source(path):
    print 'Cleaning %s' % path
    path = os.path.normpath(path)
    regex_cases = re.compile(r'\r?\n(\r?\n[ \t]+case)', re.MULTILINE)
    
    def updatefile(src_file):
        global count
        tmp_file = src_file + '.tmp'
        count = 0
        with open(src_file, 'r') as fh:
            buf = fh.read()
            
        def fix_cases(match):
            global count
            count += 1
            return match.group(1)
            
        buf = regex_cases.sub(fix_cases, buf)
        if count > 0:
            with open(tmp_file, 'w') as fh:
                fh.write(buf)
            shutil.move(tmp_file, src_file)
            
    for path, _, filelist in os.walk(path, followlinks=True):
        sub_dir = os.path.relpath(path, path)
        for cur_file in fnmatch.filter(filelist, '*.java'):
            src_file = os.path.normpath(os.path.join(path, cur_file))
            updatefile(src_file)
            
if __name__ == '__main__':
    main()
