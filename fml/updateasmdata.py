import os, os.path, sys
import shutil, glob, fnmatch
import csv, re

ctorsigre = re.compile('<init>\((.*)\)')
ctorparamre = re.compile('(([ZBCSIJFD]|L([\w\/]+);))')

def get_merged_info():
    mcp_dir = os.path.join(os.getcwd(), 'mcp')
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

def process_file(file, srg):
    name = os.path.basename(file)
    print 'Processing: ' + name
    
    if name == 'mcp_merge.cfg':
        with open(file + '-new','w') as nf:
            with open(file) as f:
                for line in f:
                    parts = line.split('#')
                    target = parts[1].strip()
                    newpart = '%s%s #%s\n' % (parts[0][0], srg['CL:'][target], target)
                    nf.write(newpart)
    else:
        with open(file + '-new', 'wb') as nf:
            with open(file) as f:
                for line in f:
                    parts = line.split('#')
                    if len(parts) < 2:
                        nf.write(line)
                        continue
                    if len(parts[1]) < 4:
                        nf.write(line)
                        continue
                        
                    typ = parts[1][0:3]
                    
                    if not srg.has_key(typ):
                        nf.write(line)
                        continue
                        
                    name = parts[1][3:].strip()
                    name = (name if name.startswith('net/minecraft/') else 'net/minecraft/src/' + name)
                    action = parts[0].split(' ')
                    
                    if name.endswith('/*'):
                        targ = srg['CL:'][name[:-2]]
                        wildcard = ('.*()' if typ == 'MD:' else '.*')
                        
                        newline = '%s %s%s #%s' % (action[0], targ, wildcard, '#'.join(parts[1:]))
                        nf.write(newline)
                    
                    elif name.find('/<init>') >= 0:
                        targ = srg['CL:'][name[:name.find('/<init>')]]
                        args = '<init>('
                        armatch = ctorsigre.search(name).group(1)
                        for bit in ctorparamre.findall(armatch):
                            if len(bit[2]) > 0 and srg['CL:'].has_key(bit[2]):
                                cl = 'L' + srg['CL:'][bit[2]] + ';'
                            else:
                                cl = bit[1]
                            args += cl 
                        args += ')V'
                        newline = ('%s %s.%s #%s' % (action[0], targ, args, '#'.join(parts[1:])))
                        nf.write(newline)
                        
                    else:
                        if name not in srg[typ]:
                            nf.write("%s # -- MISSING MAPPING\n" %( line.rstrip() ))
                            print("%s is missing a mapping"% name)
                        else:
                            targ = srg[typ][name]
                            args = targ.replace('/', '.', 1).replace(' ', '', 1)
                            newline = ('%s %s #%s' % (action[0], args, '#'.join(parts[1:])))
                            nf.write(newline)
                    
def main():
    srg = get_merged_info()
    for arg in sys.argv:
        path = os.path.join(os.getcwd(), arg)
        
        if arg.endswith('_at.cfg') or arg == 'mcp_merge.cfg':
            process_file(path, srg)
        elif os.path.isdir(path):
            for file in os.listdir(path):
                if file.endswith('_at.cfg'):
                    process_file(os.path.join(path, file), srg)
            
if __name__ == '__main__':
    main()
        
