import os, os.path, sys
import shutil, glob, fnmatch
import csv, re
from pprint import pprint
from zipfile import ZipFile
from optparse import OptionParser

def get_merged_info(mcp_dir):
    joined = os.path.join(mcp_dir, 'conf', 'joined.srg')
    values = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    
    if not os.path.isfile(joined):
        sys.path.append('install')
        from fml import gen_merged_srg
        values = gen_merged_srg(mcp_dir, None)
    else:
        with open(joined, 'r') as fh:
            for line in fh:
                pts = line.rstrip('\r\n').split(' ')
                if pts[0] == 'MD:':
                    values[pts[0]][pts[1] + ' ' + pts[2]] = pts[3] + ' ' + pts[4]
                else:
                    values[pts[0]][pts[1]] = pts[2]
    return {t:{v.split(' ')[0]:k for k, v in m.items()} for t,m in values.items()}

def main():
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store_true', dest='mcp_dir', help='Path to download/extract MCP to', default=None)
    options, _ = parser.parse_args()
    
    fml_dir = os.path.dirname(os.path.abspath(__file__))
    mcp_dir = os.path.abspath('mcp')
    
    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        mcp_dir = os.path.abspath('..')

    client_jar = os.path.join(mcp_dir, 'jars', 'bin', 'minecraft.jar.backup')
    server_jar = os.path.join(mcp_dir, 'jars', 'minecraft_server.jar.backup')
    
    server_classes = []
    client_classes = []
    
    zip = ZipFile(client_jar)
    for i in zip.filelist:
        if i.filename.endswith('.class') and i.filename.find('/') == -1:
            client_classes.append(i.filename[:-6])
    zip.close()
    
    zip = ZipFile(server_jar)
    for i in zip.filelist:
        if i.filename.endswith('.class') and i.filename.find('/') == -1:
            server_classes.append(i.filename[:-6])
    zip.close()

    srg = get_merged_info(mcp_dir)
    pkgs = {}    
    pkg_file = os.path.join('conf', 'packages.csv')
    
    if os.path.isfile(pkg_file):
        with open(pkg_file) as fh:
            reader = csv.DictReader(fh)
            for line in reader:
                pkgs[line['class']] = line['package']
    
    classes = []
    for cls in srg['CL:'].keys():
        if cls.startswith('net/minecraft/src/'):
            obf = srg['CL:'][cls]
            cls = cls[18:]
            classes.append(cls)
            if obf in server_classes:
                server_classes.remove(obf)
                server_classes.append(cls)
            if obf in client_classes:
                client_classes.remove(obf)
                client_classes.append(cls)
    
    for cls in pkgs.keys():
        if not cls in classes:
            print 'Removed Class: %s/%s' % (pkgs[cls], cls)
        
    for cls in classes:
        if not cls in pkgs.keys():
            print 'New Class: %s' % cls
            if cls.find('/') == -1:
                if cls in server_classes and cls in client_classes:
                    pkgs[cls] = 'get_me_out_of_here_shared'
                elif cls in server_classes:
                    pkgs[cls] = 'get_me_out_of_here_server'
                elif cls in client_classes:
                    pkgs[cls] = 'get_me_out_of_here_client'
                else:
                    pkgs[cls] = 'get_me_out_of_here_src'
    
    tmp=[]
    for cls,pkg in pkgs.items():
        tmp.append({'class': cls, 'package': pkg})
    
    with open(pkg_file, 'wb') as fh:
        writer = csv.DictWriter(fh, fieldnames=['class', 'package'], lineterminator='\n')
        writer.writeheader()
        for row in sorted(tmp, key=lambda x: (x['package'], x['class'])):
            writer.writerow(row)
    
if __name__ == '__main__':
    main()
        
