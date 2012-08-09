import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

from forge import setup_forge_mcp, apply_forge_patches

def main():
    print '=================================== Setup Start ================================='
    dont_gen_conf = '-no_gen_conf' in sys.argv
    setup_fml(dont_gen_conf)
    
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
    
    #Restore mcp/conf.bak, therefore restoring normal MCP updating ability
    if not dont_gen_conf:
        mcp_conf = os.path.join(mcp_dir, 'conf')
        mcp_conf_bak = os.path.join(mcp_dir, 'conf.bak')
        
        if os.path.isdir(mcp_conf):
            print 'Removing new conf folder'
            shutil.rmtree(mcp_conf)
        
        print 'Restoreing original MCP Conf'
        os.rename(mcp_conf_bak, mcp_conf)
    
    print '=================================== Setup Finished ================================='
    
def setup_fml(dont_gen_conf):        
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
    fml_main(fml_dir, mcp_dir, dont_gen_conf)

if __name__ == '__main__':
    main()