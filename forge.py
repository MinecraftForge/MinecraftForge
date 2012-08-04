import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')
sys.path.append(mcp_dir)
        
def reset_logger():
    log = logging.getLogger()
    while len(log.handlers) > 0:
        log.removeHandler(log.handlers[0])
    
version_reg = re.compile(r'(([a-z]+)Version[\s]+=[\s]+)(\d+);')       
def load_version(build=0):
    info = {'major'    : -1,
            'minor'    : -1,
            'revision' : -1,
            'build'    : -1
           }
    hook_file = os.path.join(forge_dir, 'forge_common/net/minecraft/src/forge/ForgeHooks.java'.replace('/', os.sep))
    with open(hook_file, 'r') as fh:
        buf = fh.read() 
        
    def proc(match):
        try:
            info[match.group(2)] = int(match.group(3))
        except:
            pass
        return match.group(0)
        
    buf = version_reg.sub(proc, buf)
    info['build'] = build
    return info

def inject_version(src_file, build=0):
    version = load_version(build) 

    tmp_file = src_file + '.tmp'
    with open(src_file, 'r') as fh:
        buf = fh.read()            
    
    def mapname(match):
        try:
            return '%s%s;' % (match.group(1), version[match.group(2)])
        except KeyError:
            pass
        return match.group(0)
        
    buf = version_reg.sub(mapname, buf).replace('\r\n', '\n')
    
    with open(tmp_file, 'wb') as fh:
        fh.write(buf)
    shutil.move(tmp_file, src_file)
    
def zip_folder(path, key, zip):
    files = os.listdir(path)
    for file in files:
        file_path = os.path.join(path, file)
        file_key  = os.path.join(key, file)
        if os.path.isdir(file_path):
            zip_folder(file_path, file_key, zip)
        else:
            print file_key
            zip.write(file_path, file_key)
            
def zip_create(path, key, zip_name):
    zip = zipfile.ZipFile(zip_name, 'w', zipfile.ZIP_DEFLATED)
    if os.path.isdir(path):
        zip_folder(path, key, zip)
    else:
        zip.write(path, key)
    zip.close()
    

def apply_forge_patches(fml_dir, mcp_dir, forge_dir, src_dir, copy_files=True):
    sys.path.append(fml_dir)
    from fml import copytree, apply_patches
    
    has_client = os.path.isdir(os.path.join(src_dir, 'minecraft'))
    has_server = os.path.isdir(os.path.join(src_dir, 'minecraft_server'))
    
    #patch files
    print 'Applying Minecraft Forge patches'
    sys.stdout.flush()
    if has_client:
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'minecraft')):
            apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'minecraft'), src_dir)
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'common')):
            apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'common'), src_dir, '/common/', '/minecraft/')
        if copy_files and os.path.isdir(os.path.join(forge_dir, 'client')):
            copytree(os.path.join(forge_dir, 'client'), os.path.join(src_dir, 'minecraft'))
        
    if has_server:
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'minecraft_server')):
            apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'minecraft_server'), src_dir)
        if os.path.isdir(os.path.join(forge_dir, 'patches', 'common')):
            apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'common'), src_dir, '/common/', '/minecraft_server/')
        if copy_files and os.path.isdir(os.path.join(forge_dir, 'server')):
            copytree(os.path.join(forge_dir, 'server'), os.path.join(src_dir, 'minecraft_server'))
            
    if os.path.isdir(os.path.join(forge_dir, 'patches', 'common')):
        apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'common'), src_dir)
    if copy_files and os.path.isdir(os.path.join(forge_dir, 'common')):
        copytree(os.path.join(forge_dir, 'common'), os.path.join(src_dir, 'common'))