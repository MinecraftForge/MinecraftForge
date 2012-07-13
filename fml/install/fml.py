import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re

def download_deps(mcp_path):
    bin_path = os.path.normpath(os.path.join(mcp_path, 'runtime', 'bin'))
    ff_path = os.path.normpath(os.path.join(bin_path, 'fernflower.jar'))
    ret = True
    
    if not os.path.isfile(ff_path):
        try:
            urllib.urlretrieve("http://goo.gl/PnJHp", 'fernflower.zip')
            zf = zipfile.ZipFile('fernflower.zip')
            zf.extract('fernflower.jar', bin_path)
            zf.close()
            os.remove('fernflower.zip')
            print "Downloaded Fernflower successfully"
        except:
            print "Downloading Fernflower failed download manually from http://goo.gl/PnJHp"
            ret = False
    
    for lib in ["argo-2.25.jar", "guava-12.0.jar", "asm-all-4.0.jar"]:
        target = os.path.normpath(os.path.join(mcp_path, 'lib', lib))
        if not os.path.isfile(target):
            try:
                urllib.urlretrieve('http://cloud.github.com/downloads/cpw/FML/' + lib, target)
                print 'Downloaded %s successfully' % lib
            except:
                print 'Download %s failed, download manually from http://cloud.github.com/downloads/cpw/FML/%s and place in MCP/libs' % (lib, lib)
                ret = False
    
    return ret
        
def pre_decompile(mcp_dir, fml_dir):
    bin_dir = os.path.join(mcp_dir, 'jars', 'bin')
    back_jar = os.path.join(bin_dir, 'minecraft.jar.backup')
    src_jar = os.path.join(bin_dir, 'minecraft.jar')
    
    if os.path.isfile(back_jar):
        if os.path.isfile(src_jar):
            os.remove(src_jar)
        shutil.move(back_jar, src_jar)
        
def post_decompile(mcp_dir, fml_dir):
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
    
def reset_logger():
    log = logging.getLogger()
    while len(log.handlers) > 0:
        log.removeHandler(log.handlers[0])
        
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
        
def setup_fml(fml_dir, mcp_dir):
    sys.path.append(mcp_dir)
    from runtime.decompile import decompile
    from runtime.updatemd5 import updatemd5
    from runtime.cleanup import cleanup
    
    src_dir = os.path.join(mcp_dir, 'src')
        
    if os.path.isdir(src_dir):
        os.chdir(mcp_dir)
        #cleanup -f
        cleanup(None, False)
        reset_logger()
        os.chdir(fml_dir)
        
    if os.path.isdir(src_dir):
        print 'Please make sure to backup your modified files, and say yes when it asks you to do cleanup.'
        sys.exit(1)
    
    #download fernflower/argo/asm/guava
    if not download_deps(mcp_dir):
        sys.exit(1)
    
    try:
        pre_decompile(mcp_dir, fml_dir)
        
        os.chdir(mcp_dir)
        #decompile -d -n -r
        #         Conf  JAD    CSV    -r    -d    -a     -n    -p     -o     -l     -g
        decompile(None, False, False, True, True, False, True, False, False, False, False)
        reset_logger()
        os.chdir(fml_dir)
        
        post_decompile(mcp_dir, fml_dir)
        
    except SystemExit, e:
        print 'Decompile Exception: %d ' % e.code
        raise e   

    if not os.path.isdir(src_dir):
        print 'Something went wrong, src folder not found at: %s' % src_dir
        sys.exit(1)
        
    #cleanup_source
    cleanup_source(src_dir)
    
    has_client = os.path.isdir(os.path.join(mcp_dir, 'src', 'minecraft'))
    has_server = os.path.isdir(os.path.join(mcp_dir, 'src', 'minecraft_server'))
    
    #patch files
    print 'Applying Forge ModLoader patches'
    if has_client:
        if os.path.isdir(os.path.join(fml_dir, 'patches', 'minecraft')):
            apply_patches(mcp_dir, os.path.join(fml_dir, 'patches', 'minecraft'), src_dir)
        if os.path.isdir(os.path.join(fml_dir, 'src', 'minecraft')):
            copytree(os.path.join(fml_dir, 'src', 'minecraft'), os.path.join(src_dir, 'minecraft'))
        #delete argo
        shutil.rmtree(os.path.join(src_dir, 'minecraft', 'argo'))
        
    if has_server:
        if os.path.isdir(os.path.join(fml_dir, 'patches', 'minecraft_server')):
            apply_patches(mcp_dir, os.path.join(fml_dir, 'patches', 'minecraft_server'), src_dir)
        if os.path.isdir(os.path.join(fml_dir, 'src', 'minecraft_server')):
            copytree(os.path.join(fml_dir, 'src', 'minecraft_server'), os.path.join(src_dir, 'minecraft_server'))
            
    #updatemd5 -f
    os.chdir(mcp_dir)
    updatemd5(None, True)
    reset_logger()
    os.chdir(fml_dir)
    
    #update workspace
    print 'Fixing MCP Workspace'
    merge_tree(os.path.join(fml_dir, 'eclipse'), os.path.join(mcp_dir, 'eclipse'))
            
def finish_setup_fml(fml_dir, mcp_dir):
    sys.path.append(mcp_dir)
    from runtime.updatenames import updatenames
    from runtime.updatemd5 import updatemd5
    from runtime.updatemcp import updatemcp
    
    os.chdir(mcp_dir)
    updatemcp(None, True)
    reset_logger()
    updatenames(None, True)
    reset_logger()
    updatemd5(None, True)
    reset_logger()
    os.chdir(fml_dir)

def apply_patches(mcp_dir, patch_dir, target_dir):
    sys.path.append(mcp_dir)
    from runtime.pylibs.normlines import normaliselines
    from runtime.commands import cmdsplit
    
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
        
def merge_tree(root_src_dir, root_dst_dir):
    for src_dir, dirs, files in os.walk(root_src_dir):
        dst_dir = src_dir.replace(root_src_dir, root_dst_dir)
        if not os.path.exists(dst_dir):
            os.mkdir(dst_dir)
        for file_ in files:
            src_file = os.path.join(src_dir, file_)
            dst_file = os.path.join(dst_dir, file_)
            if os.path.exists(dst_file):
                os.remove(dst_file)
            shutil.copy(src_file, dst_dir)