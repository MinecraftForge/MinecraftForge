import os, os.path, sys
import shutil, fnmatch
import logging, zipfile, re
from optparse import OptionParser

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
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        mcp_dir = os.path.abspath('..')
        
    ret = 0
    fml_dir = os.path.join(forge_dir, 'fml')
    build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num)
    if ret != 0:
        sys.exit(ret)
    

    print '=================================== Release Start ================================='
    error_level = 0
    try:
        sys.path.append(mcp_dir)
        from runtime.reobfuscate import reobfuscate
        os.chdir(mcp_dir)
        reset_logger()
        reobfuscate(None, False, True, True, True, False)
        reset_logger()
        os.chdir(forge_dir)
    except SystemExit, e:
        print 'Reobfusicate Exception: %d ' % e.code
        error_level = e.code
    
    src_dir = os.path.join(mcp_dir, 'src')
    reobf_dir = os.path.join(mcp_dir, 'reobf')
    client_dir = os.path.join(reobf_dir, 'minecraft')
    
    extract_fml_obfed(mcp_dir, reobf_dir, client_dir)
    extract_paulscode(mcp_dir, client_dir)
    version = load_version(build_num)
    version_forge = '%d.%d.%d.%d' % (version['major'], version['minor'], version['revision'], version['build'])
    version_mc = load_mc_version(forge_dir)
    branch = get_branch_name()
    
    version_str = '%s-%s' % (version_mc, version_forge)
    if not branch == "":
        version_str = '%s-%s' % (version_str, branch)
        
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
    zip_add('install', '')
    zip_add('forge.py')
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

def extract_fml_obfed(mcp_dir, reobf_dir, client_dir):
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
    
if __name__ == '__main__':
    main()
