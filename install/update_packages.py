import os, os.path, sys, csv, re, fnmatch, shutil
from optparse import OptionParser

replaced = 0

def main(mcp_dir, targets):
    global replaced
    packages_csv = os.path.join(mcp_dir, 'conf', 'packages.csv')
    
    if not os.path.isfile(packages_csv):
        print 'Could not find Packages.csv, please specify a location of a MCP directory setup with FML using --mcp-dir <path>'
        sys.exit(1)
    
    pkgs = {}    
    with open(packages_csv) as fh:
        reader = csv.DictReader(fh)
        for line in reader:
            pkgs[line['class']] = line['package']
    
    if len(targets) == 0:
        print 'You must specify atleast one target directory to repackage'
        sys.exit(1)
    
    regnms = re.compile(r'net.minecraft.src.(\w+)')
    total_r = 0
    total_f = 0
    
    for target in targets:        
        for path, _, filelist in os.walk(target, followlinks=True):
            for cur_file in fnmatch.filter(filelist, '*.java'):
                file_name = os.path.join(path, cur_file)
                buf = ''
                replaced = 0
                
                with open(file_name, 'rb') as fh:
                    data = fh.read()
                    def mapname(match):
                        global replaced
                        cls = match.group(0)
                        tmp = cls[18:]
                        if tmp in pkgs.keys():
                            replaced += 1
                            return ('%s/%s' % (pkgs[tmp], tmp)).replace('/', '.')
                        return cls
                    data = regnms.sub(mapname, data)
                
                if replaced > 0:
                    total_r += replaced
                    total_f += 1
                    
                    print '%s: %d' % (os.path.relpath(file_name), replaced)
                    
                    with open(file_name + '.tmp', 'wb') as fh:
                        fh.write(data)
                    shutil.move(file_name + '.tmp', file_name)
                    
    print 'Fixed %d references in %d files' % (total_r, total_f)
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='MCP Directory', default='mcp')
    options, args = parser.parse_args()

    main(os.path.abspath(options.mcp_dir), args)