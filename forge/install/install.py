import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

sys.path.append(mcp_dir)
from runtime.decompile import decompile
from runtime.updatenames import updatenames
from runtime.updatemd5 import updatemd5
from runtime.cleanup import cleanup
from runtime.updatemcp import updatemcp

from forge import apply_patches, copytree, reset_logger, download_ff, cleanup_source, pre_decompile, post_decompile

def main():
    print '=================================== Minecraft Forge Setup Start ================================='
    
    if os.path.isdir(os.path.join(mcp_dir, 'conf')):
        shutil.rmtree(os.path.join(mcp_dir, 'conf'))
    copytree(os.path.join(forge_dir, 'conf'), os.path.join(mcp_dir, 'conf'))
    
    if os.path.isdir(src_dir):
        os.chdir(mcp_dir)
        cleanup(None, False)
        reset_logger()
        os.chdir(forge_dir)
        
    if os.path.isdir(src_dir):
        print 'Please make sure to backup your modified files, and say yes when it asks you to do cleanup.'
        sys.exit(1)
    
    if not download_ff(mcp_dir):
        sys.exit(1)
    
    try:
        pre_decompile()
        
        os.chdir(mcp_dir)
        #         Conf  JAD    CSV    -r    -d    -a     -n    -p     -o     -l     -g
        decompile(None, False, False, True, True, False, True, False, False, False, False)
        reset_logger()
        os.chdir(forge_dir)
        
        post_decompile()
        
    except SystemExit, e:
        print 'Decompile Exception: %d ' % e.code
        raise e   

    if not os.path.isdir(src_dir):
        print 'Something went wrong, src folder not found at: %s' % src_dir
        sys.exit(1)
        
    cleanup_source(src_dir)
    
    has_client = os.path.isdir(os.path.join(mcp_dir, 'src', 'minecraft'))
    has_server = os.path.isdir(os.path.join(mcp_dir, 'src', 'minecraft_server'))
    
    fml_dir = os.path.join(forge_dir, 'fml')
    print 'Applying Forge ModLoader patches'
    if has_client:
        if os.path.isdir(os.path.join(fml_dir, 'patches', 'minecraft')):
            apply_patches(os.path.join(fml_dir, 'patches', 'minecraft'), src_dir)
        if os.path.isdir(os.path.join(fml_dir, 'src', 'minecraft')):
            copytree(os.path.join(fml_dir, 'src', 'minecraft'), os.path.join(src_dir, 'minecraft'))
    if has_server:
        if os.path.isdir(os.path.join(fml_dir, 'patches', 'minecraft_server')):
            apply_patches(os.path.join(fml_dir, 'patches', 'minecraft_server'), src_dir)
        if os.path.isdir(os.path.join(fml_dir, 'src', 'minecraft_server')):
            copytree(os.path.join(fml_dir, 'src', 'minecraft_server'), os.path.join(src_dir, 'minecraft_server'))
    
    os.chdir(mcp_dir)
    updatenames(None, True)
    reset_logger()
    os.chdir(forge_dir)
        
    print 'Applying forge patches'
    if has_client:
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'minecraft')):
            apply_patches(os.path.join(forge_dir, 'patches', 'minecraft'), src_dir)
        if os.path.isdir(os.path.join(forge_dir, 'src', 'minecraft')):
            copytree(os.path.join(forge_dir, 'src', 'minecraft'), os.path.join(src_dir, 'minecraft'))
    if has_server:
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'minecraft_server')):
            apply_patches(os.path.join(forge_dir, 'patches', 'minecraft_server'), src_dir)
        if os.path.isdir(os.path.join(forge_dir, 'src', 'minecraft_server')):
            copytree(os.path.join(forge_dir, 'src', 'minecraft_server'), os.path.join(src_dir, 'minecraft_server'))
    
    os.chdir(mcp_dir)
    updatemcp(None, True)
    reset_logger()
    updatenames(None, True)
    reset_logger()
    updatemd5(None, True)
    reset_logger()
    os.chdir(forge_dir)
    
    print '=================================== Minecraft Forge Setup Finished ================================='
    
if __name__ == '__main__':
    main()