import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)
                    
def main():
    print("Applying patches")
    patches = os.path.abspath(sys.argv[1])
    work = os.path.normpath(sys.argv[2])
    
    for path, _, filelist in os.walk(patches, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patches, path[len(patches)+1:], cur_file))
            print(patch_file)
            cmd = 'patch -p1 -i "%s" ' % (patch_file)
            process = subprocess.Popen(cmdsplit(cmd), cwd=work, bufsize=-1)
            process.communicate()
    
if __name__ == '__main__':
    main()
