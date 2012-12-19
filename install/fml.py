import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re, shlex
import csv, ConfigParser
from hashlib import md5  # pylint: disable-msg=E0611
from pprint import pprint
from zipfile import ZipFile
from pprint import pprint

mcp_version = '7.23'

def download_deps(mcp_path):
    ret = True
    for lib in ['argo-2.25.jar', 'guava-12.0.1.jar', 'guava-12.0.1-sources.jar', 'asm-all-4.0.jar', 'asm-all-4.0-source.jar', 'bcprov-jdk15on-147.jar']:
        libF = os.path.join(mcp_path, 'lib')
        if not os.path.isdir(libF):
            os.makedirs(libF)
            
        target = os.path.normpath(os.path.join(libF, lib))
        
        if not os.path.isfile(target):
            try:
                urllib.urlretrieve('http://files.minecraftforge.net/fmllibs/' + lib, target)
                print 'Downloaded %s' % lib
            except:
                print 'Download %s failed, download manually from http://files.minecraftforge.net/fmllibs/%s or http://files.minecraftforge.net/fmllibs/fml_libs_dev.zip and place in MCP/lib' % (lib, lib)
                ret = False
    
    return ret

def config_get_section(config, section):
    dict = {}
    options = config.options(section)
    for option in options:
        try:
            dict[option] = config.get(section, option)
        except:
            dict[option] = None
    return dict

def download_file(url, target, md5=None):
    name = os.path.basename(target)
    if os.path.isfile(target) and not md5 == None:
        if not get_md5(target) == md5:
            print 'Modified %s detected, removing' % name
            os.remove(target)
    
    if not os.path.isfile(target):
        try:
            urllib.urlretrieve(url, target)
            if not md5 == None:
                if not get_md5(target) == md5:
                    print 'Download of %s failed md5 check, deleting' % name
                    os.remove(target)
                    return False
            print 'Downloaded %s' % name
        except Exception as e:
            print e
            print 'Download of %s failed, download it manually from \'%s\' to \'%s\'' % (target, url, target)
            return False
    else:
        print 'File Exists: %s' % os.path.basename(target)
    return True
    
def download_native(url, folder, name):
    if not os.path.exists(folder):
        os.makedirs(folder)
    
    target = os.path.join(folder, name)
    if not download_file(url + name, target):
        return False
    
    zip = ZipFile(target)
    for name in zip.namelist():
        if not name.startswith('META-INF') and not name.endswith('/'):
            out_file = os.path.join(folder, name)
            if not os.path.isfile(out_file):
                print '    Extracting %s' % name
                out = open(out_file, 'wb')
                out.write(zip.read(name))
                out.flush()
                out.close()
    zip.close()
    return True 
    
def download_minecraft(mcp_dir, fml_dir, version=None):
    versions_file = os.path.join(fml_dir, 'mc_versions.cfg')
    if not os.path.isfile(versions_file):
        print 'Could not find mc_versions.cfg in FML directory.'
        sys.exit(1)
    
    config = ConfigParser.ConfigParser()
    config.read(versions_file)
    
    default = config_get_section(config, 'default')
    if version is None:
        version = default['current_ver']    
    
    bin_folder = os.path.join(mcp_dir, 'jars', 'bin')
    if not os.path.exists(bin_folder):
        os.makedirs(bin_folder)
        
    failed = False

    for lib in default['libraries'].split(' '):
        failed = not download_file(default['base_url'] + lib, os.path.join(bin_folder, lib)) or failed
    for native in default['natives'].split(' '):
        failed = not download_native(default['base_url'], os.path.join(bin_folder, 'natives'), native) or failed
    
    if not config.has_section(version):
        print 'Error: Invalid minecraft version, could not find \'%s\' in mc_versions.cfg' % version
        sys.exit(1)
    
    mc_info = config_get_section(config, version)
    
    client_jar = os.path.join(bin_folder, 'minecraft.jar')
    server_jar = os.path.join(mcp_dir, 'jars', 'minecraft_server.jar')
    
    # Remove any invalid files
    file_backup(os.path.join(mcp_dir, 'jars', 'bin'), 'minecraft.jar', mc_info['client_md5'])
    file_backup(os.path.join(mcp_dir, 'jars'), 'minecraft_server.jar', mc_info['server_md5'])
        
    failed = not download_file(mc_info['client_url'], client_jar, mc_info['client_md5']) or failed
    failed = not download_file(mc_info['server_url'], server_jar, mc_info['server_md5']) or failed
    
    # Backup clean jars, or delete if corrupted
    file_backup(os.path.join(mcp_dir, 'jars', 'bin'), 'minecraft.jar', mc_info['client_md5'])
    file_backup(os.path.join(mcp_dir, 'jars'), 'minecraft_server.jar', mc_info['server_md5'])
    
    if failed:
        print 'Something failed verifying minecraft files, see log for details.'
        sys.exit(1)

