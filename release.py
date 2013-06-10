import os, os.path, sys, glob
import shutil, fnmatch
import logging, zipfile, re
from optparse import OptionParser
from urllib2 import HTTPError

forge_dir = os.path.dirname(os.path.abspath(__file__))
from forge import reset_logger, load_version, zip_folder, zip_create, inject_version, build_forge_dev
from changelog import make_changelog

zip = None
zip_name = None
zip_base = None
version_str = None
version_mc = None

def main():
    global version_str
    global version_mc
    
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='MCP Path', default=None)
    parser.add_option('-b', '--build', action='store', dest='build', help='Build number', default=None)
    parser.add_option('-s', '--skipchangelog', action='store_true', dest='skip_changelog', help='Skip Changelog', default=False)
    parser.add_option('-j', '--sign-jar', action='store', dest='sign_jar', help='Path to jar signer command', default=None)
    options, _ = parser.parse_args()
    
    build_num = 0
    if not options.build is None:
        try:
            build_num = int(options.build)
        except:
            pass

    mcp_dir = os.path.join(forge_dir, 'mcp')
    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
        
    ret = 0
    fml_dir = os.path.join(forge_dir, 'fml')
    ret = build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num)
    if ret != 0:
        sys.exit(ret)
    
    
    temp_dir = os.path.join(forge_dir, 'temp')
    src_dir = os.path.join(mcp_dir, 'src')
    reobf_dir = os.path.join(mcp_dir, 'reobf')
    client_dir = os.path.join(reobf_dir, 'minecraft')
    fml_dir = os.path.join(temp_dir, 'fml')

    print '=================================== Release Start ================================='
    
    fml = glob.glob(os.path.join(forge_dir, 'fml', 'target', 'fml-src-*.%d-*.zip' % build_num))
    if not len(fml) == 1:
        if len(fml) == 0:
            print 'Missing FML source zip, should be named fml-src-*.zip inside ./fml/target/ created when running setup'
        else:
            print 'To many FML source zips found, we should only have one. Check the Forge Git for the latest FML version supported'
        sys.exit(1)
    
    if os.path.isdir(fml_dir):
        shutil.rmtree(fml_dir)
        
    print 'Extracting: %s' % os.path.basename(fml[0])
    zf = zipfile.ZipFile(fml[0])
    zf.extractall(temp_dir)
    zf.close()
    
    error_level = 0
    try:
        sys.path.append(mcp_dir)
        from runtime.reobfuscate import reobfuscate
        os.chdir(mcp_dir)
        reset_logger()
        reobfuscate(None, False, True, True, True, False, False)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        print 'Reobfusicate Exception: %d ' % e.code
        error_level = e.code
    
    extract_fml_obfed(fml_dir, mcp_dir, reobf_dir, client_dir)
    extract_paulscode(mcp_dir, client_dir)
    version = load_version(build_num)
    version_forge = '%d.%d.%d.%d' % (version['major'], version['minor'], version['revision'], version['build'])
    version_mc = load_mc_version(fml_dir)
    branch = get_branch_name()
    
    version_str = '%s-%s' % (version_mc, version_forge)
    if not branch == "":
        version_str = '%s-%s' % (version_str, branch)
        
    out_folder = os.path.join(forge_dir, 'forge-%s' % version_str)
    if os.path.isdir(out_folder):
        shutil.rmtree(out_folder)
        
    os.makedirs(out_folder)
    
