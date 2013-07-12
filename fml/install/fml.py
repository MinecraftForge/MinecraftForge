import os, os.path, sys
import urllib, zipfile, json, urllib2
import shutil, glob, fnmatch
import subprocess, logging, re, shlex
import csv, ConfigParser
from hashlib import md5  # pylint: disable-msg=E0611
from pprint import pprint
from zipfile import ZipFile
from pprint import pprint
from contextlib import closing

#==========================================================================
#                      Utility Functions
#==========================================================================
def config_get_section(config, section):
    dict = {}
    options = config.options(section)
    for option in options:
        try:
            dict[option] = config.get(section, option)
        except:
            dict[option] = None
    return dict

def read_mc_versions(fml_dir, version=None, work_dir=None):
    ###########################################################################################################
    #Read mc_versions.cfg from the fml folder for the specified version, or the default 
    #version if none is specified.
    #
    #Should return a dictionary with the following keys:
    # 'new_laucher' True if the version uses the new launcher structure for assets/libraries
    # 'client_url' URL path to the minecraft client jar
    # 'client_md5' MD5 checksum of the client jar
    # 'server_url' URL path to the minecraft server jar
    # 'server_md5' MD5 checksum of the server jar
    # 'json_url' URL path to the json file for this version, or 'None' if new_launcher is false
    # 'mcp_ver' Human readable version for MCP to use for this version of Minecraft
    # 'mcp_url' URL path for MCP to download, or 'None' if there isn't one avalible
    # 'mcp_md5' MD5 checksum of the MCP archive
    # 'mcp_file' File path to the MCP archive
    #
    # If work_dir is specified the following keys also contain values:
    #   'natives_dir' File path to native libraries used by Minecraft
    #   'library_dir' File path to 'libraries' folder
    #       if 'new_launcher' is false this points to work_dir/bin/ and will not be used as maven style folder structure
    #       if 'new_launcher' is true this points to work_dir/libraraies/ and will use the maven style folder structure
    #   'client_file' File path for the client minecraft.jar
    #   'server_file' File path to the server minecraft jar
    #   'json_file' 
    #       if 'new_launcher' is false, this is 'None'
    #       if 'new_launcher' is true, this is the path to the version json file on disc.
    #   'asset_dir' Folder containering all assets/resources used by minecraft
    #
    #   Note: Because a file is give a path it does NOT mean that it exists! So alway check before using
    ############################################################################################################################
    
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
    mc_info['downloads'] = []
    mc_info['version'] = version
    if not 'client_url' in mc_info.keys():
        mc_info['new_launcher'] = True
        base_url = 'http://s3.amazonaws.com/Minecraft.Download/versions/%s' % version
        mc_info['client_url'] = '%s/%s.jar' % (base_url, version)
        mc_info['json_url']   = '%s/%s.json' % (base_url, version)
        mc_info['server_url'] = '%s/minecraft_server.%s.jar' % (base_url, version)
        if not work_dir is None:
            version_dir = os.path.join(work_dir, 'versions', version)
            mc_info['natives_dir'] = os.path.join(version_dir, '%s-natives' % version)
            mc_info['library_dir'] = os.path.join(work_dir, 'libraries')
            mc_info['client_file'] = os.path.join(version_dir, '%s.jar' % version)
            mc_info['json_file']   = os.path.join(version_dir, '%s.json' % version)
            mc_info['server_file'] = os.path.join(work_dir, 'minecraft_server.%s.jar' % version)
            mc_info['asset_dir']   = os.path.join(work_dir, 'assets')
    else:
        mc_info['new_launcher'] = False
        mc_info['json_url'] = None
        if not work_dir is None:
            mc_info['natives_dir'] = os.path.join(work_dir, 'bin', 'natives')
            mc_info['library_dir'] = os.path.join(work_dir, 'bin')
            mc_info['client_file'] = os.path.join(work_dir, 'bin', 'minecraft.jar')
            mc_info['json_file']   = None
            mc_info['server_file'] = os.path.join(work_dir, 'minecraft_server.jar')
            mc_info['asset_dir']   = os.path.join(work_dir, 'resources')
            for lib in default['libraries'].split(' '):
                mc_info['libraries'].append({
                    'url'     : default['base_url'] + lib,
                    'file'    : os.path.join(work_dir, 'bin', lib),
                    'extract' : None,
                    'md5'     : None
                    })
            for native in default['natives'].split(' '):
                mc_info['libraries'].append({
                    'url_ex'  : default['base_url'] + lib,
                    'file'    : os.path.join(work_dir, 'bin', lib),
                    'extract' : { 'exclude' : ['META-INF/'] },
                    'md5'     : None
                    })
    
    if not mc_info['mcp_url'].startswith('http'):
        mc_info['mcp_url'] = None
    mc_info['mcp_file'] = os.path.join(fml_dir, 'mcp%s.zip' % mc_info['mcp_ver'])
            
    if not work_dir is None:
        for x in ['natives_dir', 'library_dir', 'asset_dir']:
            if not os.path.isdir(mc_info[x]):
                os.makedirs(mc_info[x])
        for x in ['client_file', 'server_file', 'json_file', 'mcp_file']:
            if mc_info[x] is None:
                continue
            dir = os.path.dirname(mc_info[x])
            if not os.path.isdir(dir):
                os.makedirs(dir)
        
    return mc_info

