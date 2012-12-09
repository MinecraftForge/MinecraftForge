import os, os.path, sys, zipfile
import shutil, glob, fnmatch
from optparse import OptionParser

forge_dir = os.path.dirname(os.path.abspath(__file__))

from forge import apply_forge_patches

def main():
    print '=================================== Setup Start ================================='
    
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to download/extract MCP to', default=None)
    parser.add_option('-n', '--no-extract', action='store_true', dest='no_extract', help='Do not attempt to extract FML zip files', default=False)
    options, _ = parser.parse_args()
    
    fml_dir = os.path.join(forge_dir, 'fml')
    mcp_dir = os.path.join(forge_dir, 'mcp')

    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        mcp_dir = os.path.abspath('..')
        
    src_dir = os.path.join(mcp_dir, 'src')
    
    setup_fml(mcp_dir, fml_dir, options.no_extract)
    
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
    
    setup_eclipse(forge_dir)
    
    print '=================================== Setup Finished ================================='
    
def setup_fml(mcp_dir, fml_dir, dont_extract=False):        
    print 'Setting up Forge ModLoader'
    if not dont_extract:
        fml = glob.glob(os.path.join(forge_dir, 'fml-src-*.zip'))
        if not len(fml) == 1:
            if len(fml) == 0:
                print 'Missing FML source zip, should be named fml-src-*.zip inside your forge folder, obtain it from the repo'
            else:
                print 'To many FML source zips found, we should only have one. Check the Forge Git for the latest FML version supported'
            sys.exit(1)
        
        if os.path.isdir(fml_dir):
            shutil.rmtree(fml_dir)
            
        print 'Extracting: %s' % os.path.basename(fml[0]) 
        
        zf = zipfile.ZipFile(fml[0])
        zf.extractall(forge_dir)
        zf.close()
    
    sys.path.append(fml_dir)
    from install import fml_main
    fml_main(fml_dir, mcp_dir, True)

def setup_eclipse(forge_dir):
    eclipse_dir = os.path.join(forge_dir, 'eclipse')
    eclipse_zip = os.path.join(forge_dir, 'eclipse-workspace-dev.zip')
    
    if not os.path.isdir(eclipse_dir) and os.path.isfile(eclipse_zip):
        print 'Extracting Dev Workspace'
        zf = zipfile.ZipFile(eclipse_zip)
        zf.extractall(forge_dir)
        zf.close()
        
if __name__ == '__main__':
    main()