#    options.skip_changelog = True #Disable till jenkins fixes its shit
    if not options.skip_changelog:
        changelog_file = 'forge-%s/minecraftforge-changelog-%s.txt' % (version_str, version_str)
        try:
            make_changelog("http://jenkins.minecraftforge.net:81/job/minecraftforge/", build_num, changelog_file, version_str)
        except HTTPError, e:
            print 'Changelog failed to generate: %s' % e
            options.skip_changelog = True
    
    version_file = 'forgeversion.properties'
    if os.path.exists(version_file):
        os.remove(version_file)
    
    with open(version_file, 'wb') as fh:
        fh.write('forge.major.number=%d\n' % version['major'])
        fh.write('forge.minor.number=%d\n' % version['minor'])
        fh.write('forge.revision.number=%d\n' % version['revision'])
        fh.write('forge.build.number=%d\n' % version['build'])
    
    if not options.sign_jar is None:
        sign_jar(forge_dir, options.sign_jar, client_dir, 'minecraftforge-universal-%s.zip' % version_str)
    else:
        zip_start('minecraftforge-universal-%s.zip' % version_str)
        zip_folder(client_dir, '', zip)
        zip_add('MANIFEST.MF','META-INF/MANIFEST.MF')
    zip_add('client/forge_logo.png')
    zip_add('install/MinecraftForge-Credits.txt')
    zip_add('install/MinecraftForge-License.txt')
    zip_add('install/Paulscode IBXM Library License.txt')
    zip_add('install/Paulscode SoundSystem CodecIBXM License.txt')
    zip_add('common/forge_at.cfg')
    zip_add(version_file)
    if not options.skip_changelog:
        zip_add(changelog_file, 'MinecraftForge-Changelog.txt')
    
    #Add dependancy and licenses from FML
    FML_FILES = [
        'CREDITS-fml.txt',
        'LICENSE-fml.txt',
        'README-fml.txt',
        'common/fml_at.cfg',
        'common/fml_marker.cfg',
        'common/fmlversion.properties',
        'common/mcpmod.info',
        'client/mcp.png'
    ]
    for file in FML_FILES:
        zip_add(os.path.join(fml_dir, file))
    
    zip_end()
    
    inject_version(os.path.join(forge_dir, 'common/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep)), build_num)
    zip_start('minecraftforge-src-%s.zip' % version_str, 'forge')
    zip_add('client',  'client')
    zip_add('common',  'common')
    zip_add('patches', 'patches')
    zip_add(fml_dir,    'fml')
    zip_add('install', '')
    zip_add('forge.py')
    zip_add(version_file)
    if not options.skip_changelog:
        zip_add(changelog_file, 'MinecraftForge-Changelog.txt')
    zip_end()
    inject_version(os.path.join(forge_dir, 'common/net/minecraftforge/common/ForgeVersion.java'.replace('/', os.sep)), 0)
    
    if os.path.exists(version_file):
        os.remove(version_file)
    shutil.rmtree(temp_dir)
    
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
            print '    ' + key
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
    
def load_mc_version(fml_dir):
    props = os.path.join(fml_dir, 'common', 'fmlversion.properties')
    
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

def extract_fml_obfed(fml_dir, mcp_dir, reobf_dir, client_dir):
    fml_file = os.path.join(fml_dir, 'difflist.txt')
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
    
def extract_paulscode(mcp_dir, client_dir):
    client = zipfile.ZipFile(os.path.join(mcp_dir, 'temp', 'client_reobf.jar'))
    
    print 'Extracting Reobfed Paulscode for mac users -.-'
    
    for i in client.filelist:
        if i.filename.startswith('paulscode'):
            if not os.path.isfile(os.path.join(client_dir, i.filename)):
                print '   %s' % i.filename
                client.extract(i.filename, client_dir)
        
    client.close()

def get_branch_name():
    from subprocess import Popen, PIPE, STDOUT
    branch = ''
    if os.getenv("GIT_BRANCH") is None:
        try:
            process = Popen(["git", "rev-parse", "--abbrev-ref", "HEAD"], stdout=PIPE, stderr=STDOUT, bufsize=-1)
            branch, _ = process.communicate()
            branch = branch.rstrip('\r\n')
        except OSError:
            print "Git not found"
    else:
        branch = os.getenv("GIT_BRANCH").rpartition('/')[2]
    branch = branch.replace('master', '')
    branch = branch.replace('HEAD', '')
    print 'Detected Branch as \'%s\'' % branch
    return branch

def sign_jar(forge_dir, command, files, dest_zip):
    from subprocess import Popen, PIPE, STDOUT
    global zip
    zip_file = os.path.join(forge_dir, 'tmp.jar')
    
    if os.path.isfile(zip_file):
        os.remove(zip_file)
    
    print '============== Creating tmp zip to sign ====================='
    zf = zipfile.ZipFile(zip_file, 'w', zipfile.ZIP_DEFLATED)
    zf.write(os.path.join(forge_dir, 'MANIFEST.MF'), 'META-INF/MANIFEST.MF')
    zip_folder_filter(files, '', zf, 'cpw/mods/'.replace('/', os.sep))
    zip_folder_filter(files, '', zf, 'net/minecraftforge/'.replace('/', os.sep))
    zf.close()
    print '================ End tmp zip to sign ========================'
    
    try:
        process = Popen([command, zip_file, "forge"], stdout=PIPE, stderr=STDOUT, bufsize=-1)
        out, _ = process.communicate()
        print out
    except OSError as e:
        print "Error creating signed tmp jar: %s" % e.strerror
        sys.exit(1)
    
    tmp_dir = os.path.join(forge_dir, 'tmp')
    if os.path.isdir(tmp_dir):
        shutil.rmtree(tmp_dir)
        
    zf = zipfile.ZipFile(zip_file)
    zf.extractall(tmp_dir)
    zf.close()
    os.remove(zip_file)
    
    zip_start(dest_zip)
    zip_folder(tmp_dir, '', zip)
    zip_folder(files, '', zip)
    
    if os.path.isdir(tmp_dir):
        shutil.rmtree(tmp_dir)

def zip_folder_filter(path, key, zip, filter):
    files = os.listdir(path)
    for file in files:
        file_path = os.path.join(path, file)
        file_key  = os.path.join(key, file)
        if os.path.isdir(file_path):
            zip_folder_filter(file_path, file_key, zip, filter)
        else:
            if file_key.startswith(filter):
                print file_key
                zip.write(file_path, file_key)

if __name__ == '__main__':
    main()