def download_file(url, target, md5=None, root=None, prefix=''):
    name = os.path.basename(target)
    
    if not root is None:
        name = os.path.abspath(target)
        name = name[len(os.path.abspath(root)) + 1:]
    
    dir = os.path.dirname(target)
    if not os.path.isdir(dir):
        os.makedirs(dir)
    
    if os.path.isfile(target) and not md5 == None:
        if not get_md5(target) == md5:
            print '%s%s Modified, removing' % (prefix, name)
            os.remove(target)
    
    if not os.path.isfile(target):
        try:
            urllib.urlretrieve(url, target)
            if not md5 == None:
                if not get_md5(target) == md5:
                    print '%sDownload of %s failed md5 check, deleting' % (prefix, name)
                    os.remove(target)
                    return False
            if prefix == '':
                print 'Downloaded %s' % name
            else:
                print '%s%s Done' % (prefix, name)
        except Exception as e:
            print e
            print '%sDownload of %s failed, download it manually from \'%s\' to \'%s\'' % (prefix, target, url, target)
            return False
    return True
    
def get_headers(url):
    #Connects to the given URL and requests just the headers, No data
    #Used when talking to Minecraft's asset/library server to gather server side MD5's
    #Returns a dictionary of all headers
    class HeadRequest(urllib2.Request):
        def get_method(self):
            return 'HEAD'
    response = urllib2.urlopen(HeadRequest(url))
    array = [line.rstrip('\r\n') for line in response.info().headers]
    dict = {}
    for line in array:
        pts = line.split(':', 1)
        pts[1] = pts[1].strip()
        #Strip the first and last "s if the stirng is surrounded with them
        if pts[1][0] == '"' and pts[1][-1] == '"':
            pts[1] = pts[1][1:-1]
        dict[pts[0]] = pts[1]
    
    return dict    
    
def get_md5(file):
    #Returns the MD5 digest of the specified file, or None if the file doesnt exist
    if not os.path.isfile(file):
        return None
    with closing(open(file, 'rb')) as fh:
        return md5(fh.read()).hexdigest()   

def fix_patch(in_file, out_file, find=None, rep=None):
    #Fixes the following issues in the patch file if they exist:
    #  Normalizes the path seperators for the current OS
    #  Normalizes the line endings
    # Returns the path that the file wants to apply to
    
    in_file = os.path.normpath(in_file)
    if out_file is None:
        tmp_file = in_file + '.tmp'
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
        
def apply_patch(patch, target, mcp_dir):
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

def file_backup(file, md5=None):
    #Takes a backup of the passed in file
    #Verifying the md5 sum if it's specified
    #At the end of this things should be in one of two states:
    # 1) file and file.backup exist, both are valid and match the md5 provided
    # 2) neither file or file.backup exist, as they both failed the md5 check
    
    base = os.path.dirname(file)
    name = os.path.basename(file)
    bck = os.path.join(base, name + '.backup')
    src = os.path.join(base, name)
    
    if not os.path.isfile(src) and not os.path.isfile(bck):
        return
    
    if os.path.isfile(bck):
        if get_md5(bck) == md5 or md5 is None:
            if os.path.isfile(src):
                os.remove(src)
            shutil.move(bck, src)
        else:
            os.remove(bck)
    
    if os.path.isfile(src):
        if not get_md5(src) == md5 and not md5 is None:
            print 'Modified %s detected, removing' % os.path.basename(src)
            os.remove(src)
        else:
            shutil.copy(src, bck) 

def normaliselines(file):
    #Normalises the lines of the specified file to linux \n line endings
    file = os.path.normpath(file)
    tmp = file + '.tmp'

    with open(file, 'rb') as in_file:
        with open(tmp, 'wb') as out_file:
            out_file.write(in_file.read().replace('\r\n', '\n'))

    shutil.move(tmp, file)

def load_srg(srg_file, reverse=False):
    #Loads a Retroguard .srg file into a dictonary
    #If reverse if true, the mappings are reversed
    with open(srg_file, 'r') as fh:
        lines = fh.readlines()
    srg = {'CL:': {}, 'MD:': {}, 'FD:': {}, 'PK:': {}}
    
    for line in lines:
        line = line.strip()
        if len(line) == 0: continue
        if line[0] == '#': continue
        args = line.split(' ')
        type = args[0]
        
        if type == 'PK:' or type == 'CL:' or type == 'FD:':
            srg[type][args[1]] = args[2]
        elif type == 'MD:':
            srg[type][args[1] + ' ' + args[2]] = args[3] + ' ' + args[4]
        else:
            assert 'Unknown type %s' % line

    if reverse:
        for type,map in srg.items():
            srg[type] = dict([[v,k] for k,v in map.items()])
    return srg

def extract_zip(src, dst, prefix=None, filter=[]):
    # Extract a zip rchive to the specified folder,
    # Filtering out anything that matches the supplied filters
    
    def is_filtered(name, excludes):
        for ex in excludes:
            if name.startswith(ex):
                return True
        return name.endswith('/')
                    
    zip = ZipFile(src)
    for name in zip.namelist():
        if is_filtered(name, filter):
            continue
        out_file = os.path.join(dst, os.sep.join(name.split('/')))
        
        if not os.path.isfile(out_file):                    
            dir = os.path.dirname(out_file)
            if not os.path.isdir(dir):
                os.makedirs(dir)
        
            if not prefix is None:
                print '%sExtracting %s' % (prefix, name)
            out = open(out_file, 'wb')
            out.write(zip.read(name))
            out.flush()
            out.close()
    zip.close()