def get_md5(file):
    if not os.path.isfile(file):
        return ""
    with open(file, 'rb') as fh:
        return md5(fh.read()).hexdigest()

def pre_decompile(mcp_dir, fml_dir):
    download_minecraft(mcp_dir, fml_dir)
    
def file_backup(base, file, md5):
    bck_jar = os.path.join(base, file + '.backup')
    src_jar = os.path.join(base, file)
    
    if not os.path.isfile(src_jar) and not os.path.isfile(bck_jar):
        return
    
    if os.path.isfile(bck_jar):
        if get_md5(bck_jar) == md5:
            if os.path.isfile(src_jar):
                os.remove(src_jar)
            shutil.move(bck_jar, src_jar)
        else:
            os.remove(bck_jar)
    
    if os.path.isfile(src_jar):
        if not get_md5(src_jar) == md5:
            print 'Modified %s detected, removing' % os.path.basename(src_jar)
            os.remove(src_jar)
        else:
            shutil.copy(src_jar, bck_jar)
        
def post_decompile(mcp_dir, fml_dir):
    bin_dir = os.path.join(mcp_dir, 'jars', 'bin')
    back_jar = os.path.join(bin_dir, 'minecraft.jar.backup')
    src_jar = os.path.join(bin_dir, 'minecraft.jar')
    
    if not os.path.isfile(src_jar):
        return
        
    print 'Stripping META-INF from minecraft.jar'
    
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
    from runtime.cleanup import cleanup
    from runtime.commands import Commands, CLIENT, SERVER
    
    src_dir = os.path.join(mcp_dir, 'src')
        
    if os.path.isdir(src_dir):
        os.chdir(mcp_dir)
        cleanup(None, False)
        reset_logger()
        os.chdir(fml_dir)
        
    if os.path.isdir(src_dir):
        print 'Please make sure to backup your modified files, and say yes when it asks you to do cleanup.'
        sys.exit(1)
    
    #download fernflower/argo/asm/guava
    if not download_deps(mcp_dir):
        sys.exit(1)
    
    def applyrg_shunt(self, side, reobf=False, applyrg_real = Commands.applyrg):
        if not self.has_wine and not self.has_astyle:
                self.logger.error('!! Please install either wine or astyle for source cleanup !!')
                self.logger.error('!! This is REQUIRED by FML/Forge Cannot proceed !!')
                sys.exit(1)
        jars = {CLIENT: self.jarclient, SERVER: self.jarserver}
        
        dir_bin = os.path.join(fml_dir, 'bin')
        if not os.path.isdir(dir_bin):
            os.makedirs(dir_bin)
        
        class_path = os.pathsep.join(['.', os.path.join(mcp_dir, 'lib', '*'), dir_bin])
        dir_common = os.path.join(fml_dir, 'common')
        dir_trans  = os.path.join(dir_common, 'cpw', 'mods', 'fml', 'common', 'asm', 'transformers')
        cmd_compile = '"%s" -Xlint:-options -deprecation -g -source 1.6 -target 1.6 -classpath "{classpath}" -sourcepath "{sourcepath}" -d "{outpath}" "{target}"' % self.cmdjavac
        cmd_compile = cmd_compile.format(classpath=class_path, sourcepath=dir_common, outpath=dir_bin, target="{target}")
        
        if (side == CLIENT):
            self.logger.info('> Compiling AccessTransformer')
            if not runcmd(self, cmd_compile.format(target=os.path.join(dir_trans, 'AccessTransformer.java')), echo=False):
                sys.exit(1)
                
            self.logger.info('> Compiling MCPMerger')
            if not runcmd(self, cmd_compile.format(target=os.path.join(dir_trans, 'MCPMerger.java')), echo=False):
                sys.exit(1)
            
            self.logger.info('> Running MCPMerger')
            forkcmd = ('"%s" -classpath "{classpath}" cpw.mods.fml.common.asm.transformers.MCPMerger "{mergecfg}" "{client}" "{server}"' % self.cmdjava).format(
                classpath=class_path, mergecfg=os.path.join(fml_dir, 'mcp_merge.cfg'), client=jars[CLIENT], server=jars[SERVER])
                
            if not runcmd(self, forkcmd):
                sys.exit(1)
        
        self.logger.info('> Running AccessTransformer')
        forkcmd = ('"%s" -classpath "{classpath}" cpw.mods.fml.common.asm.transformers.AccessTransformer "{jar}" "{fmlconfig}"' % self.cmdjava).format(
            classpath=class_path, jar=jars[side], fmlconfig=os.path.join(fml_dir, 'common', 'fml_at.cfg'))
            
        forge_cfg = os.path.join(fml_dir, '..', 'common', 'forge_at.cfg')
        if os.path.isfile(forge_cfg):
            self.logger.info('   Forge config detected')
            forkcmd += ' "%s"' % forge_cfg

        for dirname, dirnames, filenames in os.walk(os.path.join(fml_dir, '..', 'accesstransformers')):
            for filename in filenames:
                accesstransformer = os.path.join(dirname, filename)
                if os.path.isfile(accesstransformer):              
                    self.logger.info('   Access Transformer "%s" detected' % filename)
                    forkcmd += ' "%s"' % accesstransformer
        
        if not runcmd(self, forkcmd):
            sys.exit(1)
        
        self.logger.info('> Really Applying Retroguard')
        applyrg_real(self, side, reobf)
    
    #Check the original jars not the transformed jars
    def checkjars_shunt(self, side, checkjars_real = Commands.checkjars):
        self.jarclient = self.jarclient + '.backup'
        self.jarserver = self.jarserver + '.backup'
        #print 'Jar Check %s %s %s' % (side, self.jarclient, self.jarserver)
        ret = checkjars_real(self, side)
        self.jarclient = self.jarclient[:-7]
        self.jarserver = self.jarserver[:-7]
        #print 'Jar Check out %s %s %s' % (side, self.jarclient, self.jarserver)
        return ret
    
    try:
        pre_decompile(mcp_dir, fml_dir)
        
        os.chdir(mcp_dir)
        Commands.applyrg = applyrg_shunt
        Commands.checkjars = checkjars_shunt
        #decompile -d -n -r
        #         Conf  JAD    CSV    -r    -d    -a     -n    -p     -o     -l     -g     -c     -s
        decompile(None, False, False, True, True, False, True, False, False, False, False, True, False)
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
    
    os.chdir(mcp_dir)
    commands = Commands(verify=True)
    updatemd5_side(mcp_dir, commands, CLIENT)
    reset_logger()
    
    os.chdir(fml_dir)
    
