import os, os.path, sys, csv, re, fnmatch, shutil, zipfile, pprint
from optparse import OptionParser
from pprint import pprint

#This script attempts to rename local variables in a Fernflower decompiled Java source file to jad style names.
#Script originally written by Grum in perl, Converted to Python by monoxide, and bug fixes/cleanup by LexManos

class VarNamer:
    # Seems this is needed to keep these variables confined to the instance
    def __init__(self):
        self.last = {
          'byte':     [0, 0, ['b']],
          'char':     [0, 0, ['c']],
          'short':    [1, 0, ['short']],
          'int':      [0, 1, ['i', 'j', 'k', 'l']],
          'boolean':  [0, 1, ['flag']],
          'double':   [0, 0, ['d']],
          'float':    [0, 1, ['f']],
          'File':     [1, 1, ['file']],
          'String':   [0, 1, ['s']],
          'Class':    [0, 1, ['oclass']],
          'Long':     [0, 1, ['olong']],
          'Byte':     [0, 1, ['obyte']],
          'Short':    [0, 1, ['oshort']],
          'Boolean':  [0, 1, ['obool']],
          'Package':  [0, 1, ['opackage']]
        }
        self.remap = {
          'long': 'int',
        }

    def get_name(self, type, var):
        index = type if self.last.has_key(type) else (self.remap[type] if self.remap.has_key(type) else None)
        if (not index) and (re.search('^[A-Z]', type) or re.search(r'(\[|\.\.\.)', type)):
            type = type.replace('...', '[]')
            while type.find('[][]') != -1:
                type = type.replace('[][]', '[]')
 
            name = type.lower()
            skip = 1
 
            if re.search(r'\[', type):
                skip = 1
                name = "a" + name
                name = name.replace('[', '').replace(']', '').replace('...', '')
 
            self.last[type] = [0, skip, [name]]
            index = type
    
        if not index:
            print "No data for type: %s '%s'\n" % (type, var)
            return type
 
        id = self.last[index][0]
        skip_zero = self.last[index][1]
        data = self.last[index][2]
        self.last[index][0] += 1
 
        amount = len(data)
    
        if amount == 1:
            return data[0] + ('' if ((not id) and skip_zero) else ('%d' % id))
        else:
            num = id / amount
            return data[id % amount] + ('' if ((not num) and skip_zero) else ('%d' % num))
      
def rename_file(file, indent='3', MCP=False):
    tmp = file + '.tmp'
    with open(file, 'rb') as in_file:
        data = rename_class(in_file.read().replace('\r', ''), indent, MCP)
        with open(tmp, 'wb') as out_file:
            out_file.write(data)
    shutil.move(tmp, file)
    
def rename_class(data, indent='3', MCP=False):
    METHOD_REG = r'^ {%s}(\w+\s+\S.*\.*(|static )(?:\{|\);|})$' % indent
    CATCH_REG = r'catch \((.*)\) {'
    if MCP:
        if indent == '3':
            indent = '4'
        METHOD_REG = r'^ {%s}(\w+\s+\S.*\(.*|static)$' % indent
        CATCH_REG = r'catch \((.*)\)$'
        
    lines = data.split('\n')
    output = ''
 
    inside_method = False
    method = ''
    method_variables = []
    skip = False
 
    for line in lines:
        line += '\n'
        if re.search(METHOD_REG, line) and not re.search('=', line) and not re.search(r'\(.*\(', line):
            if re.search(r'\(.+\)', line):
                method_variables += [s.strip() for s in re.search(r'\((.+)\)', line).group(1).split(',')]

            method += line
     
            # Could be a single-line method
            skip = True
            if not re.search(r'(}|\);|throws .+?;)$', line):
                inside_method = True
     
        elif re.search(r'^ {%s}}$' % indent, line):
            inside_method = False
     
        if inside_method:
            if skip:
                skip = False
                continue
     
            method += line
     
            m = re.search(CATCH_REG, line)
            if m:
                method_variables += [m.group(1)]
            else:
                method_variables += map(lambda x: x.group(0), filter(
                        lambda x: not re.match(r'^(return)', x.group(0)) and not re.match(r'^(throw)', x.group(0)),
                        re.finditer(r'([a-z_$][a-z0-9_\[\]]+ var\d+)', line, re.I)))
            
        else:
            if method:
                namer = VarNamer()
                todo = map(lambda x: [x, namer.get_name(x.split(' ')[0], x)], method_variables)
                
                replace = {}
                for mapping in todo:
                    if not ' ' in mapping[0]:
                        continue
                    replace[mapping[0].split(' ')[1]] = mapping[1]
                    
                for k in sorted(replace, key=len, reverse=True):
                    original = k
                    to = replace[k]
                    
                    #Don't rename already renamed things.
                    if not re.match('var\d+', original):
                        continue
     
                    method = method.replace(original, to)
     
                output += method
     
                method = ''
                method_variables = []
     
            if skip:
                skip = False
                continue
     
            output += line
            
    return output[:-1]
            
def main(options, args):
    for arg in args:
        for path, _, filelist in os.walk(arg, followlinks=True):
            for cur_file in fnmatch.filter(filelist, '*.java'):
                file = os.path.normpath(os.path.join(path, cur_file))
                rename_file(file, options.indent, options.mcp)

if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp', action='store_true', dest='mcp', help='Use MCP regexs', default=False)
    parser.add_option('-i', '--indent', action='store', dest='indent', help='Custom indent to use', default='3')
    options, args = parser.parse_args()

    main(options, args)