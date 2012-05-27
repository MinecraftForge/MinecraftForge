import os, os.path, sys
import shutil, fnmatch
import logging, zipfile

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

sys.path.append(mcp_dir)
from runtime.reobfuscate import reobfuscate

from forge import reset_logger, load_version, zip_folder, zip_create, inject_version
from build import build

reobf_dir = os.path.join(mcp_dir, 'reobf')
client_dir = os.path.join(reobf_dir, 'minecraft')
server_dir = os.path.join(reobf_dir, 'minecraft_server')
zip = None
zip_name = None
zip_base = None
version_str = None

def main():
    global version_str
    build_num = 0
    if len(sys.argv) > 1:
        try:
            build_num = int(sys.argv[1])
        except:
            pass
    ret = 0
    ret = build(build_num)
    if ret != 0:
        sys.exit(ret)
    
    print '=================================== Release Start ================================='
    error_level = 0
    try:
        os.chdir(mcp_dir)
        reset_logger()
        reobfuscate(None, False, True, True)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        print 'Reobfusicate Exception: %d ' % e.code
        error_level = e.code
    
    extract_fml_obfed()
    version = load_version(build_num)
    version_str = '%d.%d.%d.%d' % (version['major'], version['minor'], version['revision'], version['build'])
        
    out_folder = os.path.join(forge_dir, 'forge-%s' % version_str)
    if os.path.isdir(out_folder):
        shutil.rmtree(out_folder)
        
    os.makedirs(out_folder)
    
    zip_start('minecraftforge-client-%s.zip' % version_str)
    zip_folder(client_dir, '', zip)
    zip_add('forge_common/mod_MinecraftForge.info', 'mod_MinecraftForge.info')
    zip_add('MinecraftForge-Credits.txt')
    zip_add('MinecraftForge-License.txt')
    zip_end()
    
    zip_start('minecraftforge-server-%s.zip' % version_str)
    zip_folder(server_dir, '', zip)
    zip_add('forge_common/mod_MinecraftForge.info', 'mod_MinecraftForge.info')
    zip_add('MinecraftForge-Credits.txt')
    zip_add('MinecraftForge-License.txt')
    zip_end()
    
    inject_version(os.path.join(forge_dir, 'forge_common', 'net', 'minecraft', 'src', 'forge', 'ForgeHooks.java'), build_num)
    zip_start('minecraftforge-src-%s.zip' % version_str, 'forge')
    zip_add('forge_client/src', 'src/minecraft')
    zip_add('forge_server/src', 'src/minecraft_server')
    zip_add('forge_common',     'src/minecraft')
    zip_add('forge_common',     'src/minecraft_server')
    zip_add('patches',          'patches')
    zip_add('fml',              'fml')
    zip_add('conf',             'conf')
    zip_add('install/install.cmd')
    zip_add('install/install.sh')
    zip_add('install/README-MinecraftForge.txt')
    zip_add('install/install.py')
    zip_add('forge.py')
    zip_add('MinecraftForge-Credits.txt')
    zip_add('MinecraftForge-License.txt')
    zip_end()
    inject_version(os.path.join(forge_dir, 'forge_common', 'net', 'minecraft', 'src', 'forge', 'ForgeHooks.java'), 0)
    
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
            
def zip_add_perm(file, perm, key=None):
    if key == None:
        key = os.path.basename(file)
    else:
        key = key.replace('/', os.sep)
    if not zip_base is None:
        key = os.path.join(zip_base, key)
    file = os.path.join(forge_dir, file.replace('/', os.sep))
    if os.path.isfile(file):
        print key   
        #zip.write(file, key)
        
        with open(file, 'r') as fh: data = fh.read()
        info = zipfile.ZipInfo(key)
        info.external_attr = 0777 << 16L
        zip.writestr(info, data)
    
def zip_start(name, base=None):
    global zip, zip_name, zip_base
    zip_name = name
    
    print '=================================== %s Start =================================' % zip_name
    zip_file = os.path.join(forge_dir, 'forge-%s' % version_str, name)
    zip = zipfile.ZipFile(zip_file, 'w', zipfile.ZIP_DEFLATED)
    zip_base = base
    
def zip_end():
    global zip, zip_name, zip_base
    zip.close()
    print '=================================== %s Finished =================================' % zip_name
    zip_name = None
    zip_base = None
    
def extract_fml_obfed():
    fml_file = os.path.join(forge_dir, 'fml', 'difflist.txt')
    if not os.path.isfile(fml_file):
        print 'Could not find Forge ModLoader\'s DiffList, looking for it at: %s' % fml_file
        sys.exit(1)
        
    with open(fml_file, 'r') as fh:
        lines = fh.readlines()
        
    client = zipfile.ZipFile(os.path.join(mcp_dir, 'temp', 'client_reobf.jar'))
    server = zipfile.ZipFile(os.path.join(mcp_dir, 'temp', 'server_reobf.jar'))
    
    print 'Extracting Reobfed Forge ModLoader classes'
    lines.append("minecraft/net/minecraft/client/MinecraftApplet.class") #Needed because users dont install Forge properly -.-
    
    for line in lines:
        line = line.replace('\n', '').replace('\r', '').replace('/', os.sep)
        print line
        if not os.path.isfile(os.path.join(reobf_dir, line)):
            side = line.split(os.sep)[0]
            if side == 'minecraft':
                client.extract(line[10:].replace(os.sep, '/'), client_dir)
            else:
                server.extract(line[17:].replace(os.sep, '/'), server_dir)
        
    client.close()
    server.close()
    
if __name__ == '__main__':
    main()