def updatemd5_side(mcp_dir, commands, side):
    sys.path.append(mcp_dir)
    from runtime.mcp import recompile_side, updatemd5_side
    from runtime.commands import SIDE_NAME
    
    recomp = recompile_side(commands, side)
    if recomp:
        commands.logger.info('> Generating %s md5s', SIDE_NAME[side])
        commands.gathermd5s(side, skip_fml=True)

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)
    
def runcmd(commands, command, echo=True):
    forklist = cmdsplit(command)
    process = subprocess.Popen(forklist, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
    output, _ = process.communicate()    
    
    if echo:
        for line in output.splitlines():
            commands.logger.info(line)

    if process.returncode:
        if not echo:        
            for line in output.splitlines():
                commands.logger.info(line)
        return False
    return True

def get_joined_srg(mcp_dir):
    joined = os.path.join(mcp_dir, 'conf', 'joined.srg')
    values = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    
    if not os.path.isfile(joined):
        print 'Could not read joined.srg, file not found'
        sys.exit(1)
    else:
        with open(joined, 'r') as fh:
            for line in fh:
                pts = line.rstrip('\r\n').split(' ')
                if pts[0] == 'MD:':
                    values[pts[0]][pts[1] + ' ' + pts[2]] = pts[3] + ' ' + pts[4]
                else:
                    values[pts[0]][pts[1]] = pts[2]
    
    return values

def apply_fml_patches(fml_dir, mcp_dir, src_dir, copy_files=True):
    #Delete /minecraft/cpw to get rid of the Side/SideOnly classes used in decompilation
    cpw_mc_dir = os.path.join(src_dir, 'minecraft', 'cpw')
    if os.path.isdir(cpw_mc_dir):
        shutil.rmtree(cpw_mc_dir)
        
    #patch files
    print 'Applying Forge ModLoader patches'
    sys.stdout.flush()

    if os.path.isdir(os.path.join(fml_dir, 'patches', 'minecraft')):
        apply_patches(mcp_dir, os.path.join(fml_dir, 'patches', 'minecraft'), src_dir)
    if copy_files and os.path.isdir(os.path.join(fml_dir, 'client')):
        copytree(os.path.join(fml_dir, 'client'), os.path.join(src_dir, 'minecraft'))
    if copy_files and os.path.isdir(os.path.join(fml_dir, 'common')):
        copytree(os.path.join(fml_dir, 'common'), os.path.join(src_dir, 'minecraft'))

    #delete argo
    if os.path.isdir(os.path.join(src_dir, 'minecraft', 'argo')):
        shutil.rmtree(os.path.join(src_dir, 'minecraft', 'argo'))

def finish_setup_fml(fml_dir, mcp_dir):
    sys.path.append(mcp_dir)
    from runtime.updatenames import updatenames
    from runtime.updatemd5 import updatemd5
    from runtime.updatemcp import updatemcp
    
    os.chdir(mcp_dir)
    updatenames(None, True, True, False)
    reset_logger()
    updatemd5(None, True, True, False)
    reset_logger()
    os.chdir(fml_dir)

def apply_patches(mcp_dir, patch_dir, target_dir, find=None, rep=None):
    sys.path.append(mcp_dir)
    from runtime.commands import cmdsplit
    
    temp = os.path.abspath('temp.patch')
    cmd = cmdsplit('patch -p2 -i "%s" ' % temp)
    
    if os.name == 'nt':
        applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
        cmd = cmdsplit('"%s" -uf -p2 -i "%s"' % (applydiff, temp))
    
    for path, _, filelist in os.walk(patch_dir, followlinks=True):
        for cur_file in fnmatch.filter(filelist, '*.patch'):
            patch_file = os.path.normpath(os.path.join(patch_dir, path[len(patch_dir)+1:], cur_file))
            target_file = os.path.join(target_dir, fix_patch(patch_file, temp, find, rep))
            process = subprocess.Popen(cmd, cwd=target_dir, bufsize=-1)
            process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)

