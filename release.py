import os, os.path, sys, glob
import shutil, fnmatch, time, json
import logging, zipfile, re, subprocess
from pprint import pformat, pprint
from optparse import OptionParser
from urllib2 import HTTPError
from contextlib import closing
from datetime import datetime
sys.stdout = os.fdopen(sys.stdout.fileno(), 'w', 0)
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
    
    if os.path.isfile('MANIFEST.MF'):
        os.remove('MANIFEST.MF')
        
    fml_name = os.path.basename(fml[0]).replace('src', 'universal').replace('.zip', '.jar').replace('-master.', '.')
    print('Extracting %s MANIFEST.MF' % fml_name)
    with closing(zipfile.ZipFile(os.path.join(forge_dir, 'fml', 'target', fml_name), mode='r')) as zip_in:
        with closing(open('MANIFEST.MF', 'wb')) as out:
            out.write(zip_in.read('META-INF/MANIFEST.MF'))
    
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
    gen_bin_patches(mcp_dir, os.path.join(forge_dir, 'fml'), build_num, client_dir)
    
    version = load_version(build_num)
    version_forge = '%d.%d.%d.%d' % (version['major'], version['minor'], version['revision'], version['build'])
    version_mc = load_mc_version(fml_dir)
    branch = get_branch_name()
    
    version_str = '%s-%s' % (version_mc, version_forge)
    if not branch == "":
        version_str = '%s-%s' % (version_str, branch)
        
    out_folder = os.path.join(forge_dir, 'target')
    if not os.path.isdir(out_folder):    
        os.makedirs(out_folder)
    
    for f in ['minecraftforge-changelog-%s.txt', 'minecraftforge-universal-%s.jar', 'minecraftforge-installer-%s.jar']:
        fn = os.path.join(out_folder, f % version_str)
        if os.path.isfile(fn):
            os.remove(fn)
    
    if not options.skip_changelog:
        changelog_file = 'target/minecraftforge-changelog-%s.txt' % (version_str)
        try:
            make_changelog("http://ci.jenkins.minecraftforge.net/job/minecraftforge/", build_num, changelog_file, version_str)
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

    json_data = gather_json(forge_dir, version_mc, version_forge, version_str)
    
    if not options.sign_jar is None:
        sign_jar(forge_dir, options.sign_jar, client_dir, 'minecraftforge-universal-%s.jar' % version_str)
    else:
        zip_start('minecraftforge-universal-%s.jar' % version_str)
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
    print('    version.json')
    zip.writestr('version.json', json.dumps(json_data['versionInfo'], indent=4, separators=(',', ': ')))
    
    #Add dependancy and licenses from FML
    FML_FILES = [
        'CREDITS-fml.txt',
        'LICENSE-fml.txt',
        'README-fml.txt',
        'common/fml_at.cfg',
        'common/fml_marker.cfg',
        'common/fmlversion.properties',
        'common/mcpmod.info',
        'client/mcplogo.png',
        'common/deobfuscation_data-%s.lzma' % version_mc
    ]
    for file in FML_FILES:
        zip_add(os.path.join(fml_dir, file))
    
    zip_end()
    
    build_installer(forge_dir, version_str, version_forge, version_mc, out_folder, json.dumps(json_data, indent=4, separators=(',', ': ')))
    
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
    if os.path.isfile('MANIFEST.MF'):
        os.remove('MANIFEST.MF')
    
    print '=================================== Release Finished %d =================================' % error_level
    sys.exit(error_level)

def gather_json(forge_dir, version_mc, version_forge, version_str):
    def getTZ():
        ret = '-'
        t = time.timezone
        if (t < 0):
            ret = '+'
            t *= -1
        
        h = int(t/60/60)
        t -= (h*60*60)
        m = int(t/60)
        return '%s%02d%02d' % (ret, h, m)
    timestamp = datetime.now().replace(microsecond=0).isoformat() + getTZ()
    json_data = {}
    with closing(open(os.path.join(forge_dir, 'fml', 'jsons', '%s-rel.json' % version_mc), 'r')) as fh:
        data = fh.read()
        data = data.replace('@version@', version_forge)
        data = data.replace('@timestamp@', timestamp)
        data = data.replace('@minecraft_version@', version_mc)
        data = data.replace('@universal_jar@', 'minecraftforge-universal-%s.jar' % version_str)
        data = data.replace('FMLTweaker', 'F_M_L_Tweaker')
        data = data.replace('FML', 'Forge')
        data = data.replace('F_M_L_Tweaker', 'FMLTweaker')
        data = data.replace('cpw.mods:fml:', 'net.minecraftforge:minecraftforge:')
        json_data = json.loads(data)
    pprint(json_data)
    return json_data

def build_installer(forge_dir, version_str, version_forge, version_minecraft, out_folder, json_data):
    file_name = 'minecraftforge-installer-%s.jar' % version_str
    universal_name = 'minecraftforge-universal-%s.jar' % version_str
    print '================== %s Start ==================' % file_name
    with closing(zipfile.ZipFile(os.path.join(forge_dir, 'fml', 'installer_base.jar'), mode='a')) as zip_in:
        with closing(zipfile.ZipFile(os.path.join(out_folder, file_name), 'w', zipfile.ZIP_DEFLATED)) as zip_out:
            # Copy everything over
            for i in zip_in.filelist:
                if not i.filename in ['install_profile.json', 'big_logo.png']:
                    #print('    %s' % i.filename)
                    zip_out.writestr(i.filename, zip_in.read(i.filename))
            print('    %s' % universal_name)
            zip_out.write(os.path.join(out_folder, universal_name), universal_name)
            print('    big_logo.png')
            zip_out.write(os.path.join(forge_dir, 'client', 'forge_logo.png'), 'big_logo.png')
            print('    install_profile.json')
            zip_out.writestr('install_profile.json', json_data)
            
    print '================== %s Finished ==================' % file_name
    
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
    zip_file = os.path.join(forge_dir, 'target', name)
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

def gen_bin_patches(mcp_dir, fml_dir, build_num, client_dir):
    print('Creating Binary patches')
    os.environ['WORKSPACE'] = os.path.join(mcp_dir, '..')
    os.environ['BUILD_NUMBER'] = str(build_num)

    BUILD = ['ant', 'makebinpatches']
    if sys.platform.startswith('win'):
        BUILD = ['cmd', '/C'] + BUILD
    
    if not run_command(BUILD, cwd=fml_dir):
        print('Could not crate binary patches')
        sys.exit(1)
    
    fml_lzma = os.path.join(fml_dir, 'binpatches.pack.lzma')
    obf_lzma = os.path.join(client_dir, 'binpatches.pack.lzma')
    shutil.move(fml_lzma, obf_lzma)

def run_command(command, cwd='.', verbose=True):
    print('Running command: ')
    print(pformat(command))
        
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=1, cwd=cwd)
    while process.poll() is None:
        line = process.stdout.readline()
        if line:
            line = line.rstrip()
            print(line)
    if process.returncode:
        print "failed: {0}".format(process.returncode)
        return False
    return True   
        
if __name__ == '__main__':
    main()
