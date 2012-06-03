import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')
src_dir = os.path.join(mcp_dir, 'src')

sys.path.append(mcp_dir)
from runtime.pylibs.normlines import normaliselines
from runtime.commands import cmdsplit

def apply_patches(patch_dir, target_dir):
    temp = os.path.abspath('temp.patch')
    cmd = cmdsplit('patch -p2 -i "%s" ' % temp)
    display = True
    
    if os.name == 'nt':
        applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
        cmd = cmdsplit('"%s" -uf -p2 -i "%s"' % (applydiff, temp))
        display = False
    
    for path, _, filelist in os.walk(patch_dir, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patch_dir, path[len(patch_dir)+1:], cur_file))
            if display:
                print 'patching file %s' % os.path.join(path[len(patch_dir)+1:], cur_file)
            normaliselines(patch_file, temp)            
            process = subprocess.Popen(cmd, cwd=target_dir, bufsize=-1)
            process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)
        
#Taken from: http://stackoverflow.com/questions/7545299/distutil-shutil-copytree
def _mkdir(newdir):
    """works the way a good mkdir should :)
        - already exists, silently complete
        - regular file in the way, raise an exception
        - parent directory(ies) does not exist, make them as well
    """
    if os.path.isdir(newdir):
        pass
    elif os.path.isfile(newdir):
        raise OSError("a file with the same name as the desired " \
                      "dir, '%s', already exists." % newdir)
    else:
        head, tail = os.path.split(newdir)
        if head and not os.path.isdir(head):
            _mkdir(head)
        #print "_mkdir %s" % repr(newdir)
        if tail:
            os.mkdir(newdir)
            
#Taken from: http://stackoverflow.com/questions/7545299/distutil-shutil-copytree
def copytree(src, dst, verbose=0, symlinks=False):
    """Recursively copy a directory tree using copy2().

    The destination directory must not already exist.
    If exception(s) occur, an Error is raised with a list of reasons.

    If the optional symlinks flag is true, symbolic links in the
    source tree result in symbolic links in the destination tree; if
    it is false, the contents of the files pointed to by symbolic
    links are copied.

    XXX Consider this example code rather than the ultimate tool.

    """
    
    if verbose == -1:
        verbose = len(os.path.abspath(dst)) - 1
    names = os.listdir(src)
    # os.makedirs(dst)
    _mkdir(dst) # XXX
    errors = []
    for name in names:
        srcname = os.path.join(src, name)
        dstname = os.path.join(dst, name)
        try:
            if symlinks and os.path.islink(srcname):
                linkto = os.readlink(srcname)
                os.symlink(linkto, dstname)
            elif os.path.isdir(srcname):
                copytree(srcname, dstname, verbose, symlinks)
            else:
                shutil.copy2(srcname, dstname)
                if verbose > 0:
                    print os.path.abspath(srcname)[verbose:]
            # XXX What about devices, sockets etc.?
        except (IOError, os.error), why:
            errors.append((srcname, dstname, str(why)))
        # catch the Error from the recursive copytree so that we can
        # continue with other files
        except Exception, err:
            errors.extend(err.args[0])
    try:
        shutil.copystat(src, dst)
    except WindowsError:
        # can't copy file access times on Windows
        pass
        
def reset_logger():
    log = logging.getLogger()
    while len(log.handlers) > 0:
        log.removeHandler(log.handlers[0])
        
def download_ff(mcp_path):
    bin_path = os.path.normpath(os.path.join(mcp_path, 'runtime', 'bin'))
    ff_path = os.path.normpath(os.path.join(bin_path, 'fernflower.jar'))

    if os.path.isfile(ff_path):
        return True
    
    try:
        urllib.urlretrieve("http://goo.gl/PnJHp", 'fernflower.zip')
        zf = zipfile.ZipFile('fernflower.zip')
        zf.extract('fernflower.jar', bin_path)
        zf.close()
        os.remove('fernflower.zip')
        print "Downloaded Fernflower successfully"
        return True
    except:
        print "Downloading Fernflower failed download manually from http://goo.gl/PnJHp"
        return False
    
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
    
count = 0
def cleanup_source(path):
    path = os.path.normpath(path)
    regex_cases_before = re.compile(r'((case|default).+\r?\n)\r?\n', re.MULTILINE) #Fixes newline after case before case body
    regex_cases_after = re.compile(r'\r?\n(\r?\n[ \t]+(case|default))', re.MULTILINE) #Fixes newline after case body before new case
    
    def updatefile(src_file):
        global count
        tmp_file = src_file + '.tmp'
        count = 0
        with open(src_file, 'r') as fh:
            buf = fh.read()
            
        def fix_cases(match):
            global count
            count += 1
            return match.group(1)
            
        buf = regex_cases_before.sub(fix_cases, buf)
        buf = regex_cases_after.sub(fix_cases, buf)
        if count > 0:
            with open(tmp_file, 'w') as fh:
                fh.write(buf)
            shutil.move(tmp_file, src_file)
            
    for path, _, filelist in os.walk(path, followlinks=True):
        sub_dir = os.path.relpath(path, path)
        for cur_file in fnmatch.filter(filelist, '*.java'):
            src_file = os.path.normpath(os.path.join(path, cur_file))
            updatefile(src_file)
            
            
def pre_decompile():
    bin_dir = os.path.join(mcp_dir, 'jars', 'bin')
    back_jar = os.path.join(bin_dir, 'minecraft.jar.backup')
    src_jar = os.path.join(bin_dir, 'minecraft.jar')
    
    if os.path.isfile(back_jar):
        if os.path.isfile(src_jar):
            os.remove(src_jar)
        shutil.move(back_jar, src_jar)
        
def post_decompile():
    print 'Stripping META-INF from minecraft.jar'
    bin_dir = os.path.join(mcp_dir, 'jars', 'bin')
    back_jar = os.path.join(bin_dir, 'minecraft.jar.backup')
    src_jar = os.path.join(bin_dir, 'minecraft.jar')
    
    if os.path.isfile(back_jar):
        os.remove(back_jar)
    
    shutil.move(src_jar, back_jar)
    
    zip_in = zipfile.ZipFile(back_jar, mode='a')
    zip_out = zipfile.ZipFile(src_jar, 'w', zipfile.ZIP_DEFLATED)
    for i in zip_in.filelist:
        if not i.filename.startswith('META-INF'):
            c = zip_in.read(i.filename)
            zip_out.writestr(i.filename, c)
        else:
            print 'Skipping: %s' % i.filename
    zip_out.close()