def fix_patch(in_file, out_file, find=None, rep=None):
    in_file = os.path.normpath(in_file)
    if out_file is None:
        tmp_filename = in_file + '.tmp'
    else:
        out_file = os.path.normpath(out_file)
        tmp_file = out_file
        dir_name = os.path.dirname(out_file)
        if dir_name:
            if not os.path.exists(dir_name):
                os.makedirs(dir_name)
    file = 'not found'
    with open(in_file, 'rb') as inpatch:
        with open(tmp_file, 'wb') as outpatch:
            for line in inpatch:
                line = line.rstrip('\r\n')
                if line[:3] in ['+++', '---', 'Onl', 'dif']:
                    if not find == None and not rep == None:
                        line = line.replace('\\', '/').replace(find, rep).replace('/', os.sep)
                    else:
                        line = line.replace('\\', '/').replace('/', os.sep)
                    outpatch.write(line + os.linesep)
                else:
                    outpatch.write(line + os.linesep)
                if line[:3] == '---':
                    file = line[line.find(os.sep, line.find(os.sep)+1)+1:]
                    
    if out_file is None:
        shutil.move(tmp_file, in_file)
    return file
        
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
        verbose = len(os.path.abspath(dst)) + 1
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
                    print os.path.abspath(dstname)[verbose:]
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
        
