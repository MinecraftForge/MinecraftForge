import os, os.path, sys
import shutil, glob, fnmatch
import csv, re
from pprint import pprint
from zipfile import ZipFile
from optparse import OptionParser
from contextlib import closing

def get_merged_info(fml_dir, mcp_dir):
    joined = os.path.join(mcp_dir, 'conf', 'joined.srg')
    values = {'PK:': {}, 'CL:': {}, 'FD:': {}, 'MD:': {}}
    
    if not os.path.isfile(joined):
        sys.path.append(os.path.join(fml_dir, 'install'))
        from fml import gen_merged_srg
        values = create_merged_srg(mcp_dir, None)
    else:
        with closing(open(joined, 'r')) as fh:
            for line in fh:
                pts = line.rstrip('\r\n').split(' ')
                if pts[0] == 'MD:':
                    values[pts[0]][pts[1] + ' ' + pts[2]] = pts[3] + ' ' + pts[4]
                else:
                    values[pts[0]][pts[1]] = pts[2]
    return {t:{v.split(' ')[0]:k for k, v in m.items()} for t,m in values.items()}

def load_suggestions(fml_dir):
    map_file = os.path.join(fml_dir, 'map_client.txt')
    if not os.path.isfile(map_file):
        return None

    map = {}
    with closing(open(map_file, 'r')) as fh:
        for line in fh:
            if line[0] == ' ': 
                continue
            line = line[0:-2].split(' -> ')
            map[line[1]] = line[0].rsplit('.', 1)[0]
    return map

def main():
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='MCP install folder', default='mcp')
    parser.add_option('-f', '--fml-dir', action='store', dest='fml_dir', help='FML instlal folder', default='.')
    options, _ = parser.parse_args()
    
    sys.path.append(os.path.join(options.fml_dir, 'install'))
    
    from fml import read_mc_versions, load_srg
    info = read_mc_versions(options.fml_dir, work_dir=os.path.join(options.mcp_dir, 'jars'))
    suggs = load_suggestions(options.fml_dir)
    
    types = {'client' : [], 'server' : []}
    for type in ['client', 'server']:
        with closing(ZipFile(info['%s_file' % type])) as zip:
            for i in zip.filelist:
                if i.filename.endswith('.class') and i.filename.find('/') == -1:
                    types[type].append(i.filename[:-6])

    srg = get_merged_info(options.fml_dir, options.mcp_dir)
    pkgs = {}
    pkg_file = os.path.join('conf', 'packages.csv')
    
    if os.path.isfile(pkg_file):
        with closing(open(pkg_file)) as fh:
            reader = csv.DictReader(fh)
            for line in reader:
                pkgs[line['class']] = line['package']
    
    classes = {}
    for cls in srg['CL:'].keys():
        if cls.startswith('net/minecraft/src/'):
            obf = srg['CL:'][cls]
            cls = cls[18:]
            classes[cls] = obf
            for type in ['server', 'client']:
                if obf in types[type]:
                    types[type].remove(obf)
                    types[type].append(cls)
    
    for cls in pkgs.keys():
        if not cls in classes.keys():
            print 'Removed Class: %s/%s' % (pkgs[cls], cls)
            pkgs.pop(cls)
    
    for cls,obf in classes.items():
        if not cls in pkgs.keys():
            sug = ''
            if not suggs is None and obf in suggs.keys():
                sug = '_' + suggs[obf].replace('.', '_')
                
            print 'New Class: %s' % cls
            if cls.find('/') == -1:
                if cls in types['server'] and cls in types['client']:
                    pkgs[cls] = 'get_me_out_of_here_shared' + sug
                elif cls in types['server']:
                    pkgs[cls] = 'get_me_out_of_here_server' + sug
                elif cls in types['client']:
                    pkgs[cls] = 'get_me_out_of_here_client' + sug
                else:
                    pkgs[cls] = 'get_me_out_of_here_src' + sug
    
    tmp=[]
    for cls,pkg in pkgs.items():
        tmp.append({'class': cls, 'package': pkg})
    
    with closing(open(pkg_file, 'wb')) as fh:
        writer = csv.DictWriter(fh, fieldnames=['class', 'package'], lineterminator='\n')
        writer.writeheader()
        for row in sorted(tmp, key=lambda x: (x['package'], x['class'])):
            writer.writerow(row)
    
if __name__ == '__main__':
    main()
        
