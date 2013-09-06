import os
import sys
import fnmatch
import shlex
import difflib
import time, subprocess
from optparse import OptionParser

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)
    
def main():
    print("Applying patches")
    
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to MCP', default=None)
    parser.add_option('-p', '--patch-dir', action='store', dest='patch_dir', help='Patches Directory', default=None)
    parser.add_option('-t', '--target-dir', action='store', dest='target_dir', help='Target Directory', default=None)
    options, _ = parser.parse_args()
        
    if options.mcp_dir is None:
        print 'Must supply MCP directory with --mcp-dir'
        return
    elif options.patch_dir is None:
        print 'Must supply patches directory with --patch-dir'
        return
    elif options.target_dir is None:
        print 'Must supplt target directory with --target-dir'
        return
        
    print "Applying patches from '%s' to '%s'" % (options.patch_dir, options.target_dir)
    apply_patches(options.mcp_dir, options.patch_dir, options.target_dir)
        
def apply_patches(mcp_dir, patch_dir, target_dir, find=None, rep=None):
    # Attempts to apply a directory full of patch files onto a target directory.
    sys.path.append(mcp_dir)
    
    temp = os.path.abspath('temp.patch')
    cmd = cmdsplit('patch -p2 -i "%s" ' % temp)
    
    if os.name == 'nt':
        applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
        cmd = cmdsplit('"%s" -uf -p2 -i "%s"' % (applydiff, temp))
    
    for path, _, filelist in os.walk(patch_dir, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patch_dir, path[len(patch_dir)+1:], cur_file))
            target_file = os.path.join(target_dir, fix_patch(patch_file, temp, find, rep))
            process = subprocess.Popen(cmd, cwd=target_dir, bufsize=-1)
            process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)  

def fix_patch(in_file, out_file, find=None, rep=None):
    #Fixes the following issues in the patch file if they exist:
    #  Normalizes the path seperators for the current OS
    #  Normalizes the line endings
    # Returns the path that the file wants to apply to
    
    in_file = os.path.normpath(in_file)
    if out_file is None:
        tmp_file = in_file + '.tmp'
    else:
        out_file = os.path.normpath(out_file)
        tmp_file = out_file
        dir_name = os.path.dirname(out_file)
        if dir_name:
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
                
    file = 'not found'
    with open(in_file, 'rb') as inpatch:
        with open(tmp_file, 'wb') as outpatch:
            for line in inpatch:
                line = line.rstrip('\r\n')
                if line[:3] in ['+++', '---', 'Onl', 'dif']:
                    if not find == None and not rep == None:
                        line = line.replace('\\', '/').replace(find, rep).replace('/', os.sep)
                    else:
                        line = line.replace('\\', '/').replace('/', os.sep)
                    outpatch.write(line + os.linesep)
                else:
                    outpatch.write(line + os.linesep)
                if line[:3] == '---':
                    file = line[line.find(os.sep, line.find(os.sep)+1)+1:]
                    
    if out_file is None:
        shutil.move(tmp_file, in_file)
    return file
    
if __name__ == '__main__':
    main()
