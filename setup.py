import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

from forge import apply_forge_patches

def main():
    print '=================================== Setup Start ================================='
    setup_fml()
    
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
    apply_forge_patches(os.path.join(forge_dir, 'fml'), mcp_dir, forge_dir, work_dir, False)
    
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
    
    sys.path.append(fml_dir)
    from install import fml_main
    fml_main(fml_dir, mcp_dir)

if __name__ == '__main__':
    main()