def merge_tree(root_src_dir, root_dst_dir, prefix=None):
    #Merges the source directory into the dest directory, 
    #will overwrite anything the currently exists
    for src_dir, dirs, files in os.walk(root_src_dir):
        dst_dir = src_dir.replace(root_src_dir, root_dst_dir)
        clean_dir = src_dir.replace(root_src_dir, '')[1:]
        if not os.path.exists(dst_dir):
            os.mkdir(dst_dir)
        for file_ in files:
            src_file = os.path.join(src_dir, file_)
            dst_file = os.path.join(dst_dir, file_)
            if os.path.exists(dst_file):
                os.remove(dst_file)
            shutil.copy(src_file, dst_dir)
            if not prefix is None:
                print('%s%s%s'% (prefix, clean_dir, file_))

def read_file(file):
    if not os.path.exists(file):
        return None
    buf = None
    with closing(open(file, 'r')) as fh:
        buf = fh.read()
    return buf

def runcmd(cmd, echo=True, commands=None):
    forklist = cmdsplit(cmd)
    process = subprocess.Popen(forklist, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
    output, _ = process.communicate()    
    
    if echo and not commands is None:
        for line in output.splitlines():
            commands.logger.info(line)

    if process.returncode:
        if not echo and not commands is None:
            for line in output.splitlines():
                commands.logger.info(line)
        return False
    return True

def cmdsplit(args):
    if os.sep == '\\':
        args = args.replace('\\', '\\\\')
    return shlex.split(args)    

def kill_signatures(jar_file):
    # Removes everything in a jar file's META-INF folder, typically used to remove signature data
    dir = os.path.dirname(jar_file)
    name = os.path.basename(jar_file)
    tmp_jar = os.path.join(dir, '%s.temp' % name)
    
    if not os.path.isfile(jar_file):
        return
    
    if os.path.isfile(tmp_jar):
        os.remove(tmp_jar)
    
    shutil.move(jar_file, tmp_jar)
    
    print('Stripping META-INF from %s' % jar_file)
    
    with closing(zipfile.ZipFile(tmp_jar, mode='a')) as zip_in:
        with closing(zipfile.ZipFile(jar_file, 'w', zipfile.ZIP_DEFLATED)) as zip_out:
            for i in zip_in.filelist:
                if not i.filename.startswith('META-INF'):
                    c = zip_in.read(i.filename)
                    zip_out.writestr(i.filename, c)
                else:
                    print('    Skipping: %s' % i.filename)
    os.remove(tmp_jar)

def apply_patches(mcp_dir, patch_dir, target_dir, find=None, rep=None):
    # Attempts to apply a directory full of patch files onto a target directory.
    sys.path.append(mcp_dir)
    
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
        
#==========================================================================
#                      MCP Related Functions
#==========================================================================
def download_mcp(fml_dir, mcp_dir, version=None):
    if os.path.isfile(os.path.join(mcp_dir, 'runtime', 'commands.py')):
        print 'MCP Detected already, not downloading'
        return True
        
    if os.path.isdir(mcp_dir):
        print 'Old MCP Directory exists, but MCP was not detected, please delete MCP directory at \'%s\'' % mcp_dir
        sys.exit(1)

    mc_info = read_mc_versions(fml_dir, version=version)
    
    print('Checking MCP zip (may take time to download)')
    if not download_file(mc_info['mcp_url'], mc_info['mcp_file'], mc_info['mcp_md5']):
        sys.exit(1)
        
    if not os.path.isdir(mcp_dir):
        _mkdir(mcp_dir)
        
    print 'Extracting MCP to \'%s\'' % mcp_dir
    extract_zip(mc_info['mcp_file'], mcp_dir, filter=['eclipse']) #, prefix='    ')
    
    #If we're not on windows, lets set the executable flag on all shell scripts and astyle-osx
    if os.name != 'nt':
        for path, _, filelist in os.walk(mcp_dir):
            for cur_file in fnmatch.filter(filelist, '*.sh'):
              file_name = os.path.join(path, cur_file)
              process = subprocess.Popen(cmdsplit('chmod +x "%s"' % file_name), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
              output, _ = process.communicate()
             
        process = subprocess.Popen(cmdsplit('chmod +x "%s/runtime/bin/astyle-osx"' % mcp_dir), stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
        output, _ = process.communicate()

    #As a helper to build servers, or people who don't wish to download all libraris and assets every time.
    #Copy all data from 'mcp_data' to the mcp directory
    data_dir = os.path.join(fml_dir, 'mcp_data')
    if os.path.isdir(data_dir):
        print 'Moving mcp_data to MCP'
        merge_tree(data_dir, mcp_dir)##, prefix='    ')
    
    return True
    
def setup_mcp(fml_dir, mcp_dir, gen_conf=True):
    #Modifies MCP to the state FML needs it in for recompile/reobf/etc to work as we intend it.
    #Does not decompile minecraft in this stage!
    
    print('Setting up MCP')
    runtime = os.path.join(mcp_dir, 'runtime', 'commands.py')
    file_backup(runtime)
    
    patch = os.path.join(fml_dir, 'commands.patch')
    
    if not os.path.isfile(patch):
        raise Exception('Commands.py patch not found %s' % patch)
        return
        
    print('Patching commands.py')
    apply_patch(patch, runtime, mcp_dir=mcp_dir)
    
    try:
        sys.path.append(mcp_dir)
        from runtime.commands import commands_sanity_check
        commands_sanity_check()
    except ImportError as ex:
        print('Could not verify commands.py patch integrity, this typically means that you are not in a clean MCP environment.')
        print('Download a clean version of MCP %s and try again' % mcp_version)
        print(ex)
        sys.exit(1)
    
    mcp_conf = os.path.join(mcp_dir, 'conf')
    mcp_conf_bak = os.path.join(mcp_dir, 'conf.bak')
    fml_conf = os.path.join(fml_dir, 'conf')
    
    if gen_conf:
        if os.path.isdir(mcp_conf_bak):
            print 'Reverting old conf backup folder'
            shutil.rmtree(mcp_conf)
            os.rename(mcp_conf_bak, mcp_conf)

        create_merged_conf(mcp_dir, fml_dir)

        print 'Backing up MCP Conf'
        os.rename(mcp_conf, mcp_conf_bak)
    else:
        shutil.rmtree(mcp_conf)
    
    print 'Copying FML conf'
    shutil.copytree(fml_conf, mcp_conf)
    
    create_renamed_conf(mcp_dir, fml_dir)
    
    #update workspace
    if not os.path.isfile(os.path.join(fml_dir, 'fmlbuild.properties-sample')):
        mcp_eclipse = os.path.join(mcp_dir, 'eclipse')
        
        if os.path.isdir(os.path.join(mcp_eclipse, 'Client')) and os.path.isdir(os.path.join(mcp_eclipse, 'Server')):
            shutil.rmtree(mcp_eclipse)
            
        if not os.path.isdir(mcp_eclipse) and os.path.isdir(os.path.join(fml_dir, 'eclipse')):
            print 'Fixing MCP Workspace'
            copytree(os.path.join(fml_dir, 'eclipse'), mcp_eclipse)        

def whereis(filename, rootdir):
    # Snagged from MCP
    if not os.path.exists(rootdir):
        return []
    #logging.info('> Searching for %s in %s', filename, rootdir)
    results = []
    for path, _, filelist in os.walk(rootdir):
        if filename in filelist:
            results.append(path)
    return results
        
def find_java():
    # Snagged from MCP so we can gather this info without setting up it's Command object
    results = []
    if os.name == 'nt':
        if not results:
            import _winreg
            for flag in [_winreg.KEY_WOW64_64KEY, _winreg.KEY_WOW64_32KEY]:
                try:
                    k = _winreg.OpenKey(_winreg.HKEY_LOCAL_MACHINE, r'Software\JavaSoft\Java Development Kit', 0, _winreg.KEY_READ | flag)
                    version, _ = _winreg.QueryValueEx(k, 'CurrentVersion')
                    k.Close()
                    k = _winreg.OpenKey(_winreg.HKEY_LOCAL_MACHINE, r'Software\JavaSoft\Java Development Kit\%s' % version, 0, _winreg.KEY_READ | flag)
                    path, _ = _winreg.QueryValueEx(k, 'JavaHome')
                    k.Close()
                    path = os.path.join(str(path), 'bin')
                    if (runcmd('"%s" -version' % os.path.join(path, 'javac'))):
                        results.append(path)
                except (OSError):
                    pass
        if not results:
            if (runcmd('javac -version')):
                results.append('')
        if not results and 'ProgramW6432' in os.environ:
            results.extend(whereis('javac.exe', os.environ['ProgramW6432']))
        if not results and 'ProgramFiles' in os.environ:
            results.extend(whereis('javac.exe', os.environ['ProgramFiles']))
        if not results and 'ProgramFiles(x86)' in os.environ:
            results.extend(whereis('javac.exe', os.environ['ProgramFiles(x86)']))
    else:
        if not results:
            if (runcmd('javac -version')):
                results.append('')
        if not results:
            results.extend(whereis('javac', '/usr/bin'))
        if not results:
            results.extend(whereis('javac', '/usr/local/bin'))
        if not results:
            results.extend(whereis('javac', '/opt'))
    if not results:
        print('Java JDK is not installed ! Please install java JDK from http://java.oracle.com')
        sys.exit(1)
        
    return { 
        'javac' : '"%s"' % os.path.join(results[0], 'javac'),
        'java'  : '"%s"' % os.path.join(results[0], 'java')
    }

#==================================================================================
#                     MCP Conf Merger Code
#==================================================================================
def create_merged_conf(mcp_dir, fml_dir):
    print('Creating merged conf')
    #Creates the merged conf folder from MCP's conf folder to fml_dir/conf
    
    #Lets grab the files we dont work on
    for file in ['version.cfg', 'joined.exc']:
        dst_file = os.path.join(fml_dir, 'conf', file)
        src_file = os.path.join(mcp_dir, 'conf', file)
        if not os.path.isdir(os.path.dirname(dst_file)):
            os.makedirs(os.path.dirname(dst_file))
        if os.path.exists(dst_file):
            os.remove(dst_file)
        shutil.copy(src_file, dst_file)
        normaliselines(dst_file)
        print('    Copying %s' % os.path.normpath(src_file))

    print('    Generating merged Retroguard data')
    common_srg = create_merged_srg(mcp_dir, fml_dir)
    print('    Reading merged MCInjector config')
    common_exc = load_merged_exc(mcp_dir, fml_dir)
    print('    Gathering list of common searge names')
    common_map = create_shared_searge_names(common_srg, common_exc)
    
    for x in [['fields.csv', 'searge'], ['methods.csv', 'searge'], ['params.csv', 'param']]:
        print('    Generating merged csv for %s' % x[0])
        create_merged_csv(common_map, os.path.join(mcp_dir, 'conf', x[0]),  os.path.join(fml_dir, 'conf', x[0]), main_key=x[1])
    
def create_merged_srg(mcp_dir, fml_dir):
    #Merges two .srg files together to create one master mapping.
    #  When issues are encountered, they are reported but the client srg is trusted over the server
    client_file = os.path.join(mcp_dir, 'conf', 'client.srg')
    server_file = os.path.join(mcp_dir, 'conf', 'server.srg')
    
    if not os.path.isfile(client_file) or not os.path.isfile(server_file):
        print('    Could not find client and server srg files in "%s"' % mcp_dir)
        return False
        
    client = load_srg(client_file)
    server = load_srg(server_file)    
    
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
            common[type][key] = value + ' #C' # Tag the client only entries
            
    for type in common:
        for key, value in server[type].items():
            common[type][key] = value + ' #S' # Tag the server only entries
            
    if not fml_dir is None:
        #Print joined retroguard files
        with open(os.path.join(fml_dir, 'conf', 'joined.srg'), 'wb') as f:
            for type in ['PK:', 'CL:', 'FD:', 'MD:']:
                for key in sorted(common[type]):
                    f.write('%s %s %s\n' % (type, key, common[type][key]))
    
    return common
    
def load_merged_exc(mcp_dir, fml_dir):
    #Reads the exc file into a dictionary
    joined = {}
    with open(os.path.join(mcp_dir, 'conf', 'joined.exc'), 'r') as fh:
        for line in fh:
            if not line.startswith('#'):
                pts = line.rstrip('\r\n').split('=')
                joined[pts[0]] = pts[1]
    
    return joined    
    
def create_shared_searge_names(common_srg, common_exc):
    #Creates an array of all srg names that are common on both the client and server
    field = re.compile(r'field_[0-9]+_[a-zA-Z_]+$')
    method = re.compile(r'func_[0-9]+_[a-zA-Z_]+')
    param = re.compile(r'p_[\w]+_\d+_')
    
    searge = []
    
    for key, value in common_srg['FD:'].items():
        m = field.search(value)
        if not m is None and not '#' in value:
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
    
def create_merged_csv(common_map, in_file, out_file, main_key='searge'):
    #Filter throw the csv and condense 'shared' mappings into one entry of side 2
    fields = []
    data = []
    with closing(open(in_file, 'r')) as fh:
        reader = csv.DictReader(fh)
        fields = reader.fieldnames
        data = [r for r in reader]
        
    side = [
        [r for r in data if r['side'] == '0' and not r[main_key] in common_map],
        [r for r in data if r['side'] == '1' and not r[main_key] in common_map],
        sorted([r for r in data if r[main_key] in common_map], key=lambda row: row['side'])
    ]
    
    added = []
    common = []
    for row in side[2]:
        if not row[main_key] in added:
            row['side'] = '2'
            added.append(row[main_key])
            common.append(row)
            
    with closing(open(out_file, 'wb')) as fh:
        writer = csv.DictWriter(fh, fieldnames=fields, lineterminator='\n')
        writer.writeheader()
        for row in sorted(side[0] + side[1] + common, key=lambda row: row[main_key]):
            writer.writerow(row)    

def create_renamed_conf(mcp_dir, fml_dir):
    # Creates copies of the joined srg and exec files with the new Packaged names
    # Also updates the patches in the conf folder for the new packaged names
    print('Creating Repackaged data')
    pkg_file = os.path.join(fml_dir, 'conf', 'packages.csv')
    
    pkgs = {}
    if os.path.isfile(pkg_file):
        with closing(open(pkg_file)) as fh:
            reader = csv.DictReader(fh)
            for line in reader:
                pkgs[line['class']] = line['package']
                
    def repackage_class(pkgs, cls):
        if cls.startswith('net/minecraft/src/'):
            tmp = cls[18:]
            if tmp in pkgs.keys():
               return '%s/%s' % (pkgs[tmp], tmp)
        return cls
        
    for ext in ['srg', 'exc']:
        regnms = re.compile(r'net/minecraft/src/(\w+)')
        print('    Creating re-packaged %s' % ext)
        
        buf = read_file(os.path.join(mcp_dir, 'conf', 'joined.%s' % ext))
        def mapname(match):
            return repackage_class(pkgs, match.group(0))
        buf = regnms.sub(mapname, buf)
        
        with closing(open(os.path.join(mcp_dir, 'conf', 'packaged.%s' % ext), 'wb')) as outf:
            outf.write(buf)
    
    print('    Creating re-packaged MCP patches')
    
    def fix_patches(patch_in, patch_tmp):        
        regnms = re.compile(r'net\\minecraft\\src\\(\w+)')
        with closing(open(patch_in, 'r')) as fh:
            buf = fh.read()
            def mapname(match):
                return repackage_class(pkgs, match.group(0).replace('\\', '/')).replace('/', '\\')
            buf = regnms.sub(mapname, buf)
            
        with closing(open(patch_tmp, 'w')) as fh:
            fh.write(buf)
                        
        shutil.move(patch_tmp, patch_in)

    patch_dir = os.path.join(mcp_dir, 'conf', 'patches')
    fix_patches(os.path.join(patch_dir, 'minecraft_ff.patch'       ), os.path.join(patch_dir, 'tmp.patch'))
    fix_patches(os.path.join(patch_dir, 'minecraft_server_ff.patch'), os.path.join(patch_dir, 'tmp.patch'))

#==========================================================================
#                      MCP Decompile Process
#==========================================================================
def reset_logger():
    # Resets the logging handlers, if we don't do this, we get multi-prints from MCP
    log = logging.getLogger()
    while len(log.handlers) > 0:
        log.removeHandler(log.handlers[0])

count = 0
def cleanup_source(path):
    # We cleanup various things in MCP such as:
    # astyle differences:
    #  newline after case before case body
    #  newline after case body before new case
    # We also assign jad-style names to local variables in decompiled code.
    
    from rename_vars import rename_class
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
        old = buf.replace('\r', '')
        buf = rename_class(old, MCP=True)

        if count > 0 or buf != old:
            with open(tmp_file, 'w') as fh:
                fh.write(buf)
            shutil.move(tmp_file, src_file)
            
    for path, _, filelist in os.walk(path, followlinks=True):
        sub_dir = os.path.relpath(path, path)
        for cur_file in fnmatch.filter(filelist, '*.java'):
            src_file = os.path.normpath(os.path.join(path, cur_file))
            updatefile(src_file)

compile_tools = True
client_jar = None
def decompile_minecraft(fml_dir, mcp_dir, disable_at=False, disable_merge=False, enable_server=False, disable_client=False, disable_assets=False):
    # This is where the bulk of the decompile actually happens
    # Its a bit of a pain as we need to hook into MCP's Commands class to inject our transformers at certian times.
    global compile_tools
    global client_jar
    
    sys.path.append(mcp_dir)
    from runtime.decompile import decompile
    from runtime.cleanup import cleanup
    from runtime.commands import Commands, CLIENT, SERVER
    
    fml_dir = os.path.abspath(fml_dir)
    
    # Make sure the src directroy is dead
    src_dir = os.path.join(mcp_dir, 'src')        
    if os.path.isdir(src_dir):
        os.chdir(mcp_dir)
        cleanup(None, False, False)
        reset_logger()
        os.chdir(fml_dir)
        
    if os.path.isdir(src_dir):
        print 'Please make sure to backup your modified files, and say yes when it asks you to do cleanup.'
        sys.exit(1)
    
    compile_tools = True
    
    # Inject ourselves before RetroGuard if it's used
    def applyrg_shunt(self, side, reobf=False, applyrg_real=Commands.applyrg):
        transformers_hook(self, side)
        self.logger.info('> Really Applying Retroguard')
        applyrg_real(self, side, reobf)
    # Inject ourselves before SpecialSource if it's used
    def applyss_shunt(self, side, reobf=False, srg_names=False, in_jar=None, out_jar=None, keep_lvt=False, keep_generics=False, applyss_real=Commands.applyss):
        transformers_hook(self, side)
        self.logger.info('> Really Applying SpecialSource')
        applyss_real(self, side, reobf=reobf, srg_names=srg_names, in_jar=in_jar, out_jar=out_jar, keep_lvt=keep_lvt, keep_generics=keep_generics)
        
    # This is our pre-everything hook.
    # We do the following:
    # Verify if astyle is installed/accessible, if not then exit
    # We also compile and run our transformers:
    #   MCPMerge, This merges the runnable code from the server and client creating a single codebase that is complete.
    #   AccessTransformer: 
    #       This changes access levels of classes fields and methods based on a configuration map. 
    #       Allows up to keep out base edits down because we don't have to patch the access levels of everything.
    def transformers_hook(self, side):
        global compile_tools
        if not self.has_wine and not self.has_astyle:
            self.logger.error('!! Please install either wine or astyle for source cleanup !!')
            self.logger.error('!! This is REQUIRED by FML/Forge Cannot proceed !!')
            sys.exit(1)
        jars = {CLIENT: self.jarclient, SERVER: self.jarserver}
        kill_signatures(jars[side])
        
        dir_bin = os.path.join(fml_dir, 'bin')
        if not os.path.isdir(dir_bin):
            os.makedirs(dir_bin)
        
        class_path = os.pathsep.join([f for f in self.cpathclient + [dir_bin] if not f in jars.values()])
        dir_common  = os.path.join(fml_dir, 'common')
        dir_trans   = os.path.join(dir_common, 'cpw/mods/fml/common/asm/transformers'.replace('/', os.sep))
        java        = self.cmdjava.translate(None, '"')
        javac       = self.cmdjavac.translate(None, '"')
        cmd_compile = '"%s" -Xlint:-options -deprecation -g -source 1.6 -target 1.6 -classpath "{classpath}" -sourcepath "{sourcepath}" -d "{outpath}" "{target}"' % javac
        cmd_compile = cmd_compile.format(classpath=class_path, sourcepath=dir_common, outpath=dir_bin, target="{target}")
        
        #Compile AccessTransformer and MCPMerger if we havent already
        # Only needs to happen once, but we don't know if were gunna decompile both client and server so cant do it based off side
        if compile_tools:
            self.logger.info('> Compiling AccessTransformer')
            if not runcmd(cmd_compile.format(target=os.path.join(dir_trans, 'AccessTransformer.java')), commands=self, echo=False):
                sys.exit(1)
                
            self.logger.info('> Compiling MCPMerger')
            if not runcmd(cmd_compile.format(target=os.path.join(dir_trans, 'MCPMerger.java')), commands=self, echo=False):
                sys.exit(1)
                
            compile_tools = False
        
        # Merge the client and server jar, only needs to be run once so only do it on the client
        if side == CLIENT:
            if not disable_merge:
                self.logger.info('> Running MCPMerger')
                forkcmd = ('"%s" -classpath "{classpath}" cpw.mods.fml.common.asm.transformers.MCPMerger "{mergecfg}" "{client}" "{server}"' % java).format(
                    classpath=class_path, mergecfg=os.path.join(fml_dir, 'mcp_merge.cfg'), client=jars[CLIENT], server=jars[SERVER])
                    
                if not runcmd(forkcmd, echo=False, commands=self):
                    sys.exit(1)
            else:
                self.logger.info('> MCPMerge disabled')
        
        apply_ats(fml_dir, mcp_dir, class_path, jars[side], disable_at=disable_at, commands=self)
    
    #Check the original jars not the transformed jars
    def checkjars_shunt(self, side, checkjars_real = Commands.checkjars):
        self.jarclient = self.jarclient + '.backup'
        self.jarserver = self.jarserver + '.backup'
        ret = checkjars_real(self, side)
        self.jarclient = self.jarclient[:-7]
        self.jarserver = self.jarserver[:-7]
        return ret
    
    try:
        pre_decompile(mcp_dir, fml_dir, disable_assets=disable_assets)
        
        os.chdir(mcp_dir)
        Commands.applyrg = applyrg_shunt
        Commands.applyss = applyss_shunt
        Commands.checkjars = checkjars_shunt
        #decompile -d -n -r
        #         Conf  JAD    CSV    -r    -d    -a     -n    -p     -o     -l     -g     -c                 -s              --rg  --workDir --json(None=compute) --nocopy
        decompile(None, False, False, True, True, False, True, False, False, False, False, not disable_client, enable_server, False, os.path.join(mcp_dir,'jars'), None, True)
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
    commands = Commands(verify=True,workdir=os.path.join(mcp_dir,'jars'))
    
    if not disable_client:
        updatemd5_side(mcp_dir, commands, CLIENT)
        reset_logger()
    
    if enable_server:
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

def pre_decompile(mcp_dir, fml_dir, disable_assets=False):
    download_minecraft(mcp_dir, fml_dir)
    
    if not disable_assets:
        download_assets(mcp_dir)

def post_decompile(mcp_dir, fml_dir):
    if False:
        print('hi')

def apply_fml_patches(fml_dir, mcp_dir, src_dir, copy_files=True):
    #Delete /minecraft/cpw to get rid of the Side/SideOnly classes used in decompilation
    cpw_mc_dir = os.path.join(src_dir, 'minecraft', 'cpw')
    if os.path.isdir(cpw_mc_dir):
        shutil.rmtree(cpw_mc_dir)
        
    #patch files
    print('Applying Forge ModLoader patches')
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

def finish_setup_fml(fml_dir, mcp_dir, enable_server=False, disable_client=False, disable_rename=False):
    sys.path.append(mcp_dir)
    from runtime.updatenames import updatenames
    from runtime.updatemd5 import updatemd5
    from runtime.updatemcp import updatemcp
    
    os.chdir(mcp_dir)
    if not disable_rename:
        updatenames(None, True, not disable_client, enable_server)
        reset_logger()
    updatemd5(None, True, not disable_client, enable_server)
    reset_logger()
    os.chdir(fml_dir)
#==========================================================================
#                      Download Functions!
#==========================================================================    
def download_minecraft(mcp_dir, fml_dir, version=None):
    mc_info = read_mc_versions(fml_dir, version=version, work_dir=os.path.join(mcp_dir, 'jars'))

    failed = False
    
    if mc_info['new_launcher']:
        if os.path.isdir(os.path.join(fml_dir, 'jsons')):
            json_file = os.path.join(fml_dir, 'jsons', '%s-dev.json' % mc_info['version'])
        else:
            json_file = os.path.join(fml_dir, 'fml.json')
        
        version_json = None
        try:
            version_json = json.load(open(json_file))
        except Exception as e:
            print 'Failed to load version json: %s' % json_file
            sys.exit(1)
        
        failed = download_libraries(mcp_dir, version_json['libraries'], mc_info['natives_dir']) or failed
        if os.path.isfile(mc_info['json_file']):
            os.remove(mc_info['json_file'])
        shutil.copy(json_file, mc_info['json_file'])
    else:
        failed = not download_list(mc_info['downloads']) or failed
    
    # Remove any invalid files
    for type in ['client', 'server']:
        print("Backing up %s"%type)
        file_backup(mc_info['%s_file' % type], mc_info['%s_md5' % type])
        failed = not download_file(mc_info['%s_url' % type], mc_info['%s_file' % type], mc_info['%s_md5' % type]) or failed
        file_backup(mc_info['%s_file' % type], mc_info['%s_md5' % type])
    
    if failed:
        print 'Something failed verifying minecraft files, see log for details.'
        sys.exit(1)

def download_libraries(mcp_dir, libraries, natives_dir):
    # Will attempt to download a list of maven style libraries from the default Minecraft website
    # or a custom website if the library specifies it
    # This list should be in the format of the new launcher's version.json file
    # Under the entry 'libraries'
    lib_dir = os.path.join(mcp_dir, 'jars', 'libraries')
    default_url = 'http://s3.amazonaws.com/Minecraft.Download/libraries'
    
    downloads = []
    failed = False
    
    for lib in libraries:
        name = lib['name'].split(':')
        domain  = name[0].split('.')
        root    = name[1]
        version = name[2]
        path = domain + [root, version]
        extract = None
        root_url = default_url
        if 'extract' in lib.keys():
            extract = lib['extract']
        if 'url' in lib.keys():
            root_url = lib['url']
        
        file_names = ['%s-%s.jar' % (root, version)]
        
        if 'natives' in lib.keys():
            file_names = []
            for k,v in lib['natives'].items():
                file_names.append('%s-%s-%s.jar' % (root, version, v))
                
        if 'children' in lib.keys():
            for child in lib['children']:
                file_names.append('%s-%s-%s.jar' % (root, version, child))
                
        for file_name in file_names:
            url = '%s/%s/%s' % (root_url, '/'.join(path), file_name)
            file_path = os.path.join(lib_dir, os.sep.join(path), file_name)
            headers = get_headers(url)
            if headers is None:
                print('Could not retreive headers for library: %s ( %s )' % (lib['name'], url))
                failed = True
            else:
                md5 = None
                if 'ETag' in headers.keys(): # Amazon headers, Mojang's server
                    md5 = headers['ETag']
                else: # Could be a normal maven repo, check for .md5 file
                    try:
                        md5 = urllib2.urlopen(url + '.md5').read().split(' ')[0].replace('\r', '').replace('\n', '')
                        if not len(md5) == 32:
                            md5 = None
                            print('Could not retrieve md5 for library %s ( %s.md5 )' % (file_name, url))
                            failed = True
                    except (HTTPError):
                        failed = True
                        pass
                downloads.append({
                    'url' : url,
                    'file' : file_path,
                    'md5'  : md5,
                    'size' : headers['Content-Length'],
                    'extract' : extract
                })
    
    return download_list(downloads, natives_dir) or failed
    
def download_list(list, natives_dir):
    #Downloads a list of files and urls. Verifying md5s if avalible. 
    #Skipping already existing and valid files.
    #Also extracts files that are specified to be extracted to the natives folder
    missing = []
    for dl in list:
        if os.path.isfile(dl['file']):
            if dl['md5'] is None or not get_md5(dl['file']) == dl['md5']:
                missing.append(dl)
        else:
            missing.append(dl)
            
    if len(missing) == 0:
        return False
        
    print 'Downloading %s libraries' % len(missing)
    failed = False
    for dl in missing:
        if download_file(dl['url'], dl['file'], dl['md5'], prefix='    '):
            if not dl['extract'] is None:
                excludes = []
                if 'exclude' in dl['extract'].keys():
                    excludes = dl['extract']['exclude']
                extract_zip(dl['file'], natives_dir, prefix='        ', filter=excludes)
        else:
            print('    Failed to download %s from %s' % (os.path.basename(dl['file']), dl['url']))
            failed = True
        
    return failed

def download_assets(mcp_dir):
    from xml.dom.minidom import parse
    asset_dir = os.path.join(mcp_dir, 'jars', 'assets')
    base_url = 'http://s3.amazonaws.com/Minecraft.Resources'
    
    print('Gathering assets list from %s' % base_url)
    
    files = []
    failed = False
    
    try:
        url = urllib.urlopen(base_url)
        xml = parse(url)
        
        def get(xml, key):
            return xml.getElementsByTagName(key)[0].firstChild.nodeValue
        
        for asset in xml.getElementsByTagName('Contents'):
            path = get(asset, 'Key')
            if path.endswith('/'):
                continue
                
            file = os.path.join(asset_dir, os.sep.join(path.split('/')))
            md5 = get(asset, 'ETag').replace('"', '')
            
            if os.path.isfile(file):
                if get_md5(file) == md5:
                    continue
                
            files.append({
                'file' : file,
                'url'  : '%s/%s' % (base_url, path),
                'size' : get(asset, 'Size'),
                'md5'  : md5
            })
    except Exception as e:
        print 'Error gathering asset list:'
        pprint(e)
        sys.exit(1)
    
    if len(files) == 0:
        print('    No new assets need to download')
        return
        
    print('    Downloading %s assets' % len(files))
    for file in files:
        failed = not download_file(file['url'], file['file'], file['md5'], root=asset_dir, prefix='        ') or failed
    
    if failed:
        print('    Downloading assets failed, please review log for more details')
    
#==========================================================================
#                            Transformers
#==========================================================================
def apply_ats(fml_dir, mcp_dir, class_path, target, disable_at=False, commands=None):
    def log(msg):
        if commands is None:
            print(msg)
        else:
            commands.logger.info(msg)
            
    cmds = find_java()
    if cmds is None:
        log('>Could not run Access Transformer, Java not found!')
        sys.exit(1)
        
    if not disable_at:
        log('> Running AccessTransformer')
        forkcmd = ('"%s" -classpath "{classpath}" cpw.mods.fml.common.asm.transformers.AccessTransformer "{jar}" "{fmlconfig}"' % cmds['java']).format(
            classpath=class_path, jar=target, fmlconfig=os.path.join(fml_dir, 'common', 'fml_at.cfg'))
            
        forge_cfg = os.path.join(fml_dir, '..', 'common', 'forge_at.cfg')
        if os.path.isfile(forge_cfg):
            log('    Forge config detected')
            forkcmd += ' "%s"' % forge_cfg

        for dirname, dirnames, filenames in os.walk(os.path.join(fml_dir, '..', 'accesstransformers')):
            for filename in filenames:
                accesstransformer = os.path.join(dirname, filename)
                if os.path.isfile(accesstransformer):              
                    log('    Access Transformer "%s" detected' % filename)
                    forkcmd += ' "%s"' % accesstransformer
        
        if not runcmd(forkcmd, echo=False, commands=commands):
            sys.exit(1)
    else:
        log('> Access Transformer disabled')
