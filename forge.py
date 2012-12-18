import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re
import csv, shutil
import pprint

forge_dir = os.path.dirname(os.path.abspath(__file__))
        
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
    hook_file = os.path.join(forge_dir, 'common/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep))
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
    import pprint
    files = os.listdir(path)
    for file in files:
        file_path = os.path.join(path, file)
        file_key  = os.path.join(key, file)
        if os.path.isdir(file_path):
            zip_folder(file_path, file_key, zip)
        else:
            if not file_key.replace(os.sep, '/') in zip.NameToInfo:
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
    
    #patch files
    print 'Applying Minecraft Forge patches'
    sys.stdout.flush()

    if os.path.isdir(os.path.join(forge_dir, 'patches', 'minecraft')):
        apply_patches(mcp_dir, os.path.join(forge_dir, 'patches', 'minecraft'), src_dir)
        
    if copy_files and os.path.isdir(os.path.join(forge_dir, 'client')):
        copytree(os.path.join(forge_dir, 'client'), os.path.join(src_dir, 'minecraft'))
    if copy_files and os.path.isdir(os.path.join(forge_dir, 'common')):
        copytree(os.path.join(forge_dir, 'common'), os.path.join(src_dir, 'minecraft'))

def build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num=0):
    version = load_version(build_num)
    print '=================================== Build %d.%d.%d.%d Start =================================' % (version['major'], version['minor'], version['revision'], version['build'])
    
    src_dir = os.path.join(mcp_dir, 'src')
    if os.path.isdir(src_dir):
        shutil.rmtree(src_dir)
    
    sys.path.append(fml_dir)
    from fml import copytree
        
    print 'src_work -> src'
    copytree(os.path.join(mcp_dir, 'src_work'), src_dir)
    print '\nCopying Client Code'
    copytree(os.path.join(forge_dir, 'client'), os.path.join(src_dir, 'minecraft'), -1)
    print '\nCopying Common Code'
    copytree(os.path.join(forge_dir, 'common'), os.path.join(src_dir, 'minecraft'), -1)
    print
    inject_version(os.path.join(src_dir, 'minecraft/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep)), build_num)
    
    error_level = 0
    try:
        sys.path.append(mcp_dir)
        from runtime.recompile import recompile
        
        os.chdir(mcp_dir)
        reset_logger()
        recompile(None, True, False)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        if not e.code == 0:
            print 'Recompile Exception: %d ' % e.code
            error_level = e.code
        
    print '=================================== Build Finished %d =================================' % error_level
    return error_level