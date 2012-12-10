import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging
from optparse import OptionParser

forge_dir = os.path.dirname(os.path.abspath(__file__))
fml_dir = os.path.join(forge_dir, 'fml')

sys.path.append(fml_dir)
from forge import apply_forge_patches
from fml import setup_fml, finish_setup_fml, apply_fml_patches, setup_mcp, reset_logger

def main(mcp_dir):
    src_dir = os.path.join(mcp_dir, 'src')
    
    print '================ Forge ModLoader Setup Start ==================='
    setup_mcp(fml_dir, mcp_dir, True)
    setup_fml(fml_dir, mcp_dir)
    apply_fml_patches(fml_dir, mcp_dir, os.path.join(mcp_dir, 'src'))
    finish_setup_fml(fml_dir, mcp_dir)
    print '================  Forge ModLoader Setup End  ==================='

    sys.path.append(mcp_dir)    
    from runtime.updatenames import updatenames
    from runtime.updatemd5 import updatemd5
    
    print '=============================== Minecraft Forge Setup Start ====================================='
    print 'Applying forge patches'
    apply_forge_patches(os.path.join(forge_dir, 'fml'), mcp_dir, forge_dir, src_dir, True)
    os.chdir(mcp_dir)
    updatenames(None, True, True, False)
    reset_logger()
    updatemd5(None, True, True, False)
    reset_logger()
    os.chdir(forge_dir)    
    print '=============================== Minecraft Forge Setup Finished ================================='
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to download/extract MCP to', default=None)
    options, _ = parser.parse_args()

    if not options.mcp_dir is None:
        main(os.path.abspath(options.mcp_dir))
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        main(os.path.abspath('..'))
    else:
        main(os.path.abspath('mcp'))
