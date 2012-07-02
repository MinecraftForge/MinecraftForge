import sys
import os
import commands
import fnmatch
import re
import subprocess, shlex

mcp_home = sys.argv[1]
mcp_dir = os.path.abspath(mcp_home)

print(mcp_dir)
sys.path.append(mcp_dir)

from runtime.commands import Commands
Commands._version_config = os.path.join(mcp_dir,Commands._version_config)

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
    print("Obtaining version information from git")
    cmd = "git describe --long --match='[^(jenkins)]*'"
    try:
      process = subprocess.Popen(cmdsplit(cmd), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
      vers, _ = process.communicate()
    except OSError:
      print("Git not found")
      vers="v1.0-0-deadbeef"
    (major,minor,rev,githash)=re.match("v(\d+).(\d+)-(\d+)-(.*)",vers).groups()

    (mcpversion,mcclientversion,mcserverversion) = re.match("[.\w]+ \(data: ([.\w]+), client: ([.\w.]+), server: ([.\w.]+)\)",Commands.fullversion()).groups()
    
    with open("fmlversion.properties","w") as f:
      f.write("%s=%s\n" %("fmlbuild.major.number",major))
      f.write("%s=%s\n" %("fmlbuild.minor.number",minor))
      f.write("%s=%s\n" %("fmlbuild.revision.number",rev))
      f.write("%s=%s\n" %("fmlbuild.githash",githash))
      f.write("%s=%s\n" %("fmlbuild.mcpversion",mcpversion))
      f.write("%s=%s\n" %("fmlbuild.mcclientversion",mcclientversion))
      f.write("%s=%s\n" %("fmlbuild.mcserverversion",mcserverversion))

    print("Version information: FML %s.%s.%s using MCP %s for c:%s, s:%s" % (major, minor, rev, mcpversion, mcclientversion, mcserverversion))
    
if __name__ == '__main__':
    main()
