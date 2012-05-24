import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex

mcp_root = os.path.abspath(sys.argv[1])
sys.path.append(os.path.join(mcp_root,"runtime"))
from filehandling.srgshandler import parse_srg

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)

def main():
    list_file = os.path.abspath(sys.argv[2])
    prelist = os.path.join(mcp_root,"temp","server.md5")
    postlist = os.path.join(mcp_root,"temp","server_reobf.md5")
    cmd = 'diff --unchanged-group-format='' --old-group-format='' --new-group-format=\'%%>\' --changed-group-format=\'%%>\' %s %s' % (prelist, postlist)
    process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, bufsize=-1)
    difflist,_= process.communicate()
    srg_data = parse_srg(os.path.join(mcp_root,"temp","server_rg.srg"))
    classes = {}
    for row in srg_data['CL']:
      classes[row['deobf_name']] = row['obf_name']

    with open(list_file, 'w') as fh:
      for diff in difflist.splitlines():
        diffrow=diff.strip().split()
        clazz=diffrow[0]
        if clazz in classes:
          clazz=classes[clazz]
        if clazz.startswith("net/minecraft/src/"):
          clazz=clazz[len("net/minecraft/src/"):]
        fh.write("minecraft_server/%s.class\n" %(clazz))

    prelist = os.path.join(mcp_root,"temp","client.md5")
    postlist = os.path.join(mcp_root,"temp","client_reobf.md5")
    cmd = 'diff --unchanged-group-format='' --old-group-format='' --new-group-format=\'%%>\' --changed-group-format=\'%%>\' %s %s' % (prelist, postlist)
    process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, bufsize=-1)
    difflist,_= process.communicate()
    srg_data = parse_srg(os.path.join(mcp_root,"temp","client_rg.srg"))
    classes = {}
    for row in srg_data['CL']:
      classes[row['deobf_name']] = row['obf_name']

    with open(list_file, 'a') as fh:
      for diff in difflist.splitlines():
        diffrow=diff.strip().split()
        clazz=diffrow[0]
        if clazz in classes:
          clazz=classes[clazz]
        if clazz.startswith("net/minecraft/src/"):
          clazz=clazz[len("net/minecraft/src/"):]
        fh.write("minecraft/%s.class\n" %(clazz))


    
if __name__ == '__main__':
    main()