def cleanDirs(path):
    if not os.path.isdir(path):
        return
 
    files = os.listdir(path)
    if len(files):
        for f in files:
            fullpath = os.path.join(path, f)
            if os.path.isdir(fullpath):
                cleanDirs(fullpath)
 
    files = os.listdir(path)
    if len(files) == 0:
        os.rmdir(path)
        
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

def download_mcp(mcp_dir, fml_dir, version=None):
    if os.path.isfile(os.path.join(mcp_dir, 'runtime', 'commands.py')):
        print 'MCP Detected already, not downloading'
        return True
        
    if os.path.isdir(mcp_dir):
        print 'Old MCP Directory exists, but MCP was not detected, please delete MCP directory at \'%s\'' % mcp_dir
        sys.exit(1)
        
    versions_file = os.path.join(fml_dir, 'mc_versions.cfg')
    if not os.path.isfile(versions_file):
        print 'Could not find mc_versions.cfg in FML directory.'
        sys.exit(1)
    
    config = ConfigParser.ConfigParser()
    config.read(versions_file)
    
    default = config_get_section(config, 'default')
    if version is None:
        version = default['current_ver']
    
    if not config.has_section(version):
        print 'Error: Invalid minecraft version, could not find \'%s\' in mc_versions.cfg' % version
        sys.exit(1)

    mc_info = config_get_section(config, version)
    mcp_zip = os.path.join(fml_dir, 'mcp%s.zip' % mc_info['mcp_ver'])
    
    if not download_file(mc_info['mcp_url'], mcp_zip, mc_info['mcp_md5']):
        sys.exit(1)
        
    if not os.path.isdir(mcp_dir):
        _mkdir(mcp_dir)
        
    print 'Extracting MCP to \'%s\'' % mcp_dir
    zf = ZipFile(mcp_zip)
    zf.extractall(mcp_dir)
    zf.close()
    
    eclipse_dir = os.path.join(mcp_dir, 'eclipse')
    if os.path.isdir(eclipse_dir):
        shutil.rmtree(eclipse_dir)
    
    if os.name != 'nt':
        for path, _, filelist in os.walk(mcp_dir):
            for cur_file in fnmatch.filter(filelist, '*.sh'):
              file_name = os.path.join(path, cur_file)
              process = subprocess.Popen(cmdsplit('chmod +x "%s"' % file_name), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
              output, _ = process.communicate()
             
        process = subprocess.Popen(cmdsplit('chmod +x "%s/runtime/bin/astyle-osx"' % mcp_dir), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        output, _ = process.communicate()
        
    return True
    
def setup_mcp(fml_dir, mcp_dir, dont_gen_conf=True):
    global mcp_version
    
    if not download_mcp(mcp_dir, fml_dir):
        sys.exit(1)
    
    backup = os.path.join(mcp_dir, 'runtime', 'commands.py.bck')
    runtime = os.path.join(mcp_dir, 'runtime', 'commands.py')
    patch = os.path.join(fml_dir, 'commands.patch')
    
    print 'Setting up MCP'
    if os.path.isfile(backup):
        print 'Restoring commands.py backup'
        if os.path.exists(runtime):
            os.remove(runtime)
        shutil.copy(backup, runtime)
    else:
        print 'Backing up commands.py'
        shutil.copy(runtime, backup)
    
    if not os.path.isfile(patch):
        raise Exception('Commands.py patch not found %s' % patch)
        return
        
    temp = os.path.abspath('temp.patch')
    cmd = 'patch -i "%s" ' % temp
    
    if os.name == 'nt':
        applydiff = os.path.abspath(os.path.join(mcp_dir, 'runtime', 'bin', 'applydiff.exe'))
        cmd = '"%s" -uf -i "%s"' % (applydiff, temp)
        
    if os.sep == '\\':
        cmd = cmd.replace('\\', '\\\\')
    cmd = shlex.split(cmd)
    
    fix_patch(patch, temp)
    process = subprocess.Popen(cmd, cwd=os.path.join(mcp_dir, 'runtime'), bufsize=-1)
    process.communicate()

    if os.path.isfile(temp):
        os.remove(temp)
    
    try:
        sys.path.append(mcp_dir)
        from runtime.commands import commands_sanity_check
        commands_sanity_check()
    except ImportError as ex:
        print 'Could not verify commands.py patch integrity, this typically means that you are not in a clean MCP environment.'
        print 'Download a clean version of MCP %s and try again' % mcp_version
        print ex
        sys.exit(1)
    
    mcp_conf = os.path.join(mcp_dir, 'conf')
    mcp_conf_bak = os.path.join(mcp_dir, 'conf.bak')
    fml_conf = os.path.join(fml_dir, 'conf')
    
    if not dont_gen_conf:
        if os.path.isdir(mcp_conf_bak):
            print 'Reverting old conf backup folder'
            shutil.rmtree(mcp_conf)
            os.rename(mcp_conf_bak, mcp_conf)

        get_conf_copy(mcp_dir, fml_dir)

        print 'Backing up MCP Conf'
        os.rename(mcp_conf, mcp_conf_bak)
    else:
        shutil.rmtree(mcp_conf)
    
    print 'Copying FML conf'
    shutil.copytree(fml_conf, mcp_conf)
    
    gen_renamed_conf(mcp_dir, fml_dir)
    
    #update workspace
    if not os.path.isfile(os.path.join(fml_dir, 'fmlbuild.properties-sample')):
        mcp_eclipse = os.path.join(mcp_dir, 'eclipse')
        
        if os.path.isdir(os.path.join(mcp_eclipse, 'Client')) and os.path.isdir(os.path.join(mcp_eclipse, 'Server')):
            shutil.rmtree(mcp_eclipse)
            
        if not os.path.isdir(mcp_eclipse) and os.path.isdir(os.path.join(fml_dir, 'eclipse')):
            print 'Fixing MCP Workspace'
            copytree(os.path.join(fml_dir, 'eclipse'), mcp_eclipse)

def normaliselines(in_filename):
    in_filename = os.path.normpath(in_filename)
    tmp_filename = in_filename + '.tmp'

    with open(in_filename, 'rb') as in_file:
        with open(tmp_filename, 'wb') as out_file:
            out_file.write(in_file.read().replace('\r\n', '\n'))

    shutil.move(tmp_filename, in_filename)

def get_conf_copy(mcp_dir, fml_dir):
    #Lets grab the files we dont work on
    for file in ['astyle.cfg', 'version.cfg', 'newids.csv']:
        dst_file = os.path.normpath(os.path.join(fml_dir, 'conf', file))
        src_file = os.path.normpath(os.path.join(mcp_dir, 'conf', file))
        if not os.path.isdir(os.path.dirname(dst_file)):
            os.makedirs(os.path.dirname(dst_file))
        if os.path.exists(dst_file):
            os.remove(dst_file)
        shutil.copy(src_file, dst_file)
        normaliselines(dst_file)
        print 'Grabbing: ' + src_file

    common_srg = gen_merged_srg(mcp_dir, fml_dir)
    common_exc = gen_merged_exc(mcp_dir, fml_dir)
    common_map = gen_shared_searge_names(common_srg, common_exc)
    
    gen_merged_csv(common_map, os.path.join(mcp_dir, 'conf', 'fields.csv'), os.path.join(fml_dir, 'conf', 'fields.csv'))
    gen_merged_csv(common_map, os.path.join(mcp_dir, 'conf', 'methods.csv'), os.path.join(fml_dir, 'conf', 'methods.csv'))
    gen_merged_csv(common_map, os.path.join(mcp_dir, 'conf', 'params.csv'), os.path.join(fml_dir, 'conf', 'params.csv'), main_key='param')
    
def gen_merged_srg(mcp_dir, fml_dir):
    print 'Generating merged Retroguard data'
    srg_client = os.path.join(mcp_dir, 'conf', 'client.srg')
    srg_server = os.path.join(mcp_dir, 'conf', 'server.srg')
    
    if not os.path.isfile(srg_client) or not os.path.isfile(srg_server):
        print 'Could not find client and server srg files in "%s"' % mcp_dir
        return False
        
    client = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    with open(srg_client, 'r') as fh:
        for line in fh:
            pts = line.rstrip('\r\n').split(' ')
            if pts[0] == 'MD:':
                client[pts[0]][pts[1] + ' ' + pts[2]] = pts[3] + ' ' + pts[4]
            else:
                client[pts[0]][pts[1]] = pts[2]
    
    server = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    with open(srg_server, 'r') as fh:
        for line in fh:
            pts = line.rstrip('\r\n').split(' ')
            if pts[0] == 'MD:':
                server[pts[0]][pts[1] + ' ' + pts[2]] = pts[3] + ' ' + pts[4]
            else:
                server[pts[0]][pts[1]] = pts[2]
    
    common = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    for type in common:
        for key, value in client[type].items():
            if key in server[type]:
                if value == server[type][key]:
                    client[type].pop(key)
                    server[type].pop(key)
                    common[type][key] = value

    for type in common:
        for key, value in client[type].items():
            common[type][key] = value #+ ' #C'
            
    for type in common:
        for key, value in server[type].items():
            common[type][key] = value #+ ' #S'
            
    #Print joined retroguard files
    if fml_dir is None:
        return common

    with open(os.path.join(fml_dir, 'conf', 'joined.srg'), 'wb') as f:
        for type in ['PK:', 'CL:', 'FD:', 'MD:']:
            for key in sorted(common[type]):
                f.write('%s %s %s\n' % (type, key, common[type][key]))
    
    return common

def gen_merged_exc(mcp_dir, fml_dir):
    print 'Reading merged MCInjector config'
    exc_joined = os.path.join(mcp_dir, 'conf', 'joined.exc')
    
    joined = {}
    with open(exc_joined, 'r') as fh:
        for line in fh:
            if not line.startswith('#'):
                pts = line.rstrip('\r\n').split('=')
                joined[pts[0]] = pts[1]
    
    return joined

def gen_shared_searge_names(common_srg, common_exc):
    field = re.compile(r'field_[0-9]+_[a-zA-Z_]+$')
    method = re.compile(r'func_[0-9]+_[a-zA-Z_]+')
    param = re.compile(r'p_[\w]+_\d+_')
    
    print 'Gathering list of common searge names'
    
    searge = []
    
    for key, value in common_srg['FD:'].items():
        m = field.search(value)
        if not m is None:
            if not m.group(0) in searge:
                searge.append(m.group(0))
                
    for key, value in common_srg['MD:'].items():
        m = method.search(value)
        if not m is None and not '#' in value:
            if not m.group(0) in searge:
                searge.append(m.group(0))
                
    for key, value in common_exc.items():
        m = param.findall(value)
        if not m is None:
            for p in m:
                if not p in searge:
                    searge.append(p)
                    
    return searge

def gen_merged_csv(common_map, in_file, out_file, main_key='searge'):
    reader = csv.DictReader(open(in_file, 'r'))
    print 'Generating merged csv for %s' % os.path.basename(in_file)
    common = []
    added = []
    for row in reader:
        if not row[main_key] in added:
            row['side'] = '2'
            common.append(row)
            added.append(row[main_key])
    writer = csv.DictWriter(open(out_file, 'wb'), fieldnames=reader.fieldnames, lineterminator='\n')
    writer.writeheader()
    for row in sorted(common, key=lambda row: row[main_key]):
        writer.writerow(row)

def repackage_class(pkgs, cls):
    if cls.startswith('net/minecraft/src/'):
        tmp = cls[18:]
        if tmp in pkgs.keys():
           return '%s/%s' % (pkgs[tmp], tmp)
    return cls

typere = re.compile('([\[ZBCSIJFDV]|L([\w\/]+);)')
def repackage_signature(pkgs, sig):
    global typere
    sig1 = sig
    params = sig.rsplit(')', 1)[0][1:]
    ret = sig.rsplit(')', 1)[1]
    
    sig = '('
    for arg in typere.findall(params):
        if len(arg[1]) > 0:
            sig += 'L%s;' % repackage_class(pkgs, arg[1])
        else:
            sig += arg[0]
    sig += ')'
    for tmp in typere.findall(ret):
        if len(tmp[1]) > 0:
            sig += 'L%s;' % repackage_class(pkgs, tmp[1])
        else:
            sig += tmp[0]
    return sig

def gen_renamed_conf(mcp_dir, fml_dir):
    pkg_file = os.path.join(fml_dir, 'conf', 'packages.csv')
    srg_in = os.path.join(mcp_dir, 'conf', 'joined.srg')
    srg_out = os.path.join(mcp_dir, 'conf', 'packaged.srg')
    exc_in = os.path.join(mcp_dir, 'conf', 'joined.exc')
    exc_out = os.path.join(mcp_dir, 'conf', 'packaged.exc')
    
    pkgs = {}
    if os.path.isfile(pkg_file):
        with open(pkg_file) as fh:
            reader = csv.DictReader(fh)
            for line in reader:
                pkgs[line['class']] = line['package']
    
    print 'Creating re-packaged srg'
    with open(srg_in, 'r') as inf:
        with open(srg_out, 'wb') as outf:
            for line in inf:
                pts = line.rstrip('\r\n').split(' ')
                if pts[0] == 'PK:':
                    outf.write(' '.join(pts) + '\n')
                elif pts[0] == 'CL:':
                    pts[2] = repackage_class(pkgs, pts[2])
                    outf.write('CL: %s %s\n' % (pts[1], pts[2]))
                elif pts[0] == 'FD:':
                    tmp = pts[2].rsplit('/', 1)
                    tmp[0] = repackage_class(pkgs, tmp[0])
                    outf.write('FD: %s %s/%s\n' % (pts[1], tmp[0], tmp[1]))
                elif pts[0] == 'MD:':
                    tmp = pts[3].rsplit('/', 1)
                    pts[3] = '%s/%s' % (repackage_class(pkgs, tmp[0]), tmp[1])
                    pts[4] = repackage_signature(pkgs, pts[4])
                    outf.write('MD: %s %s %s %s\n' % (pts[1], pts[2], pts[3], pts[4]))
                else:
                    print 'Line unknown in SRG: ' + line
                    outf.write(line)
    
    excre = re.compile('([\[ZBCSIJFDV]|L([\w\/]+);)')
    print 'Creating re-packaged exc'
    with open(exc_in, 'r') as inf:
        with open(exc_out, 'wb') as outf:
            for line in inf:
                line = line.rstrip('\r\n')
                cls = line.split('.')[0]
                named = line.rsplit('=', 1)[1]
                line = line[len(cls)+1:-1*len(named)-1]
                func = line.split('(')[0]
                
                tmp = named.split('|', 1)
                if len(tmp[0]) > 0:
                    excs = tmp[0].split(',')
                    for x in range(len(excs)):
                        excs[x] = repackage_class(pkgs, excs[x])
                    named = '%s|%s' % (','.join(excs), tmp[1])
                    
                sig = repackage_signature(pkgs, line[len(func):])
                cls = repackage_class(pkgs, cls)
                outf.write('%s.%s%s=%s\n' % (cls, func, sig, named))
    
    print 'Creating re-packaged MCP patch'
    patch_in = os.path.join(mcp_dir, 'conf', 'patches', 'minecraft_ff.patch')
    patch_tmp = os.path.join(mcp_dir, 'conf', 'patches', 'minecraft_ff.patch.tmp')
    
    regnms = re.compile(r'net\\minecraft\\src\\(\w+)')
    with open(patch_in, 'r') as fh:
        buf = fh.read()
        def mapname(match):
            return repackage_class(pkgs, match.group(0).replace('\\', '/')).replace('/', '\\')
        buf = regnms.sub(mapname, buf)
        
    with open(patch_tmp, 'w') as fh:
        fh.write(buf)
                    
    shutil.move(patch_tmp, patch_in)
