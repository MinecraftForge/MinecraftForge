import os, os.path, sys
import shutil, fnmatch
import logging, zipfile, re

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

sys.path.append(mcp_dir)
from runtime.reobfuscate import reobfuscate

from forge import reset_logger, load_version, zip_folder, zip_create, inject_version, build_forge_dev
from changelog import make_changelog

reobf_dir = os.path.join(mcp_dir, 'reobf')
client_dir = os.path.join(reobf_dir, 'minecraft')
zip = None
zip_name = None
zip_base = None
version_str = None
version_mc = None

def main():
    global version_str
    global version_mc
    
    build_num = 0
    if len(sys.argv) > 1:
        try:
            build_num = int(sys.argv[1])
        except:
            pass
    ret = 0
    fml_dir = os.path.join(forge_dir, 'fml')
    build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num)
    if ret != 0:
        sys.exit(ret)
    
    print '=================================== Release Start ================================='
    error_level = 0
    try:
        os.chdir(mcp_dir)
        reset_logger()
        reobfuscate(None, False, True, True, True, False)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        print 'Reobfusicate Exception: %d ' % e.code
        error_level = e.code
    
    extract_fml_obfed()
    extract_paulscode()
    version = load_version(build_num)
    version_forge = '%d.%d.%d.%d' % (version['major'], version['minor'], version['revision'], version['build'])
    version_mc = load_mc_version(forge_dir)
    
    version_str = '%s-%s' % (version_mc, version_forge)
        
    out_folder = os.path.join(forge_dir, 'forge-%s' % version_str)
    if os.path.isdir(out_folder):
        shutil.rmtree(out_folder)
        
    os.makedirs(out_folder)
    
    changelog_file = 'forge-%s/minecraftforge-changelog-%s.txt' % (version_str, version_str)
    make_changelog("http://jenkins.minecraftforge.net/job/forge/", build_num, changelog_file, version_str)
    
    version_file = 'forgeversion.properties'
    if os.path.exists(version_file):
        os.remove(version_file)
    
    with open(version_file, 'wb') as fh:
        fh.write('forge.major.number=%d\n' % version['major'])
        fh.write('forge.minor.number=%d\n' % version['minor'])
        fh.write('forge.revision.number=%d\n' % version['revision'])
        fh.write('forge.build.number=%d\n' % version['build'])
    
    zip_start('minecraftforge-universal-%s.zip' % version_str)
    zip_folder(client_dir, '', zip)
    zip_add('client/forge_logo.png')
    zip_add('install/MinecraftForge-Credits.txt')
    zip_add('install/MinecraftForge-License.txt')
    zip_add('fml/CREDITS-fml.txt')
    zip_add('fml/LICENSE-fml.txt')
    zip_add('fml/README-fml.txt')
    zip_add('fml/common/fml_at.cfg')
    zip_add('fml/common/fml_marker.cfg')
    zip_add('fml/common/fmlversion.properties')
    zip_add('fml/common/mcpmod.info')
    zip_add('fml/client/mcp.png')
    zip_add('install/Paulscode IBXM Library License.txt')
    zip_add('install/Paulscode SoundSystem CodecIBXM License.txt')
    zip_add('common/forge_at.cfg')
    zip_add(version_file)
    zip_add(changelog_file, 'MinecraftForge-Changelog.txt')
    zip_add('MANIFEST.MF','META-INF/MANIFEST.MF')
    zip_end()
    
    inject_version(os.path.join(forge_dir, 'common/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep)), build_num)
    zip_start('minecraftforge-src-%s.zip' % version_str, 'forge')
    zip_add('client',  'client')
    zip_add('common',  'common')
    zip_add('patches', 'patches')
    zip_add('fml',     'fml')
    zip_add('install/install.cmd')
    zip_add('install/install.sh')
    zip_add('install/README-MinecraftForge.txt')
    zip_add('install/install.py')
    zip_add('forge.py')
    zip_add('install/MinecraftForge-Credits.txt')
    zip_add('install/MinecraftForge-License.txt')
    zip_add('install/Paulscode IBXM Library License.txt')
    zip_add('install/Paulscode SoundSystem CodecIBXM License.txt')
    zip_add(version_file)
    zip_add(changelog_file, 'MinecraftForge-Changelog.txt')
    zip_end()
    inject_version(os.path.join(forge_dir, 'common/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep)), 0)
    
    if os.path.exists(version_file):
        os.remove(version_file)
    
    print '=================================== Release Finished %d =================================' % error_level
    sys.exit(error_level)
    
def zip_add(file, key=None):
    if key == None:
        key = os.path.basename(file)
    else:
        key = key.replace('/', os.sep)
    if not zip_base is None:
        key = os.path.join(zip_base, key)
    file = os.path.join(forge_dir, file.replace('/', os.sep))
    if os.path.isdir(file):
        zip_folder(file, key, zip)
    else:
        if os.path.isfile(file):
            print key
            zip.write(file, key)
    
def zip_start(name, base=None):
    global zip, zip_name, zip_base
    zip_name = name
    
    print '================== %s Start ==================' % zip_name
    zip_file = os.path.join(forge_dir, 'forge-%s' % version_str, name)
    zip = zipfile.ZipFile(zip_file, 'w', zipfile.ZIP_DEFLATED)
    zip_base = base
    
def zip_end():
    global zip, zip_name, zip_base
    zip.close()
    print '================== %s Finished ==================' % zip_name
    zip_name = None
    zip_base = None
    
def load_mc_version(forge_dir):
    props = os.path.join(forge_dir, 'fml', 'common', 'fmlversion.properties')
    
    if not os.path.isfile(props):
        print 'Could not load fmlversion.properties, build failed'
        sys.exit(1)
    
    with open(props, 'r') as fh:
        for line in fh:
            line = line.strip()
            if line.startswith('fmlbuild.mcversion'):
                return line.split('=')[1].strip()
    
    print 'Could not load fmlversion.properties, build failed'
    sys.exit(1)

def extract_fml_obfed():
    fml_file = os.path.join(forge_dir, 'fml', 'difflist.txt')
    if not os.path.isfile(fml_file):
        print 'Could not find Forge ModLoader\'s DiffList, looking for it at: %s' % fml_file
        sys.exit(1)
        
    with open(fml_file, 'r') as fh:
        lines = fh.readlines()
        
    client = zipfile.ZipFile(os.path.join(mcp_dir, 'temp', 'client_reobf.jar'))
    
    print 'Extracting Reobfed Forge ModLoader classes'
    
    for line in lines:
        line = line.replace('\n', '').replace('\r', '').replace('/', os.sep)
        if not os.path.isfile(os.path.join(reobf_dir, line)):
            print '    %s' % line
            side = line.split(os.sep)[0]
            if side == 'minecraft':
                client.extract(line[10:].replace(os.sep, '/'), client_dir)
        
    client.close()
    
def extract_paulscode():
    client = zipfile.ZipFile(os.path.join(mcp_dir, 'temp', 'client_reobf.jar'))
    
    print 'Extracting Reobfed Paulscode for mac users -.-'
    
    for i in client.filelist:
        if i.filename.startswith('paulscode'):
            if not os.path.isfile(os.path.join(client_dir, i.filename)):
                print '   %s' % i.filename
                client.extract(i.filename, client_dir)
        
    client.close()
    
if __name__ == '__main__':
    main()
