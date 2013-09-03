import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging
from optparse import OptionParser

def fml_main(fml_dir, mcp_dir, gen_conf=True, disable_patches=False, disable_at=False, disable_merge=False, enable_server=False, 
            disable_client=False, disable_rename=False, disable_assets=False, decompile=False):
    sys.path.append(fml_dir)
    from fml import download_mcp, setup_mcp, decompile_minecraft, apply_fml_patches, finish_setup_fml
    print '================ Forge ModLoader Setup Start ==================='
    download_mcp(fml_dir, mcp_dir)
    setup_mcp(fml_dir, mcp_dir, gen_conf)
    if decompile:
        decompile_minecraft(fml_dir, mcp_dir, disable_at=disable_at, disable_merge=disable_merge, 
              enable_server=enable_server, disable_client=disable_client,
              disable_assets=disable_assets)
        if disable_patches:
            print 'Patching disabled'
        else:
            apply_fml_patches(fml_dir, mcp_dir, os.path.join(mcp_dir, 'src'))
        finish_setup_fml(fml_dir, mcp_dir, enable_server=enable_server, disable_client=disable_client, disable_rename=disable_rename)
    else:
        print 'Decompile free install is on the to-do!'
    print '================  Forge ModLoader Setup End  ==================='
    
def forge_main(forge_dir, fml_dir, mcp_dir):
    sys.path.append(mcp_dir)
    sys.path.append(fml_dir)
    from runtime.updatenames import updatenames
    from runtime.updatemd5 import updatemd5
    from forge import apply_forge_patches
    from fml import reset_logger
    
    print '=============================== Minecraft Forge Setup Start ====================================='
    print 'Applying forge patches'
    apply_forge_patches(fml_dir, mcp_dir, forge_dir, os.path.join(mcp_dir, 'src'), True)
    os.chdir(mcp_dir)
    updatenames(None, True, True, False)
    reset_logger()
    updatemd5(None, True, True, False)
    reset_logger()
    os.chdir(forge_dir)    
    print '=============================== Minecraft Forge Setup Finished ================================='

if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir',   action='store',      dest='mcp_dir',       help='Path to download/extract MCP to',         default=None )
    parser.add_option('-p', '--no-patch',  action="store_true", dest='no_patch',      help='Disable application of FML patches',      default=False)
    parser.add_option('-a', '--no-access', action="store_true", dest='no_access',     help='Disable access transformers',             default=False)
    parser.add_option('-s', '--server',    action="store_true", dest='enable_server', help='Enable decompilation of server',          default=False)
    parser.add_option('-c', '--no-client', action="store_true", dest='no_client',     help='Disable decompilation of server',         default=False)
    parser.add_option('-e', '--no-merge',  action="store_true", dest='no_merge',      help='Disable merging server code into client', default=False)
    parser.add_option('-n', '--no-rename', action="store_true", dest='no_rename',     help='Disable running updatenames',             default=False)
    parser.add_option(      '--no-assets', action="store_true", dest='no_assets',     help='Disable downloading of assets folder',    default=False)
    parser.add_option('-d', '--decompile', action="store_true", dest='decompile',     help='Decompile minecraft and apply patches',   default=True)
    options, _ = parser.parse_args()
    
    forge_dir = os.path.dirname(os.path.abspath(__file__))
    fml_dir = os.path.abspath('fml')
    mcp_dir = os.path.abspath('mcp')

    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
    
    if options.no_client:
        options.no_patch = True
        
    if options.no_merge:
        options.no_patch = True
    
    fml_main(fml_dir, mcp_dir, disable_patches=options.no_patch, 
        disable_at=options.no_access, disable_merge=options.no_merge,
        enable_server=options.enable_server, disable_client=options.no_client,
        disable_rename=options.no_rename, disable_assets=options.no_assets,
        decompile=options.decompile, gen_conf=False)
        
    forge_main(forge_dir, fml_dir, mcp_dir)