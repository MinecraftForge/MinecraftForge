import os, os.path, sys, zipfile
import shutil, glob, fnmatch, subprocess
from pprint import pformat
from optparse import OptionParser

forge_dir = os.path.dirname(os.path.abspath(__file__))

from forge import apply_forge_patches

def main():
    print '=================================== Setup Start ================================='
    
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='Path to download/extract MCP to', default=None)
    parser.add_option('-b', '--build',   action='store', dest='build',   help='Build number',                    default=None)
    options, _ = parser.parse_args()
    
    build_num = 0
    if not options.build is None:
        try:
            build_num = int(options.build)
        except:
            pass
    
    fml_dir = os.path.join(forge_dir, 'fml')
    mcp_dir = os.path.join(forge_dir, 'mcp')

    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
        
    src_dir = os.path.join(mcp_dir, 'src')
    
    setup_fml(mcp_dir, fml_dir, build_num)
    
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
    apply_forge_patches(fml_dir, mcp_dir, forge_dir, work_dir, False)
    
    setup_eclipse(forge_dir)
    
    print '=================================== Setup Finished ================================='
    
def setup_fml(mcp_dir, fml_dir, build_num=0):
    print 'Setting up Forge ModLoader'
    os.environ['WORKSPACE'] = os.path.join(mcp_dir, '..')
    os.environ['BUILD_NUMBER'] = str(build_num)

    BUILD = ['ant', 'jenkinsbuild']
    if sys.platform.startswith('win'):
        BUILD = ['cmd', '/C'] + BUILD
    
    if not run_command(BUILD, cwd=fml_dir):
        print('Could not setup FML')
        sys.exit(1)
    
    sys.path.append(fml_dir)
    sys.path.append(os.path.join(fml_dir, 'install'))
    from fml import finish_setup_fml
    finish_setup_fml(fml_dir, mcp_dir)
    
    print('Copy resources:')
    copy_files(os.path.join(fml_dir, 'client'), os.path.join(mcp_dir, 'src', 'minecraft'))
    copy_files(os.path.join(fml_dir, 'common'), os.path.join(mcp_dir, 'src', 'minecraft'))

    name = 'fmlversion.properties'
    print('    ' + name)
    shutil.copy(os.path.join(fml_dir, name), os.path.join(mcp_dir, 'src', 'minecraft', name))
    
def copy_files(src_dir, dest_dir):
    for file in glob.glob(os.path.join(src_dir, '*')):
        if not os.path.isfile(file) or file.lower().endswith('.java'):
            continue
        print('    ' + file)
        shutil.copy(file, os.path.join(dest_dir, os.path.basename(file)))
            
def run_command(command, cwd='.', verbose=True):
    print('Running command: ')
    print(pformat(command))
        
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=1, cwd=cwd)
    while process.poll() is None:
        line = process.stdout.readline()
        if line:
            line = line.rstrip()
            print(line)
    if process.returncode:
        print "failed: {0}".format(process.returncode)
        return False
    return True    

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
