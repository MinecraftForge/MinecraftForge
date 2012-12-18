import sys
import os
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
    with open(list_file, 'w') as fh:
        write_changed(fh, os.path.join(mcp_root,"temp","client.md5"), os.path.join(mcp_root,"temp","client_reobf.md5"), 'minecraft')
        write_changed(fh, os.path.join(mcp_root,"temp","server.md5"), os.path.join(mcp_root,"temp","server_reobf.md5"), 'minecraft_server')
    
def write_changed(fh, pre, post, name):
    if not os.path.isfile(pre) or not os.path.isfile(post):
        print 'MD5s Missing! Can not extract %s changed files' % name
        return
        
    cmd = 'diff --unchanged-group-format='' --old-group-format='' --new-group-format=\'%%>\' --changed-group-format=\'%%>\' %s %s' % (pre, post)
    process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, bufsize=-1)
    difflist,_= process.communicate()
    srg_data = parse_srg(os.path.join(mcp_root,"temp","client_rg.srg"))
    classes = {}
    for row in srg_data['CL']:
      classes[row['deobf_name']] = row['obf_name']

    for diff in difflist.splitlines():
        diffrow=diff.strip().split()
        clazz=diffrow[0]
        if clazz in classes:
            clazz=classes[clazz]
        if clazz.startswith("net/minecraft/src/"):
            clazz=clazz[len("net/minecraft/src/"):]
        fh.write("%s/%s.class\n" %(name,clazz))
    
if __name__ == '__main__':
    main()
