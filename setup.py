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

from forge import apply_patches, copytree, reset_logger, download_ff, cleanup_source, pre_decompile, post_decompile


def main():
    print '=================================== Setup Start ================================='
    
    skipDecompile = len(sys.argv) > 1 and sys.argv[1] == '-skipdecompile'
    if not skipDecompile:
        if not download_ff(mcp_dir):
            sys.exit(1)
        
        if os.path.isdir(src_dir):
            shutil.rmtree(src_dir)
            
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
    
    setup_fml()
    
    os.chdir(mcp_dir)
    updatenames(None, True)
    reset_logger()
    updatemd5(None, True)
    reset_logger()
    os.chdir(forge_dir)
    
    base_dir = os.path.join(mcp_dir, 'src_base')
    work_dir = os.path.join(mcp_dir, 'src_work')
    
    if os.path.isdir(base_dir):
        shutil.rmtree(base_dir)
    if os.path.isdir(work_dir):
        shutil.rmtree(work_dir)
        
    print 'Setting up source directories'    
    shutil.copytree(src_dir, base_dir)
    shutil.copytree(src_dir, work_dir)
    
    print 'Applying forge patches'
    apply_patches(os.path.join(forge_dir, 'patches'), work_dir)
    
    print '=================================== Setup Finished ================================='
    
def setup_fml():        
    print 'Setting up Forge ModLoader'
    fml = glob.glob(os.path.join(forge_dir, 'fml-src-*.zip'))
    if not len(fml) == 1:
        if len(fml) == 0:
            print 'Missing FML source zip, should be named fml-src-*.zip inside your forge folder, obtain it from the repo'
        else:
            print 'To many FML source zips found, we should only have one. Check the Forge Git for the latest FML version supported'
        sys.exit(1)
        
    fml_dir = os.path.join(forge_dir, 'fml')
    if os.path.isdir(fml_dir):
        shutil.rmtree(fml_dir)
        
    print 'Extracting: %s' % os.path.basename(fml[0]) 
    
    zf = zipfile.ZipFile(fml[0])
    zf.extractall(forge_dir)
    zf.close()
    
    print 'Applying Forge ModLoader patches'
    apply_patches(os.path.join(fml_dir, 'patches'), src_dir)
    copytree(os.path.join(fml_dir, 'src'), src_dir)    

if __name__ == '__main__':
    main()