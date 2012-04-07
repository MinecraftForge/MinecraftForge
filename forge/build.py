import os, os.path, sys
import shutil, fnmatch
import logging

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

sys.path.append(mcp_dir)
from runtime.recompile import recompile

from forge import copytree, reset_logger, load_version, inject_version

def main():
    build_num = 0
    if len(sys.argv) > 1:
        try:
            build_num = int(sys.argv[1])
        except:
            pass
    sys.exit(build(build_num))
    
def build(build_num=0):
    version = load_version(build_num)
    print '=================================== Build %d.%d.%d.%d Start =================================' % (version['major'], version['minor'], version['revision'], version['build'])
    
    if os.path.isdir(src_dir):
        shutil.rmtree(src_dir)
    inject_version(os.path.join(forge_dir, 'forge_common', 'net', 'minecraft', 'src', 'forge', 'ForgeHooks.java'), build_num)
        
    print 'src_work -> src'
    copytree(os.path.join(mcp_dir, 'src_work'), src_dir)
    print '\nforge_client -> minecraft'
    copytree(os.path.join(forge_dir, 'forge_client', 'src'), os.path.join(src_dir, 'minecraft'), -1)
    print '\nforge_server -> minecraft_server'
    copytree(os.path.join(forge_dir, 'forge_server', 'src'), os.path.join(src_dir, 'minecraft_server'), -1)
    print '\nforge_common -> minecraft'
    copytree(os.path.join(forge_dir, 'forge_common'), os.path.join(src_dir, 'minecraft'), -1)
    print '\nforge_common -> minecraft_server'
    copytree(os.path.join(forge_dir, 'forge_common'), os.path.join(src_dir, 'minecraft_server'), -1)
    print
    
    error_level = 0
    try:
        os.chdir(mcp_dir)
        recompile(None)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        print 'Decompile Exception: %d ' % e.code
        error_level = e.code
        
    print '=================================== Build Finished %d =================================' % error_level
    return error_level
    
if __name__ == '__main__':
    main()