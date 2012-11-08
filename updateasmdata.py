import os, os.path, sys
import urllib, zipfile
import shutil, glob, fnmatch
import subprocess, logging, re, shlex
import csv

print(os.getcwd())
sys.path.append('install')
from fml import gen_merged_srg
ctorsigre = re.compile('<init>\((.*)\)')
ctorparamre = re.compile('(([ZBCSIJFD]|L([\w\/]+);))')
common = gen_merged_srg('..',None)
rev_common = {t:{v.split(' ')[0]:k for k, v in m.items()} for t,m in common.items()}

if len(sys.argv) < 2:
  print('Give me a file to process please')
  sys.exit(1)
print(sys.argv, len(sys.argv))

if sys.argv[1]=='mcp_merge.cfg':
  print('Fixing mcp_merge.cfg')
  with open('mcp_merge.cfg-new','w') as nf:
    with open(sys.argv[1]) as f:
      for line in f:
        parts = line.split('#')
        target = parts[1].strip()
        newpart = parts[0][0]+rev_common['CL:'][target]+" #"+target+'\n'
	nf.write(newpart)
  sys.exit(0)
elif len(sys.argv)==2:
  with open(sys.argv[1]+'-new','w') as nf:
    with open(sys.argv[1]) as f:
      for line in f:
        parts = line.split('#')
        if len(parts[1])<4:
          nf.write(line)
          continue
        typ = parts[1][0:3]
        if not rev_common.has_key(typ):
          nf.write(line)
          continue
        name = parts[1][3:].strip()
        action = parts[0].split(' ')
        if name.endswith('/*'):
          targ = rev_common['CL:']['net/minecraft/src/'+name[:-2]]
          if typ == 'MD:':
            wildcard = '.*()'
          else:
            wildcard = '.*'
          newline = action[0]+' '+targ+wildcard+' #'+'#'.join(parts[1:])
          nf.write(newline)
        elif name.find('/<init>')>=0:
          targ = rev_common['CL:']['net/minecraft/src/'+name[:name.find('/<init>')]]
          args = '<init>('
          armatch = ctorsigre.search(name).group(1)
          for bit in ctorparamre.findall(armatch):
            if len(bit[2])>0 and rev_common['CL:'].has_key(bit[2]):
              cl = 'L'+rev_common['CL:'][bit[2]]+';'
            else:
              cl = bit[1]
            args+=cl
          args+=')V'
          print(line.strip())
          newline = action[0]+' '+targ+'.'+args+' #'+'#'.join(parts[1:])
          print(newline.strip())
          nf.write(newline)
        else:
          targ = rev_common[typ]['net/minecraft/src/'+name]
          newline = action[0]+' '+targ.replace('/','.',1).replace(' ','',1)+' #'+'#'.join(parts[1:])
          nf.write(newline)
else:
  typ = sys.argv[1]
  name = sys.argv[2]
  name = name.replace('.','/')
  if typ.startswith('-'):
    print(rev_common[typ[1:]][name])
  else:
    print(common[typ][name])

        
