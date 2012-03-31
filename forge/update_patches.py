import os
import commands
import fnmatch
import re
import subprocess, shlex

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)
                    
def cleanDirs(path):
    if not os.path.isdir(path):
        return
 
    files = os.listdir(path)
    if len(files):
        for f in files:
            fullpath = os.path.join(path, f)
            if os.path.isdir(fullpath):
                cleanDirs(fullpath)
 
    files = os.listdir(path)
    if len(files) == 0:
        os.rmdir(path)
        
def main():
    print 'Creating patches'
    base = os.path.normpath(os.path.join('..', 'src_base'))
    work = os.path.normpath(os.path.join('..', 'src_work'))
    timestamp = re.compile(r'[0-9-]* [0-9:\.]* [+-][0-9]*\r?\n')
    
    for path, _, filelist in os.walk(work, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.java'):
            #print cur_file + " " + path[12:]
            file_base = os.path.normpath(os.path.join(base, path[12:], cur_file)).replace(os.path.sep, '/')	#this is ok on every platform we support
            file_work = os.path.normpath(os.path.join(work, path[12:], cur_file)).replace(os.path.sep, '/')
            patch = ''
            cmd = 'diff -u %s %s -r --strip-trailing-cr --new-file' % (file_base, file_work)
            process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
            patch, _ = process.communicate()
            patch_dir = os.path.join('patches', path[12:])
            patch_file = os.path.join(patch_dir, cur_file + '.patch')
            
            if len(patch) > 0:
                print patch_file
                patch = timestamp.sub("0000-00-00 00:00:00.000000000 -0000\n", patch)
                patch = patch.replace('\r\n', '\n')
                
                if not os.path.exists(patch_dir):
                    os.makedirs(patch_dir)
                with open(patch_file, 'wb') as fh:
                    fh.write(patch)
            else:
                if os.path.isfile(patch_file):
                    print 'Deleting empty patch: ' + patch_file
                    os.remove(patch_file)
                    

    cleanDirs('patches')
    
if __name__ == '__main__':
    main()