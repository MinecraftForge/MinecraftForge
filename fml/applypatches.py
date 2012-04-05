import sys
import os, os.path, shutil
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
    mcp = os.path.normpath(sys.argv[3])
    temp = os.path.abspath('temp.patch')
    
    cmd = cmdsplit('patch -p2 -i "%s" ' % temp)
    display = True
    
    if os.name == 'nt':
        mcp = os.path.abspath(os.path.join(mcp, 'runtime', 'bin', 'applydiff.exe'))
        cmd = cmdsplit('"%s" -uf -p2 -i "%s"' % (mcp, temp))
        display = False
    
    for path, _, filelist in os.walk(patches, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patches, path[len(patches)+1:], cur_file))
            if display:
                print 'patching file %s' % os.path.join(path[len(patches)+1:], cur_file)
            normaliselines(patch_file, temp)            
            process = subprocess.Popen(cmd, cwd=work, bufsize=-1)
            process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)
        
#Taken from MCP
def normaliselines(in_filename, out_filename=None):
    in_filename = os.path.normpath(in_filename)
    if out_filename is None:
        tmp_filename = in_filename + '.tmp'
    else:
        out_filename = os.path.normpath(out_filename)
        tmp_filename = out_filename
        dir_name = os.path.dirname(out_filename)
        if dir_name:
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
    regex_ending = re.compile(r'\r?\n')
    with open(in_filename, 'rb') as in_file:
        with open(tmp_filename, 'wb') as out_file:
            buf = in_file.read()
            if os.linesep == '\r\n':
                buf = regex_ending.sub(r'\r\n', buf)
            else:
                buf = buf.replace('\r\n', '\n')
            out_file.write(buf)
    if out_filename is None:
        shutil.move(tmp_filename, in_filename)
        
if __name__ == '__main__':
    main()
