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
    md5dir = os.path.abspath(sys.argv[1])
    list_file = os.path.abspath(sys.argv[2])
    prelist = os.path.join(md5dir,"temp","server.md5")
    postlist = os.path.join(md5dir,"temp","server_reobf.md5")
    cmd = 'diff --unchanged-group-format='' --old-group-format='' --new-group-format=\'%%>\' --changed-group-format=\'%%>\' %s %s' % (prelist, postlist)
    process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, bufsize=-1)
    difflist,_= process.communicate()
    with open(list_file, 'w') as fh:
      fh.write(difflist)

    
if __name__ == '__main__':
    main()
